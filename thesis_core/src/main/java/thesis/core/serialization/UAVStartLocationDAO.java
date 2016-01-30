package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

   public UAVStartLocationDAO(String worldName)
   {
      TBL_NAME = "uavstart_" + worldName + "_cfg";
   }

   public boolean loadCSV(Connection dbCon, File csvFile, List<UAVStartCfg> uavStartCfgs, WorldGIS gis)
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

         UAVStartCfg cfg = null;
         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
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
         logger.error("Failed to load uav start locations. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
