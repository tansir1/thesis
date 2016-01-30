package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.targets.TargetTypeConfigs;
import thesis.core.utilities.LoggerIDs;

public class TargetTypeConfigsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "target_type_cfgs";
   private final String typeColName = "TargetType";
   private final String angleColName = "BestAngle";
   private final String spdColName = "MaxSpd";

   private Connection dbCon;

   public TargetTypeConfigsDAO(Connection dbCon)
   {
      this.dbCon = dbCon;
   }

   public boolean loadData(TargetTypeConfigs tgtTypeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);
         rs.next();
         int numTgtTypes = rs.getInt(1);
         logger.info("Loading {} target types.", numTgtTypes);
         rs.close();

         tgtTypeCfgs.reset(numTgtTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int typeID = rs.getInt(typeColName);
            float spd = rs.getFloat(spdColName);
            float angle = rs.getFloat(angleColName);

            tgtTypeCfgs.setTargetData(typeID, spd, angle);
         }
         rs.close();
         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load target type configs from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(TargetTypeConfigs tgtTypeCfgs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(typeColName);
         sql.append(",");
         sql.append(angleColName);
         sql.append(",");
         sql.append(spdColName);
         sql.append(") values (?,?,?)");

         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());
         int numTgtTypes = tgtTypeCfgs.getNumTypes();

         for (int i = 0; i < numTgtTypes; ++i)
         {
            stmt.setInt(1, i);
            stmt.setFloat(2, tgtTypeCfgs.getBestAngle(i));
            stmt.setFloat(3, tgtTypeCfgs.getSpeed(i));
            stmt.addBatch();
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save target type configs to db. Details: {}", e.getMessage());
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
         initTblSQL.append(" int not null,");
         initTblSQL.append(angleColName);
         initTblSQL.append(" real not null,");
         initTblSQL.append(spdColName);
         initTblSQL.append(" real not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(typeColName + "," + angleColName + "," + spdColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load target type configs from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save target type configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
