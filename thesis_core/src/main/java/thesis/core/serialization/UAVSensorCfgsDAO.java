package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.uav.UAVSensorCfgs;
import thesis.core.utilities.LoggerIDs;

public class UAVSensorCfgsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "uav_sensors_cfg";
   private final String uavTypeColName = "UAVType";
   private final String snsrTypeColName = "SensorType";

   public UAVSensorCfgsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, UAVSensorCfgs typeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
         stmt.execute("drop table if exists " + TBL_NAME);

         StringBuilder initTblSQL = new StringBuilder("create table ");
         initTblSQL.append(TBL_NAME);
         initTblSQL.append("(");
         initTblSQL.append(uavTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(snsrTypeColName);
         initTblSQL.append(" tinyint not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(uavTypeColName + "," + snsrTypeColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int uavID = rs.getInt(uavTypeColName);
            int snsrID = rs.getInt(snsrTypeColName);

            typeCfgs.addSensorToUAV(uavID, snsrID);
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
