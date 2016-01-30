package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class TargetStartLocationDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME;
   private final String typeColName = "tgtType";
   private final String rowColName = "row";
   private final String colColName = "col";
   private final String hdgColName = "heading";

   public TargetStartLocationDAO(String worldName)
   {
      TBL_NAME = "tgtstart_" + worldName + "_cfg";
   }

   public boolean loadCSV(Connection dbCon, File csvFile, List<TargetStartCfg> tgtStartCfgs, WorldGIS gis)
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

         TargetStartCfg cfg = null;
         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
         {
            int typeID = rs.getInt(typeColName);
            int col = rs.getInt(colColName);
            int row = rs.getInt(rowColName);
            float hdg = rs.getFloat(hdgColName);

            cfg = new TargetStartCfg();
            cfg.setTargetType(typeID);
            cfg.setOrientation(hdg);
            gis.convertCellToWorld(row, col, cfg.getLocation());
            tgtStartCfgs.add(cfg);
         }
         rs.close();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load target start locations. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
