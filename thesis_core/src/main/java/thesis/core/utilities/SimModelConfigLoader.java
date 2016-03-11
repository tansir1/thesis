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
         success = loadGenericSimData(propsLdr, logger);
      }

      if (!success)
      {
         logger.error("Failed to load simulation configuration from {}.", propFile);
      }

      return success;
   }

   private boolean loadGenericSimData(PropertiesLoader props, Logger logger)
   {
      boolean success = true;
      try
      {
         cfg.setRandomSeed(props.getInt("sim.randomseed"));
         cfg.setWorldDir(new File(props.getString("sim.worldDir", "FILE_NOT_SPECIFIED")));
         cfg.setEntityTypeDir(new File(props.getString("sim.entityDir", "FILE_NOT_SPECIFIED")));
         int commsRngPercent = props.getInt("sim.commsRngPercent");
         cfg.setCommsRngPercent(commsRngPercent / 100.0);
         cfg.setCommsRelayProbability(props.getDouble("sim.commsRelayProb"));
         cfg.setBeliefDecayRate(props.getDouble("sim.belief.decayRate"));
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
   public SimModelConfig getConfigData()
   {
      return cfg;
   }
}
