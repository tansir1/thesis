package thesis.core.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.TargetEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;

/**
 * High level manager that maintains all targets in the simulation.
 */
public class TargetMgr
{
	private Logger logger;
	private List<Target> targets;

	public TargetMgr()
	{
		logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
		targets = new ArrayList<Target>();
	}

	/**
	 * Initialize the target manager with a collection of targets derived from
	 * the given configuration data.
	 *
	 * @param entTypes
	 *            Target types will be queried from this.
	 * @param worldCfg
	 *            Targets will be generated based on configuration data from
	 *            here and types will be cross referenced from entTypes.
	 */
	public void reset(EntityTypes entTypes, WorldConfig worldCfg)
	{
		logger.debug("Resetting Target Manager.");

		targets.clear();

		for (TargetEntityConfig tarEntCfg : worldCfg.targetCfgs)
		{
			TargetType type = entTypes.getTargetType(tarEntCfg.getTargetType());
			if (type != null)
			{
				Target tgt = new Target(type);
				tgt.getCoordinate().setCoordinate(tarEntCfg.getLocation());
				tgt.getHeading().copy(tarEntCfg.getOrientation());
				tgt.getHeading().normalize360();
				targets.add(tgt);
			}
			else
			{
				logger.error("Target configured with an unknown target type.  Ignoring target.");
			}
		}
	}

	/**
	 * @return An unmodifiable view of all targets in the simulation.
	 */
	public Collection<Target> getAllTargets()
	{
		return Collections.unmodifiableCollection(targets);
	}

	/**
	 * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of time.
	 */
	public void stepSimulation()
	{
		for(Target tgt : targets)
		{
			tgt.stepSimulation();
		}
	}
}
