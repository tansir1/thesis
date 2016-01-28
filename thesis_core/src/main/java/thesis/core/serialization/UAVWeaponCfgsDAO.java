package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.uav.UAVWeaponCfgs;
import thesis.core.utilities.LoggerIDs;

public class UAVWeaponCfgsDAO
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private final String TBL_NAME = "uav_weapons_cfg";
   private final String uavTypeColName = "UAVType";
   private final String wpnTypeColName = "WeaponType";
   private final String initQtyTypeColName = "InitQty";

   public UAVWeaponCfgsDAO()
   {

   }

   public boolean loadCSV(Connection dbCon, File csvFile, UAVWeaponCfgs typeCfgs)
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
         initTblSQL.append(wpnTypeColName);
         initTblSQL.append(" tinyint not null,");
         initTblSQL.append(initQtyTypeColName);
         initTblSQL.append(" tinyint not null");
         initTblSQL.append(") as select ");
         initTblSQL.append(uavTypeColName + "," + wpnTypeColName + "," + initQtyTypeColName + " ");
         initTblSQL.append("from csvread('");
         initTblSQL.append(csvFile.getAbsolutePath());
         initTblSQL.append("');");
         stmt.execute(initTblSQL.toString());

         ResultSet rs = stmt.executeQuery("select * from " + TBL_NAME);
         while (rs.next())
         {
            int uavID = rs.getInt(uavTypeColName);
            int wpnID = rs.getInt(wpnTypeColName);
            int initQty = rs.getInt(initQtyTypeColName);

            typeCfgs.addWeaponToUAV(uavID, wpnID, initQty);
         }
         rs.close();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load uav/weapon loadout configs. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
