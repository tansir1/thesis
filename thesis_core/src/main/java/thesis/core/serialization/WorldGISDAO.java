package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
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

   public WorldGISDAO(String worldName)
   {
      TBL_NAME = "worldgis_" + worldName + "_cfg";
   }

   public boolean loadCSV(Connection dbCon, File csvFile, WorldGIS gis)
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
         logger.error("Failed to load weapon type configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
