package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.weapons.WeaponTypeConfigs;

public class WeaponTypeConfigsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "weapon_types_cfg";
   private final String typeColName = "WeaponType";
   private final String fovColName = "fov";
   private final String minRngColName = "minRng";
   private final String maxRngColName = "maxRng";

   public WeaponTypeConfigsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, WeaponTypeConfigs wpnTypeCfgs)
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
         initTblSQL.append(") as select ");
         initTblSQL.append(typeColName + "," + fovColName + "," + minRngColName + "," + maxRngColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select count(*) from " + TBL_NAME);
         rs.next();
         int numWpnTypes = rs.getInt(1);
         logger.info("Loading {} weapon types.", numWpnTypes);
         rs.close();

         wpnTypeCfgs.reset(numWpnTypes);

         rs = stmt.executeQuery("select * from " + TBL_NAME);
         while(rs.next())
         {
            int typeID = rs.getInt(typeColName);
            float fov = rs.getFloat(fovColName);
            double minRng = rs.getDouble(minRngColName);
            double maxRng = rs.getDouble(maxRngColName);

            wpnTypeCfgs.setWeaponData(typeID, fov, minRng, maxRng);
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
