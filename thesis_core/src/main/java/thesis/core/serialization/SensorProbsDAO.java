package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.sensors.SensorProbs;
import thesis.core.utilities.LoggerIDs;

public class SensorProbsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_target_probs";
   private final String snsrTypeColName = "SensorType";
   private final String tgtTypeColName = "TargetType";
   private final String probDetectColName = "ProbDetect";
   private final String probConfirmColName = "ProbConfirm";

   public SensorProbsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, SensorProbs snsrProbs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(snsrTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(tgtTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probDetectColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(probConfirmColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(snsrTypeColName + "," + tgtTypeColName + "," + probDetectColName + "," + probConfirmColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
         {
            int snsrTypeID = rs.getInt(snsrTypeColName);
            int tgtTypeID = rs.getInt(tgtTypeColName);
            float probDetect = rs.getFloat(probDetectColName);
            float probConfirm = rs.getFloat(probConfirmColName);

            snsrProbs.setSensorConfirmProb(snsrTypeID, tgtTypeID, probConfirm);
            snsrProbs.setSensorDetectProb(snsrTypeID, tgtTypeID, probDetect);
         }
         rs.close();

         stmt.close();

         logger.info("Loaded sensor probabilities.");
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor probabilities. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
