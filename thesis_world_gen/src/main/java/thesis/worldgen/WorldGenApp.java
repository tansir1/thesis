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

import thesis.core.SimModel;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.entities.EntityTypesFile;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.serialization.world.WorldConfigFile;
import thesis.core.utilities.CoreUtils;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.VersionID;
import thesis.core.world.RenderSimState;
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

		Option cfgFileOpt = Option.builder("cfg").longOpt("configFile")
				.desc("Path to the generation configuration file.").hasArg().build();

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
					logger.error("Configuration file does not exist at " + cfgFile.getAbsolutePath() + " Aborting generation.");
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

	public static void generateWorlds(GeneratorConfig genCfg, EntityTypes entTypes)
	{
		if (!genCfg.getOutputDir().exists())
		{
			genCfg.getOutputDir().mkdirs();
		}

		final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
		logger.info("Generating {} worlds into directory: {}", genCfg.getNumWorlds(),
				genCfg.getOutputDir().getAbsolutePath());

		final WorldGenerator worldGen = new WorldGenerator(genCfg.getRandSeed(), genCfg.getWorldWidth(),
				genCfg.getWorldHeight(), genCfg.getNumRows(), genCfg.getNumColumns());

		final DecimalFormat numFrmt = new DecimalFormat("00");
		for (int i = 0; i < genCfg.getNumWorlds(); ++i)
		{

			File worldFile = new File(genCfg.getOutputDir(), "world" + numFrmt.format(i) + ".xml");
			File screenShotFile = new File(genCfg.getOutputDir(), "world" + numFrmt.format(i) + ".png");

			logger.debug("-------------------------------------------------");
			logger.debug("Generating world {}", i);
			WorldConfig world = worldGen.generateWorld();

			try
			{
				logger.info("Saving world {} into {}.", i, worldFile.getAbsolutePath());
				if (!WorldConfigFile.saveConfig(worldFile, world))
				{
					logger.error("Failed to save world {} into {}", i, worldFile.getAbsolutePath());
					System.exit(1);
				}

				logger.debug("Saving world {} screenshot into {}", i, screenShotFile.getAbsolutePath());
				SimModel model = new SimModel();
				model.reset(0, world, entTypes);

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

	public static void main(String[] args)
	{

		GeneratorConfig genCfg = new GeneratorConfig();
		try
		{
			if (parseCmdLine(args, genCfg))
			{
				EntityTypes entTypes = EntityTypesFile.loadTypes(genCfg.getEntityTypesFile());
				generateWorlds(genCfg, entTypes);
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
