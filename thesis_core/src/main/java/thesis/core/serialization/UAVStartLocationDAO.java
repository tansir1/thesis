package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.CellCoordinate;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class UAVStartLocationDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME;
   private final String typeColName = "uavType";
   private final String rowColName = "row";
   private final String colColName = "col";
   private final String hdgColName = "heading";

   private Connection dbCon;

   public UAVStartLocationDAO(Connection dbCon, String worldName)
   {
      this.dbCon = dbCon;
      TBL_NAME = "uavstart_" + worldName + "_cfg";
   }

   public boolean createTable()
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(typeColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(rowColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(colColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(hdgColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(");");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create uav start locations table. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadData(List<UAVStartCfg> uavStartCfgs, WorldGIS gis)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();

         UAVStartCfg cfg = null;
         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int typeID = rs.getInt(typeColName);
            int col = rs.getInt(colColName);
            int row = rs.getInt(rowColName);
            float hdg = rs.getFloat(hdgColName);

            cfg = new UAVStartCfg();
            cfg.setUAVType(typeID);
            cfg.setOrientation(hdg);
            gis.convertCellToWorld(row, col, cfg.getLocation());
            uavStartCfgs.add(cfg);
         }
         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load uav start locations from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(List<UAVStartCfg> uavStartCfgs, WorldGIS gis)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(typeColName);
         sql.append(",");
         sql.append(rowColName);
         sql.append(",");
         sql.append(colColName);
         sql.append(",");
         sql.append(hdgColName);
         sql.append(") values (?,?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         CellCoordinate cell = new CellCoordinate();
         for (UAVStartCfg uav : uavStartCfgs)
         {
            gis.convertWorldToCell(uav.getLocation(), cell);
            stmt.setInt(1, uav.getUAVType());
            stmt.setInt(2, cell.getRow());
            stmt.setInt(3, cell.getColumn());
            stmt.setFloat(4, uav.getOrientation());
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to uav target start locations to db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadCSV(File csvFile)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(typeColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(rowColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(colColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(hdgColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(typeColName + "," + rowColName + "," + colColName + "," + hdgColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load uav start locations from csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean writeCSV(File csvFile)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         StringBuilder sql = new StringBuilder("call csvwrite('");
         sql.append(csvFile.getAbsolutePath());
         sql.append("', 'select * from ");
         sql.append(TBL_NAME);
         sql.append("');");
         if (!stmt.execute(sql.toString()))
         {
            logger.error("Failed to export into csv file.");
         }

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save uav start locations to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
