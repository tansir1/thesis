package thesis.worldgen;

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
import thesis.core.utilities.LoggerIDs;

public class WorldGenApp
{

	private static boolean parseCmdLine(String[] args, WorldConfig baseCfgs)
	{
		boolean success = true;
		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

		Option heightOpt = Option.builder("h")
				.longOpt("height")
				.desc("Height of the worlds in meters.")
				.hasArg()
				.required()
				.build();

		Option widthOpt = Option.builder("w")
				.longOpt("width")
				.desc("Width of the worlds in meters.")
				.hasArg()
				.required()
				.build();

		Option rowOpt = Option.builder("r")
				.longOpt("rows")
				.desc("Number of rows in the worlds.")
				.hasArg()
				.required()
				.build();

		Option colOpt = Option.builder("c")
				.longOpt("columns")
				.desc("Number of columns in the worlds.")
				.hasArg()
				.required()
				.build();

		Options options = new Options();
		options.addOption(heightOpt);
		options.addOption(widthOpt);
		options.addOption(rowOpt);
		options.addOption(colOpt);

		try
		{
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);

			if(line.hasOption("h"))
			{
				double height = Double.parseDouble(line.getOptionValue("h"));
				baseCfgs.getWorldHeight().setAsMeters(height);
			}

			if(line.hasOption("w"))
			{
				double width = Double.parseDouble(line.getOptionValue("w"));
				baseCfgs.getWorldWidth().setAsMeters(width);
			}

			if(line.hasOption("r"))
			{
				baseCfgs.setNumRows(Integer.parseInt(line.getOptionValue("r")));
			}

			if(line.hasOption("c"))
			{
				baseCfgs.setNumColumns(Integer.parseInt(line.getOptionValue("c")));
			}
		}
		catch (ParseException e)
		{
			logger.error("Error: {}", e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			//FIXME Get the correct script call for this
			formatter.printHelp( "thesis_world_gen", options );
			success = false;
		}
		return success;
	}

	public static void main(String[] args)
	{
		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

		WorldConfig baseCfg = new WorldConfig();
		// TODO Auto-generated method stub
		if(parseCmdLine(args, baseCfg))
		{
			logger.info("{}", baseCfg);
		}
	}

}
