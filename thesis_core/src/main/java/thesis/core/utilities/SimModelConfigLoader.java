package thesis.core.utilities;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses a simulation configuration file.
 */
public class SimModelConfigLoader
{
   /**
    * Parsed configuration data is stored here.
    */
   private SimModelConfig cfg;

   public SimModelConfigLoader()
   {
      cfg = new SimModelConfig();
   }

   /**
    * Load a simulation configuration file.
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
         logger.debug("Loading simulation configuration from {}.", propFile);

         loadWorldData(propsLdr);

         success = loadGenericSimData(propsLdr, logger);

      }

      if (!success)
      {
         logger.error("Failed to load simulation configuration from {}.", propFile);
      }

      return success;
   }

   private void loadWorldData(PropertiesLoader props)
   {
      cfg.setWorldWidth(props.getDouble("world.width", 100.0));
      cfg.setWorldHeight(props.getDouble("world.height", 100.0));
      cfg.setNumWorldRows(props.getInt("world.rows", 10));
      cfg.setNumWorldCols(props.getInt("world.cols", 10));
   }

   private boolean loadGenericSimData(PropertiesLoader props, Logger logger)
   {
      boolean success = true;
      try
      {
         cfg.setRandomSeed(props.getInt("sim.randomseed"));
      }
      catch (Exception e)
      {
         logger.debug(e.getLocalizedMessage());
         success = false;
      }
      return success;
   }

   /**
    * Get the configuration data.  Only useful after parsing a configuration file via {@link #loadFile(File)}.
    * 
    * @return The configuration data.
    */
   public SimModelConfig getConfigData()
   {
      return cfg;
   }
}
