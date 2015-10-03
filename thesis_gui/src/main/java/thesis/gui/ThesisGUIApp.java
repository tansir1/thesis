package thesis.gui;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.utilities.SimModelConfigLoader;
import thesis.core.utilities.VersionIDLoader;
import thesis.gui.mainwindow.MainWindow;

public class ThesisGUIApp
{
   /**
    * Attempt to find, load, and parse the simulation configuration file.
    * 
    * @param logger
    *           Issues encountered while loading configuration data will be
    *           logged here.
    * @return The parsed configuration data or null if the data failed to load
    *         for any reason.
    */
   private static SimModelConfig loadConfig(Logger logger)
   {
      SimModelConfig cfg = null;

      File cfgFile = new File("sim.properties");
      if (cfgFile.exists())
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
      else
      {
         logger.error("Could not find sim.properties.  Aborting.");
      }

      return cfg;
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation version {}", VersionIDLoader.loadVersionID());

      SimModelConfig cfg = loadConfig(logger);
      if (cfg == null)
      {
         System.exit(1);
      }
      else
      {
         SimModel simModel = new SimModel();
         simModel.init(cfg);
         
         MainWindow mainWin = new MainWindow();
      }
   }

}
