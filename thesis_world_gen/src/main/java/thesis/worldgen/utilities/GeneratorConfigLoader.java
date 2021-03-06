package thesis.worldgen.utilities;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.PropertiesLoader;

/**
 * Parses a world generator configuration file.
 */
public class GeneratorConfigLoader
{
	/**
	 * Parsed configuration data is stored here.
	 */
	private GeneratorConfig cfg;

	public GeneratorConfigLoader()
	{
		cfg = new GeneratorConfig();
	}

	/**
	 * Load a generation configuration file.
	 *
	 * @param propFile
	 *            The properties file on disk to load.
	 * @return True if the file was loaded successfully, false otherwise.
	 */
	public boolean loadFile(File propFile)
	{
		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

		PropertiesLoader propsLdr = new PropertiesLoader();
		boolean success = propsLdr.loadFile(propFile);

		if (success)
		{
			logger.debug("Loading generation configuration from {}.", propFile);

			cfg.setOutputDir(new File(propsLdr.getString("outputDir", "FILE_NOT_SPECIFIED")));
			cfg.setEntityTypesFile(new File(propsLdr.getString("entityTypesDir", "FILE_NOT_SPECIFIED")));

			try
			{
				cfg.setRandSeed(propsLdr.getInt("randomSeed"));
				cfg.setNumWorlds(propsLdr.getInt("numWorlds"));
				cfg.setNumUAVs(propsLdr.getInt("numUAVs"));
			}
			catch (Exception e)
			{
				logger.error("Random seed not specified in configuration file.");
				success = false;
			}

			if (success)
			{
				success = loadWorldCfg(propsLdr, logger);
			}

			if (success)
			{
				success = loadTargetCfg(propsLdr, logger);
			}

		}

		if (!success)
		{
			logger.error("Failed to load generation configuration from {}.", propFile);
		}

		return success;
	}

	private boolean loadWorldCfg(PropertiesLoader propsLdr, Logger logger)
	{
		boolean success = true;

		try
		{
			cfg.setNumColumns(propsLdr.getInt("world.columns"));
			cfg.setNumRows(propsLdr.getInt("world.rows"));

			double width = propsLdr.getDouble("world.width");
			double height = propsLdr.getDouble("world.height");

			cfg.setWorldWidth(width);
			cfg.setWorldHeight(height);
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
			success = false;
		}
		return success;
	}

	private boolean loadTargetCfg(PropertiesLoader propsLdr, Logger logger)
	{
		boolean success = true;

		try
		{
			cfg.setNumMobileTargets((propsLdr.getInt("targets.numMobile")));
			cfg.setNumStaticTargets((propsLdr.getInt("targets.numStatic")));
		}
		catch (Exception e)
		{
			logger.debug(e.getMessage());
			success = false;
		}
		return success;
	}

	/**
	 * Get the configuration data. Only useful after parsing a configuration
	 * file via {@link #loadFile(File)}.
	 *
	 * @return The configuration data.
	 */
	public GeneratorConfig getConfigData()
	{
		return cfg;
	}
}
