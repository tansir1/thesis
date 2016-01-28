package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.entities.uav.UAVTypeConfigs;
import thesis.core.utilities.LoggerIDs;

public class UAVTypeConfigsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "uav_types_cfg";
   private final String typeColName = "UAVType";
   private final String turnRadColName = "turnRadius";
   private final String spdColName = "speed";

   public UAVTypeConfigsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, UAVTypeConfigs uavTypeCfgs)
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
         logger.error("Failed to load weapon type configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
