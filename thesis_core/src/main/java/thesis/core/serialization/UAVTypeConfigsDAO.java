package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.uav.UAVTypeConfigs;
import thesis.core.utilities.LoggerIDs;

public class UAVTypeConfigsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "uav_types_cfg";
   private final String typeColName = "UAVType";
   private final String turnRadColName = "turnRadius";
   private final String spdColName = "speed";

   private Connection dbCon;

   public UAVTypeConfigsDAO(Connection dbCon)
   {
      this.dbCon = dbCon;
   }

   public boolean loadData(UAVTypeConfigs uavTypeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();

         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);
         rs.next();
         int numTypes = rs.getInt(1);
         logger.info("Loading {} UAV types.", numTypes);
         rs.close();

         uavTypeCfgs.reset(numTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int typeID = rs.getInt(typeColName);
            float spd = rs.getFloat(spdColName);
            float turnRadius = rs.getFloat(turnRadColName);

            uavTypeCfgs.setUAVData(typeID, spd, turnRadius);
         }
         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load weapon type configs from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(UAVTypeConfigs uavTypeCfgs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(typeColName);
         sql.append(",");
         sql.append(turnRadColName);
         sql.append(",");
         sql.append(spdColName);
         sql.append(") values (?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());
         int numUAVTypes = uavTypeCfgs.getNumTypes();

         for (int i = 0; i < numUAVTypes; ++i)
         {
            stmt.setInt(1, i);
            stmt.setFloat(2, uavTypeCfgs.getMaxTurnRt(i));
            stmt.setFloat(3, uavTypeCfgs.getSpeed(i));
            stmt.addBatch();
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save weapon type configs to db. Details: {}", e.getMessage());
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
         initTblSQL.append(turnRadColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(spdColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(") as select ");
         initTblSQL.append(typeColName + "," + turnRadColName + "," + spdColName  + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load weapon type configs from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save weapon type configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

}
