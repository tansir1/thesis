package thesis.core.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;

/**
 * High level manager that maintains all UAVs in the simulation.
 */
public class UAVMgr
{
	private Logger logger;
	private List<UAV> uavs;

	public UAVMgr()
	{
		logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
		uavs = new ArrayList<UAV>();
	}

	/**
	 * Initialize the UAV manager with a collection of UAVs derived from
	 * the given configuration data.
	 *
	 * @param entTypes
	 *            UAV types will be queried from this.
	 * @param worldCfg
	 *            UAVs will be generated based on configuration data from
	 *            here and types will be cross referenced from entTypes.
	 */
	public void reset(EntityTypes entTypes, WorldConfig worldCfg)
	{
		logger.debug("Resetting UAV Manager.");

		uavs.clear();

		for (UAVEntityConfig uavEntCfg : worldCfg.uavCfgs)
		{
			UAVType type = entTypes.getUAVType(uavEntCfg.getUAVType());
			if (type != null)
			{
				UAV uav = new UAV(type);
				uav.getCoordinate().setCoordinate(uavEntCfg.getLocation());
				uav.getHeading().copy(uavEntCfg.getOrientation());
				uav.getHeading().normalize360();
				uavs.add(uav);
			}
			else
			{
				logger.error("UAV configured with an unknown target type.  Ignoring UAV.");
			}
		}
	}

	/**
	 * @return An unmodifiable view of all uavs in the simulation.
	 */
	public Collection<UAV> getAllUAVs()
	{
		return Collections.unmodifiableCollection(uavs);
	}

	/**
	 * Step the simulation forward by the requested amount of time.
	 *
	 * @param deltaTimeMS
	 *            Advance the simulation forward by this many milliseconds.
	 */
	public void stepSimulation(long deltaTimeMS)
	{
		for(UAV uav : uavs)
		{
			uav.stepSimulation(deltaTimeMS);
		}
	}
}
