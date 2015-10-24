package thesis.worldgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.serialization.world.WorldConfig;
import thesis.core.serialization.world.WorldConfigFile;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.RenderWorld;
import thesis.core.world.World;

public class WorldGenApp
{

	private static boolean parseCmdLine(String[] args, WorldConfig baseCfgs, GenConfig genCfg)
	{
		boolean success = true;
		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

		Option heightOpt = Option.builder("h").longOpt("height").desc("Height of the worlds in meters.").hasArg()
				.required().build();

		Option widthOpt = Option.builder("w").longOpt("width").desc("Width of the worlds in meters.").hasArg()
				.required().build();

		Option rowOpt = Option.builder("r").longOpt("rows").desc("Number of rows in the worlds.").hasArg().required()
				.build();

		Option colOpt = Option.builder("c").longOpt("columns").desc("Number of columns in the worlds.").hasArg()
				.required().build();

		Option numWorldsOpt = Option.builder("wc").longOpt("worldCnt")
				.desc("Number of worlds to generate (world count)").hasArg().build();

		Option outputDirOpt = Option.builder("out").longOpt("outputDir")
				.desc("Directory where generated world files should be placed").hasArg().build();

		Option seedOpt = Option.builder("seed").desc("Number to be used as the seed for the random number generator")
				.hasArg().build();

		Options options = new Options();
		options.addOption(heightOpt);
		options.addOption(widthOpt);
		options.addOption(rowOpt);
		options.addOption(colOpt);
		options.addOption(numWorldsOpt);
		options.addOption(outputDirOpt);
		options.addOption(seedOpt);

		try
		{
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);

			// Required options
			if (line.hasOption("h"))
			{
				double height = Double.parseDouble(line.getOptionValue("h"));
				baseCfgs.getWorldHeight().setAsMeters(height);
			}

			if (line.hasOption("w"))
			{
				double width = Double.parseDouble(line.getOptionValue("w"));
				baseCfgs.getWorldWidth().setAsMeters(width);
			}

			if (line.hasOption("r"))
			{
				baseCfgs.setNumRows(Integer.parseInt(line.getOptionValue("r")));
			}

			if (line.hasOption("c"))
			{
				baseCfgs.setNumColumns(Integer.parseInt(line.getOptionValue("c")));
			}

			// Optional generator config options
			if (line.hasOption("wc"))
			{
				genCfg.setNumWorlds(Integer.parseInt(line.getOptionValue("wc")));
			}

			if (line.hasOption("out"))
			{
				genCfg.setOutputDir(new File(line.getOptionValue("out")));
			}

			if (line.hasOption("seed"))
			{
				genCfg.setRandSeed(Integer.parseInt(line.getOptionValue("seed")));
			}
		}
		catch (ParseException e)
		{
			logger.error("Error: {}", e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			// FIXME Get the correct script call for this
			formatter.printHelp("thesis_world_gen -w 10000 -h 20000 -r 10 -c 20", options);
			success = false;
		}
		return success;
	}

	public static void generateWorlds(WorldConfig baseWorldCfg, GenConfig genCfg)
	{
		if (!genCfg.getOutputDir().exists())
		{
			genCfg.getOutputDir().mkdirs();
		}

		final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
		logger.info("Generating {} worlds into directory: {}", genCfg.getNumWorlds(),
				genCfg.getOutputDir().getAbsolutePath());

		final WorldGenerator worldGen = new WorldGenerator(genCfg.getRandSeed(), baseWorldCfg.getWorldWidth(),
				baseWorldCfg.getWorldHeight(), baseWorldCfg.getNumRows(), baseWorldCfg.getNumColumns());

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
				BufferedImage img = RenderWorld.renderToImage(new World(world), 640, 480);
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
		final WorldConfig baseWorldCfg = new WorldConfig();

		// Set default generation options
		final GenConfig genCfg = new GenConfig();
		genCfg.setOutputDir(new File("worlds"));
		genCfg.setNumWorlds(10);
		genCfg.setRandSeed(42);

		if (parseCmdLine(args, baseWorldCfg, genCfg))
		{
			generateWorlds(baseWorldCfg, genCfg);
		}
	}

}
