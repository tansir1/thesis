package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.weapons.WeaponProbs;

public class WeaponProbsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_target_probs";
   private final String wpnTypeColName = "WeaponType";
   private final String tgtTypeColName = "TargetType";
   private final String probDestroyColName = "ProbDestroy";

   public WeaponProbsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, WeaponProbs wpnProbs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(wpnTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(tgtTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probDestroyColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(") as select ");
         initTblSQL.append(wpnTypeColName + "," + tgtTypeColName + "," + probDestroyColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
         {
            int wpnTypeID = rs.getInt(wpnTypeColName);
            int tgtTypeID = rs.getInt(tgtTypeColName);
            float probDestroy = rs.getFloat(probDestroyColName);

            wpnProbs.setWeaponDestroyProb(wpnTypeID, tgtTypeID, probDestroy);
         }
         rs.close();

         stmt.close();

         logger.info("Loaded weapon probabilities.");
      }
      catch (SQLException e)
      {
         logger.error("Failed to load weapon probabilities. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
