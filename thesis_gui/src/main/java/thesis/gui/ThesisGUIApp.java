package thesis.gui;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.gui.mainwindow.MainWindow;
import thesis.gui.network.NetworkHndlr;
import thesis.gui.utilities.GUIAppConfig;
import thesis.gui.utilities.GUIAppConfigLoader;
import thesis.network.messages.InfrastructureMsg;

public class ThesisGUIApp
{
   private static GUIAppConfig loadAppConfig(Logger logger)
   {
      // TODO Export default properties file if not found

      GUIAppConfig cfg = null;

      File cfgFile = new File("gui.properties");

      GUIAppConfigLoader cfgLdr = new GUIAppConfigLoader();
      if (!cfgLdr.loadFile(cfgFile))
      {
         logger.error("Failed to parse application configuration data.");
      }
      else
      {
         cfg = cfgLdr.getConfigData();
      }

      return cfg;
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation GUI version {}", CoreUtils.loadVersionID());

      boolean abort = false;

      GUIAppConfig appCfg = loadAppConfig(logger);
      if(appCfg == null)
      {
         abort = true;
      }

      if(!abort)
      {
         LinkedBlockingQueue<InfrastructureMsg> recvQ = new LinkedBlockingQueue<InfrastructureMsg>();
         LinkedBlockingQueue<InfrastructureMsg> sendQ = new LinkedBlockingQueue<InfrastructureMsg>();

         NetworkHndlr netHndlr = new NetworkHndlr();
         netHndlr.start(sendQ, recvQ, appCfg.getServerIP(), appCfg.getServerPort());

         MainWindow mainWin = new MainWindow();
         mainWin.connectQueues(sendQ, recvQ);
      }


      if(abort)
      {
         logger.error("Failed to initialize GUI.  Terminating.");
      }
   }

}
