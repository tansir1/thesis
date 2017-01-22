package thesis.core.serialization.entities;

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

public class SensorProbsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_target_probs";
   private final String snsrTypeColName = "SensorType";
   private final String tgtTypeColName = "TargetType";
   private final String probDetectColName = "ProbDetect";
   private final String hdgCoefColName = "HdgCoeff";

   private Connection dbCon;

   public SensorProbsDAO(Connection dbCon)
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
         initTblSQL.append(tgtTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probDetectColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(hdgCoefColName);
         initTblSQL.append(" double not null");
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
         while(rs.next())
         {
            int snsrTypeID = rs.getInt(snsrTypeColName);
            int tgtTypeID = rs.getInt(tgtTypeColName);
            double probDetect = rs.getDouble(probDetectColName);
            double hdgCoeff = rs.getDouble(hdgCoefColName);

            snsrProbs.setSensorDetectTgtProb(snsrTypeID, tgtTypeID, probDetect);
            snsrProbs.setSensorHeadingCoeff(snsrTypeID, tgtTypeID, hdgCoeff);
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
         sql.append(tgtTypeColName);
         sql.append(",");
         sql.append(probDetectColName);
         sql.append(",");
         sql.append(hdgCoefColName);
         sql.append(") values (?,?,?,?,?)");


         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         int numSnsrTypes = snsrProbs.getNumSensorTypes();
         int numTgtTypes = snsrProbs.getNumTargetTypes();

         for (int i = 0; i < numSnsrTypes; ++i)
         {
            for (int j = 0; j < numTgtTypes; ++j)
            {
               stmt.setInt(1, i);
               stmt.setInt(2, j);
               stmt.setDouble(3, snsrProbs.getSensorDetectTgtProb(i, j));
               stmt.setDouble(4, snsrProbs.getSensorHeadingCoeff(i, j));
               stmt.addBatch();
            }
         }

         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save sensor probabilities to db. Details: {}", e.getMessage());
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
         initTblSQL.append(tgtTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probDetectColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(hdgCoefColName);
         initTblSQL.append(" double not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(snsrTypeColName + "," + tgtTypeColName + "," + probDetectColName + "," + hdgCoefColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor probabilities from csv. Details: {}", e.getMessage());
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
}
