package thesis.core.serialization;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

   private Connection dbCon;

   public UAVWeaponCfgsDAO(Connection dbCon)
   {
      this.dbCon = dbCon;
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


   public boolean loadData(UAVWeaponCfgs typeCfgs)
   {
      boolean success = true;
      try
      {
         Statement stmt = dbCon.createStatement();
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
         logger.error("Failed to load UAV/weapon loadout configs from db. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }

   public boolean saveData(UAVWeaponCfgs typeCfgs)
   {
      boolean success = true;
      try
      {
         StringBuilder sql = new StringBuilder("insert into ");
         sql.append(TBL_NAME);
         sql.append("(");
         sql.append(uavTypeColName);
         sql.append(",");
         sql.append(wpnTypeColName);
         sql.append(",");
         sql.append(initQtyTypeColName);
         sql.append(") values (?,?,?)");


         PreparedStatement stmt = dbCon.prepareStatement(sql.toString());
         int numUAVs = typeCfgs.getNumUAVTypes();
         int numWpns = typeCfgs.getNumWeaponTypes();
         for(int i=0; i<numUAVs; ++i)
         {
            for(int j=0; j<numWpns; j++)
            {
               if(typeCfgs.uavHasWeapon(i, j))
               {
                  stmt.setInt(1, i);
                  stmt.setInt(2, j);
                  stmt.setInt(3, typeCfgs.getInitialQuantity(i, j));
                  stmt.addBatch();
               }
            }
         }
         stmt.executeBatch();

         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to save UAV/weapon loadout configs to db. Details: {}", e.getMessage());
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


         stmt.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to load UAV/weapon loadout configs from csv. Details: {}", e.getMessage());
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
         logger.error("Failed to save UAV/weapon loadout configs to csv. Details: {}", e.getMessage());
         success = false;
      }
      return success;
   }
}
