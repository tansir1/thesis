package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
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

   public TargetTypeConfigsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, TargetTypeConfigs tgtTypeCfgs)
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

         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);

         int numTgtTypes = rs.getInt("count(*)");
         rs.close();

         tgtTypeCfgs.reset(numTgtTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
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
         logger.error("Failed to load target type configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
