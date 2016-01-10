package thesis.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.Rectangle;
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
   private Target[] targets;

   public TargetMgr()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      targets = null;
   }

   /**
    * Initialize the target manager with a collection of targets derived from
    * the given configuration data.
    *
    * @param entTypes
    *           Target types will be queried from this.
    * @param worldCfg
    *           Targets will be generated based on configuration data from here
    *           and types will be cross referenced from entTypes.
    */
   public void reset(EntityTypes entTypes, WorldConfig worldCfg, Random randGen)
   {
      logger.debug("Resetting Target Manager.");

      final int NUM_TARGETS = worldCfg.targetCfgs.size();

      targets = new Target[NUM_TARGETS];

      TargetEntityConfig tarEntCfg = null;
      for(int i=0; i<NUM_TARGETS; ++i)
      {
         tarEntCfg = worldCfg.targetCfgs.get(i);
         TargetType type = entTypes.getTargetType(tarEntCfg.getTargetType());
         if (type != null)
         {
            Target tgt = new Target(type, worldCfg.getRoadNetwork(), worldCfg.getHavens(), randGen,
                  worldCfg.getWorldWidth(), worldCfg.getWorldHeight());
            tgt.getCoordinate().setCoordinate(tarEntCfg.getLocation());
            tgt.setHeading(tarEntCfg.getOrientation());
            targets[i]=tgt;
         }
         else
         {
            logger.error("Target configured with an unknown target type.  Ignoring target.");
         }
      }
   }

   public Target[] getAllTargets()
   {
      return targets;
   }

   /**
    * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of
    * time.
    */
   public void stepSimulation()
   {
      for (Target tgt : targets)
      {
         tgt.stepSimulation();
      }
   }

   /**
    * Get all targets within the specified geographic region.
    *
    * @param region
    *           Get all targets within this region.
    * @return A list of targets in the region or an empty list if no targets are
    *         within the region.
    */
   public List<Target> getTargetsInRegion(Rectangle region)
   {
      List<Target> inRegion = new ArrayList<Target>();
      for (Target tar : targets)
      {
         if (region.isCoordinateInRegion(tar.getCoordinate()))
         {
            inRegion.add(tar);
         }
      }
      return inRegion;
   }
}
