package thesis.core.serialization.world;

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
import thesis.core.common.RoadNetwork;
import thesis.core.utilities.LoggerIDs;

public class RoadNetworkDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME;
   private final String rowsColName = "row";
   private final String colsColName = "col";

   private Connection dbCon;

   public RoadNetworkDAO(Connection dbCon, String worldName)
   {
      this.dbCon = dbCon;
      TBL_NAME = "roadnet_" + worldName + "_cfg";
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
         initTblSQL.append(rowsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(colsColName);
         initTblSQL.append(" int not null");
         initTblSQL.append(");");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create road network configs table. Details: {}", e.getMessage());
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
         initTblSQL.append(rowsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(colsColName);
         initTblSQL.append(" int not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(rowsColName + "," + colsColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load road network configs. Details: {}", e.getMessage());
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
         logger.error("Failed to save road network configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadData(RoadNetwork roadNet)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int row = rs.getInt(rowsColName);
            int col = rs.getInt(colsColName);
            roadNet.setTraversable(row, col, true);
         }

         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load road network configs from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(RoadNetwork roadNet)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(rowsColName);
         sql.append(",");
         sql.append(colsColName);
         sql.append(") values (?,?)");


         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         List<CellCoordinate> traversable = roadNet.getTraversableCells();
         for(CellCoordinate cell : traversable)
         {
            stmt.setInt(1, cell.getRow());
            stmt.setInt(2, cell.getColumn());
            stmt.addBatch();
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save road network configs to db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
