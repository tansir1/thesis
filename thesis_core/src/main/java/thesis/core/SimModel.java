package thesis.core;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.HavenRouting;
import thesis.core.common.SimTime;
import thesis.core.common.SimTimeState;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.UAVMgr;
import thesis.core.uav.comms.CommsConfig;
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

   private StatResults results;
   
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
      
      results = new StatResults();
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
   public void reset(int randomSeed, WorldConfig worldCfg, EntityTypeCfgs entTypes, double commsRngPercent,
         double commsRelayProb, double beliefDecayRate)
   {
      randGen = new Random(randomSeed);

      this.entTypes = entTypes;

      logger.debug("EntityTypes initialized with:\n{}", entTypes);
      logger.debug("World model intiliazed with:\n{}", worldCfg);

      world.copy(worldCfg.getWorld());

      HavenRouting havenRouting = new HavenRouting(world, randGen);
      tgtMgr.reset(entTypes.getTgtTypeCfgs(), worldCfg.getTargetCfgs(), havenRouting, world.getWorldGIS());

      resetUAVs(worldCfg, commsRngPercent, commsRelayProb, beliefDecayRate);
      
      results.reset(world, tgtMgr);
   }

   private void resetUAVs(WorldConfig worldCfg, double commsRngPercent, double commsRelayProb, double beliefDecayRate)
   {
      final double maxComsRng = world.getWorldGIS().getMaxWorldDistance() * commsRngPercent;

      final CommsConfig commsCfg = new CommsConfig();
      commsCfg.setCommsRelayProb(commsRelayProb);
      commsCfg.setMaxCommsRng(maxComsRng);
      // FIXME Load/Derive the number of hops?
      commsCfg.setMaxRelayHops(5);

      uavMgr.reset(entTypes, worldCfg.getUAVCfgs(), tgtMgr, randGen, commsCfg, worldCfg.getWorld().getWorldGIS(), beliefDecayRate);

      // TEMPORARY! Initializes all UAVs with a pose to fly to for development
      // testing purposes.
      /*for (UAV uav : uavMgr.getAllUAVs())
      {
         WorldPose pose = new WorldPose();

         double north = randGen.nextDouble() * world.getWorldGIS().getWidth();
         double east = randGen.nextDouble() * world.getWorldGIS().getHeight();

         pose.getCoordinate().setCoordinate(north, east);
         pose.setHeading(randGen.nextInt(360));
         uav.TEMP_setDestination(pose);

         // Temporary sensor stare point
         uav.getSensors().stareAtAll(pose.getCoordinate());
      }*/
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
   public boolean stepSimulation()
   {
      logger.trace("-------------------Simulation stepping.-----------------");
      SimTime.stepSimulation();
      final long start = System.nanoTime() / 1000000;

      tgtMgr.stepSimulation();
      uavMgr.stepSimulation();

      results.stepSimulation();
      
      boolean simFinished = results.endStateReached();
      if(simFinished)
      {
         logger.info("Simulation complete.");
      }
      
      final long end = System.nanoTime() / 1000000;
      SimTime.incrementWallTime(end - start);
      
      return simFinished;
   }

   public EntityTypeCfgs getEntityTypeCfgs()
   {
      return entTypes;
   }

   public SimTimeState getSimTimeState()
   {
      return SimTime.getTimeState();
   }
   
   public StatResults getResults()
   {
      return results;
   }
}
