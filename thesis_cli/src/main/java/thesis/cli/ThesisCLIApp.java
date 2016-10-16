package thesis.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.StatResults;
import thesis.core.serialization.DBConnections;
import thesis.core.serialization.EntityTypeCSVCodec;
import thesis.core.serialization.WorldConfigCSVCodec;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.CoreRsrcPaths;
import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.utilities.SimModelConfigLoader;

public class ThesisCLIApp
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
         SimModelConfig simCfg)
   {
      boolean success = true;
      EntityTypeCSVCodec entTypesCfgCodec = new EntityTypeCSVCodec();
      if (!entTypesCfgCodec.loadCSV(dbConns, simCfg.getEntityTypeDir(), entityTypes))
      {
         logger.error("Failed to load entity types configuration data: {}",
               simCfg.getEntityTypeDir().getAbsolutePath());
         success = false;
      }

      return success;
   }

   private static List<WorldAndName> loadWorlds(Logger logger, DBConnections dbConns, String worldsRootDir)
   {
      List<WorldAndName> worlds = new ArrayList<WorldAndName>();

      File[] directories = new File(worldsRootDir).listFiles(File::isDirectory);
      for (File worldDir : directories)
      {
         WorldConfig worldCfg = new WorldConfig();

         logger.debug("Loading world in {}", worldDir);
         loadWorld(logger, dbConns, worldCfg, worldDir);

         WorldAndName retVal = new WorldAndName();
         retVal.worldName = worldDir;
         retVal.worldCfg = worldCfg;
         worlds.add(retVal);
      }
      return worlds;
   }

   private static boolean loadWorld(Logger logger, DBConnections dbConns, WorldConfig worldCfg, File worldDir)
   {
      boolean success = true;
      WorldConfigCSVCodec worldCfgCodec = new WorldConfigCSVCodec();
      if (!worldCfgCodec.loadCSV(dbConns, worldDir, worldCfg))
      {
         logger.error("Failed to load world configuration data: {}", worldDir.getAbsolutePath());
         success = false;
      }
      return success;
   }

   private static boolean saveResults(List<WorldAndName> results)
   {
      File resultsDir = new File("results");
      if (!resultsDir.isDirectory())
      {
         resultsDir.mkdirs();
      }


      try
      {
         File worldFile = new File(resultsDir, "worldData.txt");
         PrintWriter worldWriter = new PrintWriter(worldFile);
         worldWriter.println("World, Time all detected (ms), Time all destroyed (ms), Time world known (ms)");
         
         for (WorldAndName worldAndName : results)
         {
            saveTargetData(resultsDir, worldAndName);
            
            
            StringBuilder sb = new StringBuilder();
            sb.append(worldAndName.worldName.getName());
            sb.append(",");
            sb.append(worldAndName.results.getTimeAllTargetsFound());
            sb.append(",");
            sb.append(worldAndName.results.getTimeAllTargetsDestroyed());
            sb.append(",");
            sb.append(worldAndName.results.getTimeAllWorldKnown());
            worldWriter.println(sb.toString());
            
         }         
         
         worldWriter.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      return false;
   }

   private static void saveTargetData(File resultsDir, WorldAndName results)
   {
      File tgtFile = new File(resultsDir, results.worldName.getName() + "TgtData.txt");

      try
      {
         PrintWriter tgtWriter = new PrintWriter(tgtFile);
         results.results.saveTargetResults(tgtWriter);
         tgtWriter.close();
      }
      catch (FileNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.info("Starting simulation CLI version {}", CoreUtils.loadVersionID());

      boolean abort = false;

      DBConnections dbConns = new DBConnections();
      EntityTypeCfgs entityTypes = new EntityTypeCfgs();

      SimModelConfig simCfg = loadSimConfig(logger);

      if (simCfg == null)
      {
         abort = true;
      }

      if (args.length < 1)
      {
         abort = true;
         logger.error("No world directory path given.");
      }

      if (!abort)
      {
         abort = !(dbConns.openConfigDB() && dbConns.openWorldsDB());
      }

      if (!abort)
      {
         abort = !loadData(logger, dbConns, entityTypes, simCfg);
      }

      if (!abort)
      {
         logger.debug("Sim model initialized with:\n{}", simCfg);

         List<WorldAndName> worlds = loadWorlds(logger, dbConns, args[0]);
         worlds.sort(new Comparator<WorldAndName>()
         {

            @Override
            public int compare(WorldAndName lhs, WorldAndName rhs)
            {
               return lhs.worldName.getName().compareTo(rhs.worldName.getName());
            }
         });

         for (WorldAndName worldAndName : worlds)
         {
            StringBuilder sb = new StringBuilder();
            sb.append("\n*****************************************************************\n");
            sb.append("*****************************************************************\n");
            sb.append("*****************************************************************\n");
            sb.append("-----Start simulation of world {}");
            logger.info(sb.toString(), worldAndName.worldName.getName());
            ThesisCLI simRunner = new ThesisCLI();
            simRunner.resetNewSim(simCfg, worldAndName.worldCfg, entityTypes);
            worldAndName.results = simRunner.runSim();
         }

         StringBuilder sb = new StringBuilder();
         sb.append("\n*****************************************************************\n");
         sb.append("*****************************************************************\n");
         sb.append("*****************************************************************\n");
         sb.append("-----Saving sim data to log files");
         logger.info(sb.toString());
         saveResults(worlds);
      }

      if (abort)
      {
         logger.error("Failed to initialize application.  Terminating.");
      }
   }

   private static class WorldAndName
   {
      public File worldName;
      public WorldConfig worldCfg;
      public StatResults results;
   }
}