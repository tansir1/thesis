package thesis.gui;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.entities.EntityTypesFile;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.serialization.world.WorldConfigFile;
import thesis.core.utilities.CoreRsrcPaths;
import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.utilities.SimModelConfigLoader;
import thesis.gui.mainwindow.MainWindow;

public class ThesisGUIApp
{
   /**
    * Attempt to find, load, and parse the simulation configuration file.
    *
    * If the local sim.properties file cannot be found then the embedded default
    * file will be exported.
    *
    * @param logger
    *           Issues encountered while loading configuration data will be
    *           logged here.
    * @return The parsed configuration data or null if the data failed to load
    *         for any reason.
    */
   private static SimModelConfig loadSimConfig(Logger logger)
   {
      SimModelConfig cfg = null;

      File cfgFile = new File("sim.properties");
      if (!cfgFile.exists())
      {
         logger.warn("Could not find sim.properties.  Exporting default properties.");
         if (!CoreUtils.exportResource(CoreRsrcPaths.DFLT_SIM_PATH, new File("sim.properties")))
         {
            logger.error("Could not export the default sim.properties file.");
         }
      }
      else
      {
         SimModelConfigLoader cfgLdr = new SimModelConfigLoader();
         if (!cfgLdr.loadFile(cfgFile))
         {
            logger.error("Failed to parse simulation configuration data.");
         }
         else
         {
            cfg = cfgLdr.getConfigData();
         }
      }

      return cfg;
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation version {}", CoreUtils.loadVersionID());

      boolean abort = false;

      WorldConfig worldCfg = null;
      EntityTypes entityTypes = null;

      SimModelConfig cfg = loadSimConfig(logger);
      if (cfg == null)
      {
         abort = true;
      }
      else
      {
         try
         {
            worldCfg = WorldConfigFile.loadConfig(cfg.getWorldFile());
            if (!abort && worldCfg == null)
            {
               logger.error("Failed to load world configuration file: {}", cfg.getWorldFile().getAbsolutePath());
               abort = true;
            }

            entityTypes = EntityTypesFile.loadTypes(cfg.getEntityTypeFile());
            if (!abort && entityTypes == null)
            {
               logger.error("Failed to load entity types configuration file: {}",
                     cfg.getEntityTypeFile().getAbsolutePath());
               abort = true;
            }
         }
         catch (FileNotFoundException fnfe)
         {
            logger.error("{}", fnfe);
         }
      }

      if (!abort)
      {
         logger.debug("Sim model initialized with:\n{}", cfg);

         SimModel simModel = new SimModel();
         simModel.reset(cfg.getRandomSeed(), worldCfg, entityTypes, cfg.getCommsRngPercent(),
               cfg.getCommsRelayProbability());

         MainWindow mainWin = new MainWindow();
         mainWin.connectSimModel(simModel);
      }
   }

}
