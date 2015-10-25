package thesis.core;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.entities.TargetMgr;
import thesis.core.entities.UAVMgr;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.world.World;

public class SimModel
{
	private Logger logger;
	private World world;

	private TargetMgr tgtMgr;
	private UAVMgr uavMgr;

	/**
	 * A shared random number generator that is initialized with a known seed
	 * value for reproducible experiments.
	 */
	private Random randGen;

	/**
	 * Create an uninitialized world.
	 *
	 * The world must be initialized after being constructed.
	 *
	 * @see #reset(SimModelConfig)
	 */
	public SimModel()
	{
		logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
		tgtMgr = new TargetMgr();
		uavMgr = new UAVMgr();
	}

	/**
	 * Initialize the model with the necessary configuration parameters.
	 *
	 * @param randomSeed
	 *            Initialize the random number generator with this value.
	 * @param worldCfg
	 *            Configuration data for the world.
	 * @param entTypes
	 *            The types of entities within the world.
	 */
	public void reset(int randomSeed, WorldConfig worldCfg, EntityTypes entTypes)
	{
		randGen = new Random(randomSeed);

		logger.debug("EntityTypes initialized with:\n{}", entTypes);
		logger.debug("World model intiliazed with:\n{}", worldCfg);

		world = new World(worldCfg);
		// TODO Entity types
		tgtMgr.reset(entTypes, worldCfg);
		uavMgr.reset(entTypes, worldCfg);
	}

	/**
	 * Get the world map submodel.
	 *
	 * @return The world map of the simulation.
	 */
	public World getWorld()
	{
		return world;
	}

	public TargetMgr getTargetManager()
	{
		return tgtMgr;
	}

	public UAVMgr getUAVManager()
	{
	   return uavMgr;
	}

	/**
	 * Get the shared random number generator.
	 *
	 * @return The simulation's random number generator.
	 */
	public Random getRandomGenerator()
	{
		return randGen;
	}

	/**
	 * Step the simulation forward by the requested amount of time.
	 *
	 * @param deltaTimeMS
	 *            Advance the simulation forward by this many milliseconds.
	 */
	public void stepSimulation(long deltaTimeMS)
	{
		logger.debug("-------------------Simulation stepping.-----------------");
		tgtMgr.stepSimulation(deltaTimeMS);
		uavMgr.stepSimulation(deltaTimeMS);
	}
}
