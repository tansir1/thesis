package thesis.worldgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.SimModel;
import thesis.core.serialization.DBConnections;
import thesis.core.serialization.EntityTypeCSVCodec;
import thesis.core.serialization.WorldConfigCSVCodec;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.VersionID;
import thesis.core.world.RenderSimState;
import thesis.core.world.WorldGIS;
import thesis.worldgen.utilities.GeneratorConfig;
import thesis.worldgen.utilities.GeneratorConfigLoader;
import thesis.worldgen.utilities.WorldGenRsrcPaths;

public class WorldGenApp
{
   private static boolean parseCmdLine(String[] args, GeneratorConfig genCfg) throws FileNotFoundException
   {
      boolean success = true;
      final String helpFmt = "thesis_world_gen -cfg path/to/worldGen.properties";
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

      Option helpOpt = Option.builder("h").longOpt("help").desc("Display this text output.").build();

      Option versionOpt = Option.builder("v").longOpt("version").desc("Display the version number.").build();

      Option cfgFileOpt = Option.builder("cfg").longOpt("configFile").desc("Path to the generation configuration file.")
            .hasArg().build();

      Option exportDfltFileOpt = Option.builder("dflt").longOpt("default")
            .desc("Export the default generation configuration file.").build();

      OptionGroup cfgFileOpts = new OptionGroup();
      cfgFileOpts.addOption(cfgFileOpt);
      cfgFileOpts.addOption(exportDfltFileOpt);
      cfgFileOpts.setRequired(true);

      Options options = new Options();
      options.addOption(helpOpt);
      options.addOption(versionOpt);
      options.addOptionGroup(cfgFileOpts);

      try
      {
         CommandLineParser parser = new DefaultParser();
         CommandLine line = parser.parse(options, args);

         if (line.hasOption("h"))
         {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(helpFmt, options);
            success = false;
         }

         if (line.hasOption("v"))
         {
            VersionID version = CoreUtils.loadVersionID();
            System.out.println("Thesis project version " + version);
            success = false;
         }

         if (line.hasOption("cfg"))
         {
            File cfgFile = new File(line.getOptionValue("cfg"));
            if (!cfgFile.exists())
            {
               logger.error(
                     "Configuration file does not exist at " + cfgFile.getAbsolutePath() + " Aborting generation.");
               throw new FileNotFoundException();
            }

            GeneratorConfigLoader loader = new GeneratorConfigLoader();
            if (loader.loadFile(cfgFile))
            {
               genCfg.copy(loader.getConfigData());
            }
            else
            {
               success = false;
               logger.error("Failed to load generation configuration.");
            }
         }

         if (line.hasOption("dflt"))
         {
            File cfgFile = new File("worldGen.properties");
            logger.info("Exporting default world generation configuration file to {}", cfgFile.getAbsolutePath());
            CoreUtils.exportResource(WorldGenRsrcPaths.DFLT_WORLD_GEN_CFG, cfgFile);
            success = false;
         }
      }
      catch (ParseException e)
      {
         logger.error("Error: {}", e.getMessage());
         HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp(helpFmt, options);
         success = false;
      }
      return success;
   }

   public static void generateWorlds(GeneratorConfig genCfg, EntityTypeCfgs entTypes, Logger logger)
   {
      if (!genCfg.getOutputDir().exists())
      {
         genCfg.getOutputDir().mkdirs();
      }

      DBConnections dbConns = new DBConnections();
      if (!dbConns.openWorldsDB())
      {
         logger.error("Failed to open worlds database.");
         return;
      }

      logger.info("Generating {} worlds into directory: {}", genCfg.getNumWorlds(),
            genCfg.getOutputDir().getAbsolutePath());

      WorldGIS gis = new WorldGIS();
      gis.reset(genCfg.getWorldWidth(), genCfg.getWorldHeight(), genCfg.getNumRows(), genCfg.getNumColumns());

      final WorldGenerator worldGen = new WorldGenerator(genCfg.getRandSeed(), gis);

      final DecimalFormat numFrmt = new DecimalFormat("00");
      for (int i = 0; i < genCfg.getNumWorlds(); ++i)
      {

         File worldDir = new File(genCfg.getOutputDir(), "world" + numFrmt.format(i) + "/");
         File screenShotFile = new File(genCfg.getOutputDir(), "world" + numFrmt.format(i) + ".png");

         logger.debug("-------------------------------------------------");
         logger.debug("Generating world {}", i);
         WorldConfig worldCfg = worldGen.generateWorld(entTypes, genCfg.getNumMobileTargets(),
               genCfg.getNumStaticTargets(), genCfg.getNumUAVs());

         try
         {
            WorldConfigCSVCodec worldCfgCodec = new WorldConfigCSVCodec();
            logger.info("Saving world {} into {}.", i, worldDir.getAbsolutePath());
            if (!worldCfgCodec.writeCSV(dbConns, worldDir, worldCfg))
            {
               logger.error("Failed to save world {} into {}", i, worldDir.getAbsolutePath());
               System.exit(1);
            }

            logger.debug("Saving world {} screenshot into {}", i, screenShotFile.getAbsolutePath());
            SimModel model = new SimModel();
            model.reset(0, worldCfg, entTypes, 0.0f, 0.0f, 0, 0);

            BufferedImage img = RenderSimState.renderToImage(model, 640, 480);
            ImageIO.write(img, "png", screenShotFile);
         }
         catch (IOException e)
         {
            logger.error("I/O error while saving world {}.  Details: {}", i, e.getMessage());
            System.exit(1);
         }
      }
      logger.info("World generation complete.");
   }

   private static boolean loadEntityTypeCfgs(Logger logger, EntityTypeCfgs entTypeCfgs, File entityTypesCfgDir)
   {
      boolean success = true;

      DBConnections dbConns = new DBConnections();
      if (dbConns.openConfigDB())
      {
         EntityTypeCSVCodec cfgLdr = new EntityTypeCSVCodec();
         if (!cfgLdr.loadCSV(dbConns, entityTypesCfgDir, entTypeCfgs))
         {
            logger.error("Failed to load entity configurations database.");
            success = false;
         }
         dbConns.closeConfigDB();
      }
      else
      {
         logger.error("Failed to open entity configurations database.");
         success = false;
      }
      return success;
   }

   public static void main(String[] args)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

      GeneratorConfig genCfg = new GeneratorConfig();

      try
      {
         if (parseCmdLine(args, genCfg))
         {
            EntityTypeCfgs entTypeCfgs = new EntityTypeCfgs();
            if (loadEntityTypeCfgs(logger, entTypeCfgs, genCfg.getEntityTypesDir()))
            {
               generateWorlds(genCfg, entTypeCfgs, logger);
            }
         }
      }
      catch (FileNotFoundException e)
      {
         // Already logged earlier in parseCmdLine()
      }

   }

}
