package thesis.gui;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.utilities.SimModelConfigLoader;
import thesis.core.utilities.Utils;
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
   private static SimModelConfig loadConfig(Logger logger)
   {
      SimModelConfig cfg = null;

      File cfgFile = new File("sim.properties");
      if (!cfgFile.exists())
      {
         logger.warn("Could not find sim.properties.  Exporting default properties.");
         if (!Utils.exportResource("thesis/core/sim.properties", new File("sim.properties")))
         {
            logger.error("Could not export the default sim.properties file.");
         }
      }

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
         logger.error("Failed to load exported sim.properties file.");
      }

      return cfg;
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation version {}", Utils.loadVersionID());

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
