package thesis.sim.utilities;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.PropertiesLoader;

public class SimAppConfigLoader
{
   /**
    * Parsed configuration data is stored here.
    */
   private SimAppConfig cfg;

   public SimAppConfigLoader()
   {
      cfg = new SimAppConfig();
   }

   /**
    * Load a application configuration file.
    *
    * @param propFile
    *           The properties file on disk to load.
    * @return True if the file was loaded successfully, false otherwise.
    */
   public boolean loadFile(File propFile)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

      PropertiesLoader propsLdr = new PropertiesLoader();
      boolean success = propsLdr.loadFile(propFile);

      if (success)
      {
         logger.debug("Loading application configuration from {}.", propFile);
         success = loadAppData(propsLdr, logger);
      }

      if (!success)
      {
         logger.error("Failed to load application configuration from {}.", propFile);
      }

      return success;
   }

   private boolean loadAppData(PropertiesLoader props, Logger logger)
   {
      boolean success = true;
      try
      {
         cfg.setEnableNetwork(props.getBool("network.enable", false));
         if(cfg.isEnableNetwork())
         {
            cfg.setServerIP(props.getString("network.serverIP", "NOT_SPECIFIED"));
            cfg.setServerPort(props.getInt("network.serverPort", -1));
         }
      }
      catch (Exception e)
      {
         logger.debug(e.getMessage());
         success = false;
      }
      return success;
   }

   /**
    * Get the configuration data.  Only useful after parsing a configuration file via {@link #loadFile(File)}.
    *
    * @return The configuration data.
    */
   public SimAppConfig getConfigData()
   {
      return cfg;
   }
}
