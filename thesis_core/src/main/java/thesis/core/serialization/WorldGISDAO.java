package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class WorldGISDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME;
   private final String numRowsColName = "rows";
   private final String numColsColName = "cols";
   private final String widthColName = "width";
   private final String heightColName = "height";

   private Connection dbCon;

   public WorldGISDAO(Connection dbCon, String worldName)
   {
      this.dbCon = dbCon;
      TBL_NAME = "worldgis_" + worldName + "_cfg";
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
         initTblSQL.append(numRowsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(numColsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(widthColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(heightColName);
         initTblSQL.append(" int not null");
         initTblSQL.append(")");
         stmt.execute(initTblSQL.toString());;

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create GIS configs table. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadData(WorldGIS gis)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         rs.next();// Should only be a single row

         int numCols = rs.getInt(numRowsColName);
         int numRows = rs.getInt(numColsColName);
         int width = rs.getInt(widthColName);
         int height = rs.getInt(heightColName);

         gis.reset(width, height, numRows, numCols);

         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load GIS config from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(WorldGIS gis)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(numRowsColName);
         sql.append(",");
         sql.append(numRowsColName);
         sql.append(",");
         sql.append(widthColName);
         sql.append(",");
         sql.append(heightColName);
         sql.append(") values (?,?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         stmt.setInt(1, gis.getRowCount());
         stmt.setInt(2, gis.getColumnCount());
         stmt.setDouble(3, gis.getWidth());
         stmt.setDouble(4, gis.getHeight());
         stmt.execute();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save GIS configs to db. Details: {}", e.getMessage());
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
         initTblSQL.append(numRowsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(numColsColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(widthColName);
         initTblSQL.append(" int not null,");
         initTblSQL.append(heightColName);
         initTblSQL.append(" int not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(numRowsColName + "," + numColsColName + "," + widthColName + "," + heightColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load GIS configs from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save GIS configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
