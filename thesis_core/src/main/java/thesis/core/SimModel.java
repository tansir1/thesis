package thesis.core;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.HavenRouting;
import thesis.core.common.SimTime;
import thesis.core.common.WorldPose;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.UAV;
import thesis.core.uav.UAVMgr;
import thesis.core.uav.comms.CommsConfig;
import thesis.core.utilities.ISimStepListener;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

public class SimModel
{
   private Logger logger;
   private World world;

   private TargetMgr tgtMgr;
   private UAVMgr uavMgr;

   private EntityTypeCfgs entTypes;

   private List<ISimStepListener> stepListeners;

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
      world = new World();

      stepListeners = new CopyOnWriteArrayList<ISimStepListener>();
   }

   /**
    * Initialize the model with the necessary configuration parameters.
    *
    * @param randomSeed
    *           Initialize the random number generator with this value.
    * @param worldCfg
    *           Configuration data for the world.
    * @param entTypes
    *           The types of entities within the world.
    */
   public void reset(int randomSeed, WorldConfig worldCfg, EntityTypeCfgs entTypes, float commsRngPercent,
         float commsRelayProb)
   {
      randGen = new Random(randomSeed);

      this.entTypes = entTypes;

      logger.debug("EntityTypes initialized with:\n{}", entTypes);
      logger.debug("World model intiliazed with:\n{}", worldCfg);

      world.copy(worldCfg.getWorld());

      HavenRouting havenRouting = new HavenRouting(world, randGen);
      tgtMgr.reset(entTypes.getTgtTypeCfgs(), worldCfg.getTargetCfgs(), havenRouting, world.getWorldGIS());

      resetUAVs(worldCfg, commsRngPercent, commsRelayProb);
   }

   private void resetUAVs(WorldConfig worldCfg, float commsRngPercent,
         float commsRelayProb)
   {
      final double maxComsRng = world.getWorldGIS().getMaxWorldDistance() * commsRngPercent;

      final CommsConfig commsCfg = new CommsConfig();
      commsCfg.setCommsRelayProb(commsRelayProb);
      commsCfg.setMaxCommsRng(maxComsRng);
      // FIXME Load/Derive the number of hops?
      commsCfg.setMaxRelayHops(5);

      uavMgr.reset(entTypes, worldCfg.getUAVCfgs(), tgtMgr, randGen, commsCfg);

      // TEMPORARY! Initializes all UAVs with a pose to fly to for development
      // testing purposes.
      for (UAV uav : uavMgr.getAllUAVs())
      {
         WorldPose pose = new WorldPose();

         double north = randGen.nextDouble() * world.getWorldGIS().getWidth();
         double east = randGen.nextDouble() * world.getWorldGIS().getHeight();

         pose.getCoordinate().setCoordinate(north, east);
         pose.setHeading(randGen.nextInt(360));
         uav.TEMP_setDestination(pose);

         // Temporary sensor stare point
         uav.getSensors().stareAtAll(pose.getCoordinate());
      }
   }

   public World getWorld()
   {
      return world;
   }

   public WorldGIS getWorldGIS()
   {
      return world.getWorldGIS();
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
    * Step the simulation forward by one frame's worth of time.
    *
    * @param deltaTimeMS
    *           Advance the simulation forward by this many milliseconds.
    *
    * @see #SIM_STEP_RATE_MS
    */
   public void stepSimulation()
   {
      logger.trace("-------------------Simulation stepping.-----------------");
      SimTime.stepSimulation();
      final long start = System.nanoTime() / 1000000;

      tgtMgr.stepSimulation();
      uavMgr.stepSimulation();

      final long end = System.nanoTime() / 1000000;
      SimTime.incrementWallTime(end - start);

      for(ISimStepListener listener : stepListeners)
      {
         listener.onSimulationStep();
      }
   }

   public EntityTypeCfgs getEntityTypeCfgs()
   {
      return entTypes;
   }

   public void addStepListener(ISimStepListener listener)
   {
      stepListeners.add(listener);
   }

   public void removeStepListener(ISimStepListener listener)
   {
      stepListeners.remove(listener);
   }
}
