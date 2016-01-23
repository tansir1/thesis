package thesis.core.targets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.CellCoordinate;
import thesis.core.common.Rectangle;
import thesis.core.common.WorldPose;
import thesis.core.experimental.TargetTypeConfigs;
import thesis.core.serialization.TargetEntitiesCfg;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.World;

/**
 * High level manager that maintains all targets in the simulation.
 */
public class TargetMgr
{
   private Logger logger;
   private Target[] targets;
   private World world;//Used for coordinate conversions

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
   public void reset(TargetTypeConfigs tgtTypeCfgs, TargetEntitiesCfg tgtEntCfgs, WorldConfig worldCfg, Random randGen, World world)
   {
      this.world = world;

      logger.debug("Resetting Target Manager.");

      final int NUM_TARGETS = tgtEntCfgs.getNumTargets();

      targets = new Target[NUM_TARGETS];

      for(int i=0; i<NUM_TARGETS; ++i)
      {
         int tgtType = tgtEntCfgs.getTargetType(i);
         WorldPose pose = tgtEntCfgs.getTargetPose(i);

         if (tgtTypeCfgs.typeExists(tgtType))
         {
            float tgtSpd = tgtTypeCfgs.getSpeed(tgtType);

            Target tgt = new Target(tgtType, tgtSpd, worldCfg.getRoadNetwork(), worldCfg.getHavens(), randGen,
                  worldCfg.getWorldWidth(), worldCfg.getWorldHeight());
            tgt.getCoordinate().setCoordinate(pose.getCoordinate());
            tgt.setHeading(pose.getHeading());
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
      for(int i=0; i<targets.length; ++i)
      {
         targets[i].stepSimulation();
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

   /**
    * Get all targets within the specified geographic region.
    *
    * @param region
    *           Get all targets within this region.
    * @return A list of targets in the region or an empty list if no targets are
    *         within the region.
    */
   public List<Target> getTargetsInRegion(List<CellCoordinate> region)
   {
      List<Target> inRegion = new ArrayList<Target>();
      CellCoordinate tgtTemp = new CellCoordinate();

      for(CellCoordinate searchCell : region)
      {
         for (Target tar : targets)
         {
            world.convertWorldToCell(tar.getCoordinate(), tgtTemp);

            if (tgtTemp.equals(searchCell))
            {
               inRegion.add(tar);
            }
         }
      }

      return inRegion;
   }

   /**
    * Get all targets within the specified geographic region.
    *
    * @param region
    *           Get all targets within this region.
    * @return A list of targets in the region or an empty list if no targets are
    *         within the region.
    */
   public List<Target> getTargetsInRegion(CellCoordinate region)
   {
      List<Target> inRegion = new ArrayList<Target>();
      CellCoordinate tgtTemp = new CellCoordinate();

      for (Target tar : targets)
      {
         world.convertWorldToCell(tar.getCoordinate(), tgtTemp);

         if (tgtTemp.equals(region))
         {
            inRegion.add(tar);
         }
      }

      return inRegion;
   }
}
