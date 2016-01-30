package thesis.core.serialization.entities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.sensors.SensorTypeConfigs;
import thesis.core.utilities.LoggerIDs;

public class SensorTypeConfigsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "sensor_types_cfg";
   private final String typeColName = "SensorType";
   private final String fovColName = "fov";
   private final String minRngColName = "minRng";
   private final String maxRngColName = "maxRng";
   private final String slewRtColName = "slewRate";

   private Connection dbCon;

   public SensorTypeConfigsDAO(Connection dbCon)
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
         initTblSQL.append(typeColName);
         initTblSQL.append(" tinyint primary key not null,");
         initTblSQL.append(fovColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(minRngColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(maxRngColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(slewRtColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(");");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to create sensor types table. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean loadCSV(Connection dbCon, File csvFile, SensorTypeConfigs snsrTypeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);





         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor type configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }


   public boolean loadData(SensorTypeConfigs snsrTypeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);
         rs.next();
         int numSnsrTypes = rs.getInt(1);
         logger.info("Loading {} sensor types.", numSnsrTypes);
         rs.close();

         snsrTypeCfgs.reset(numSnsrTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
         {
            int typeID = rs.getInt(typeColName);
            float fov = rs.getFloat(fovColName);
            double minRng = rs.getDouble(minRngColName);
            double maxRng = rs.getDouble(maxRngColName);
            float slewRt = rs.getFloat(slewRtColName);

            snsrTypeCfgs.setSensorData(typeID, fov, minRng, maxRng, slewRt);
         }
         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor type configs from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(SensorTypeConfigs snsrTypeCfgs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(typeColName);
         sql.append(",");
         sql.append(fovColName);
         sql.append(",");
         sql.append(minRngColName);
         sql.append(",");
         sql.append(maxRngColName);
         sql.append(",");
         sql.append(slewRtColName);
         sql.append(") values (?,?,?,?,?)");


         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());

         int numTypes = snsrTypeCfgs.getNumTypes();
         for(int i=0; i<numTypes; ++i)
         {
            stmt.setInt(1, i);
            stmt.setFloat(2, snsrTypeCfgs.getFOV(i));
            stmt.setDouble(3, snsrTypeCfgs.getMinRange(i));
            stmt.setDouble(4, snsrTypeCfgs.getMaxRange(i));
            stmt.setFloat(5, snsrTypeCfgs.getSlewRate(i));
            stmt.addBatch();
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save sensor type configs to db. Details: {}", e.getMessage());
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
         initTblSQL.append(typeColName);
         initTblSQL.append(" tinyint primary key not null,");
         initTblSQL.append(fovColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(minRngColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(maxRngColName);
         initTblSQL.append(" double not null,");
         initTblSQL.append(slewRtColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(typeColName + "," + fovColName + "," + minRngColName + "," + maxRngColName + "," + slewRtColName+ " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load sensor type configs from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save sensor type configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
