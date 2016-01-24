package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
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

   public SensorTypeConfigsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, SensorTypeConfigs snsrTypeCfgs)
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
         logger.error("Failed to load sensor type configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
