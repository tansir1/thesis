package thesis.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.SimModel;
import thesis.core.serialization.DBConnections;
import thesis.core.serialization.EntityTypeCSVCodec;
import thesis.core.serialization.WorldConfigCSVCodec;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.CoreRsrcPaths;
import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.utilities.SimModelConfigLoader;
import thesis.gui.mainwindow.MainWindow;
import thesis.network.ServerComms;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.TestMsg;

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

   private static boolean loadData(Logger logger, DBConnections dbConns, EntityTypeCfgs entityTypes,
         WorldConfig worldCfg, SimModelConfig simCfg)
   {
      boolean success = true;
      EntityTypeCSVCodec entTypesCfgCodec = new EntityTypeCSVCodec();
      if (!entTypesCfgCodec.loadCSV(dbConns, simCfg.getEntityTypeDir(), entityTypes))
      {
         logger.error("Failed to load entity types configuration data: {}",
               simCfg.getEntityTypeDir().getAbsolutePath());
         success = false;
      }

      WorldConfigCSVCodec worldCfgCodec = new WorldConfigCSVCodec();
      if (!worldCfgCodec.loadCSV(dbConns, simCfg.getWorldDir(), worldCfg))
      {
         logger.error("Failed to load world configuration data: {}",
               simCfg.getWorldDir().getAbsolutePath());
         success = false;
      }
      return success;
   }

   public static void netTest()
   {
      ServerComms comms = new ServerComms();
      comms.listenForClient("127.0.0.1", 10555);

      List<InfrastructureMsg> msgs = comms.getData();
      while(msgs == null || msgs.isEmpty())
      {
         msgs = comms.getData();
      }
      TestMsg recvMsg = (TestMsg)msgs.get(0);
      int data[] = recvMsg.getData();
      for(int i=0; i<data.length; ++i)
      {
         System.out.println(data[i]);
      }

      data[0]=3;
      data[1]=4;
      data[2]=5;
      TestMsg sendMsg = new TestMsg();
      sendMsg.setData(data);

      msgs = new ArrayList<InfrastructureMsg>();
      msgs.add(sendMsg);
      comms.sendData(msgs);
      comms.disconnect();
   }

   public static void main(String[] args)
   {
      netTest();
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation version {}", CoreUtils.loadVersionID());

      boolean abort = false;

      DBConnections dbConns = new DBConnections();

      WorldConfig worldCfg = new WorldConfig();
      EntityTypeCfgs entityTypes = new EntityTypeCfgs();

      SimModelConfig simCfg = loadSimConfig(logger);
      if (simCfg == null)
      {
         abort = true;
      }

      if(!abort)
      {
         abort = !(dbConns.openConfigDB() && dbConns.openWorldsDB());
      }

      if(!abort)
      {
         abort = !loadData(logger, dbConns, entityTypes, worldCfg, simCfg);
      }

      if (!abort)
      {
         logger.debug("Sim model initialized with:\n{}", simCfg);

         SimModel simModel = new SimModel();
         simModel.reset(simCfg.getRandomSeed(), worldCfg, entityTypes, simCfg.getCommsRngPercent(),
               simCfg.getCommsRelayProbability());

         MainWindow mainWin = new MainWindow();
         mainWin.connectSimModel(simModel);
      }
   }

}
