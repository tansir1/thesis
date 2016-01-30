package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.sensors.SensorProbs;
import thesis.core.utilities.LoggerIDs;

public class SensorMisclassifyProbsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_misclass_target_probs";
   private final String snsrTypeColName = "SensorType";
   private final String detectTypeColName = "DtctTgtType";
   private final String misclassTypeColName = "MisclassTgtType";
   private final String probColName = "Prob";

   private Connection dbCon;

   public SensorMisclassifyProbsDAO(Connection dbCon)
   {
      this.dbCon = dbCon;
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
         initTblSQL.append(snsrTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(detectTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(misclassTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(");");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create sensor probabilities table. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadData(SensorProbs snsrProbs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int snsrTypeID = rs.getInt(snsrTypeColName);
            int detectTypeID = rs.getInt(detectTypeColName);
            int misclassTypeID = rs.getInt(misclassTypeColName);
            float prob = rs.getFloat(probColName);

            snsrProbs.setSensorMisclassifyProb(snsrTypeID, detectTypeID, misclassTypeID, prob);
         }
         rs.close();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor probabilities from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(SensorProbs snsrProbs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(snsrTypeColName);
         sql.append(",");
         sql.append(detectTypeColName);
         sql.append(",");
         sql.append(misclassTypeColName);
         sql.append(",");
         sql.append(probColName);
         sql.append(") values (?,?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         int numSnsrTypes = snsrProbs.getNumSensorTypes();
         int numTgtTypes = snsrProbs.getNumTargetTypes();

         for (int i = 0; i < numSnsrTypes; ++i)
         {
            for (int j = 0; j < numTgtTypes; ++j)
            {
               for (int k = 0; k < numTgtTypes; ++k)
               {
                  stmt.setInt(1, i);
                  stmt.setInt(2, j);
                  stmt.setInt(3, k);
                  stmt.setFloat(4, snsrProbs.getSensorMisclassifyProb(i, j, k));
                  stmt.addBatch();
               }
            }
         }

         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save haven configs to db. Details: {}", e.getMessage());
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
         logger.error("Failed to save sensor probabilities to csv. Details: {}", e.getMessage());
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
         initTblSQL.append(snsrTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(detectTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(misclassTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(") as select ");
         initTblSQL
               .append(snsrTypeColName + "," + detectTypeColName + "," + misclassTypeColName + "," + probColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

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
