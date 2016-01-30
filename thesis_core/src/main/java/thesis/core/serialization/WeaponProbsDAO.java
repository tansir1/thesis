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
import thesis.core.weapons.WeaponProbs;

public class WeaponProbsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_target_probs";
   private final String wpnTypeColName = "WeaponType";
   private final String tgtTypeColName = "TargetType";
   private final String probDestroyColName = "ProbDestroy";

   private Connection dbCon;

   public WeaponProbsDAO(Connection dbCon)
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
         initTblSQL.append(wpnTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(tgtTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(probDestroyColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(")");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create weapon probabilities table. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadData(WeaponProbs wpnProbs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int wpnTypeID = rs.getInt(wpnTypeColName);
            int tgtTypeID = rs.getInt(tgtTypeColName);
            float probDestroy = rs.getFloat(probDestroyColName);

            wpnProbs.setWeaponDestroyProb(wpnTypeID, tgtTypeID, probDestroy);
         }
         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load weapon probabilities from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(WeaponProbs wpnProbs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(wpnTypeColName);
         sql.append(",");
         sql.append(tgtTypeColName);
         sql.append(",");
         sql.append(probDestroyColName);
         sql.append(") values (?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         int numWpns = wpnProbs.getNumWeaponTypes();
         int numTgts = wpnProbs.getNumTargetTypes();
         for (int i = 0; i < numWpns; ++i)
         {
            for (int j = 0; j < numTgts; ++j)
            {
               stmt.setInt(1, i);
               stmt.setInt(2, j);
               stmt.setFloat(3, wpnProbs.getWeaponDestroyProb(i, j));
               stmt.addBatch();
            }
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save weapon probabilities to db. Details: {}", e.getMessage());
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

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load weapon probabilities from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save weapon probabilities to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

}
