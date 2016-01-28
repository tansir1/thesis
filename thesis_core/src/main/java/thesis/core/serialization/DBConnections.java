package thesis.core.serialization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;

public class DBConnections
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
   private Connection cfgDB;
   private Connection worldsDB;

   public DBConnections()
   {

   }

   public boolean openConfigDB()
   {
      boolean success = true;
      try
      {
         Class.forName("org.h2.Driver");
      }
      catch (ClassNotFoundException e)
      {
         success = false;
         logger.error("Failed to load H2 database driver. Details: {}", e.getMessage());
      }

      if(success)
      {
         try
         {
            cfgDB = DriverManager.
                  getConnection("jdbc:h2:../config/cfg", "sa", "");
         }
         catch (SQLException e)
         {
            success = false;
            logger.error("Failed to connect to config database.  Details: {}", e.getMessage());
         }
      }
      return success;
   }

   public Connection getConfigDBConnection()
   {
      return cfgDB;
   }

   public void closeConfigDB()
   {
      try
      {
         cfgDB.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to close configuration database.  Details: {}", e.getMessage());
      }
   }

   public boolean openWorldsDB()
   {
      boolean success = true;
      try
      {
         Class.forName("org.h2.Driver");
      }
      catch (ClassNotFoundException e)
      {
         success = false;
         logger.error("Failed to load H2 database driver. Details: {}", e.getMessage());
      }

      if(success)
      {
         try
         {
            worldsDB = DriverManager.
                  getConnection("jdbc:h2:../worlds/worlds", "sa", "");
         }
         catch (SQLException e)
         {
            success = false;
            logger.error("Failed to connect to worlds database.  Details: {}", e.getMessage());
         }
      }
      return success;
   }

   public Connection getWorldsDBConnection()
   {
      return worldsDB;
   }

   public void closeWorldsDB()
   {
      try
      {
         worldsDB.close();
      }
      catch (SQLException e)
      {
         logger.error("Failed to close worlds database.  Details: {}", e.getMessage());
      }
   }
}
