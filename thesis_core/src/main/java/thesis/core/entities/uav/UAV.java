package thesis.core.entities.uav;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.entities.uav.comms.UAVComms;
import thesis.core.entities.uav.dubins.DubinsPath;
import thesis.core.entities.uav.dubins.DubinsPathGenerator;
import thesis.core.entities.uav.dubins.PathPhase;
import thesis.core.entities.uav.dubins.PathType;
import thesis.core.entities.uav.sensors.SensorGroup;
import thesis.core.entities.uav.sensors.SensorType;
import thesis.core.utilities.LoggerIDs;

public class UAV
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV);
   /**
    * When this amount of simulation time elapses the UAV will record its
    * current pose.
    *
    * @see #pathTrail
    */
   private static long TRAIL_SAMPLE_INTERVAL_MS = 250;

   private UAVType type;
   private WorldPose pose;
   private int id;

   private DubinsPath path;
   private PathPhase pathPhase;
   private Distance pathPhaseTraveled;

   private long lastTrailSampleTimeAccumulator;
   private List<WorldPose> pathTrail;

   private int numFramesToWypt;

   private UAVMgr uavMgr;
   private UAVComms comms;
   private Random randGen;

   private SensorGroup sensors;

   public UAV(final UAVType type, int id, final UAVMgr uavMgr, Distance maxCommsRng, int maxRelayHops, Random randGen, float commsRelayProb)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      if(uavMgr == null)
      {
         throw new NullPointerException("UAVMgr cannot be null.");
      }

      if(randGen == null)
      {
         throw new NullPointerException("Randon generator cannot be null.");
      }

      this.id = id;
      this.type = type;
      this.uavMgr = uavMgr;
      this.randGen = randGen;

      comms = new UAVComms(this, uavMgr, maxRelayHops, maxCommsRng, randGen, commsRelayProb);

      pathPhase = null;
      pose = new WorldPose();
      pathPhaseTraveled = new Distance();
      pathTrail = new ArrayList<WorldPose>();
      lastTrailSampleTimeAccumulator = 0;
      numFramesToWypt = 0;

      initSensors();
   }

   public int getID()
   {
      return id;
   }

   public UAVType getType()
   {
      return type;
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   public Angle getHeading()
   {
      return pose.getHeading();
   }

   public WorldPose getPose()
   {
      return pose;
   }

   public PathPhase getCurrentPathPhase()
   {
      return pathPhase;
   }

   /**
    * Step the simulation forward by one frame.
    */
   public void stepSimulation()
   {
      //FIXME This is temporary debug code to prevent the uav from moving after reaching
      //the destination from TEMP_setDestination()
      if(numFramesToWypt > 0)
      {
         // Move the aircraft according to its speed, heading, and turn rate
         stepPhysics();

         lastTrailSampleTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
         if (lastTrailSampleTimeAccumulator > TRAIL_SAMPLE_INTERVAL_MS)
         {
            lastTrailSampleTimeAccumulator = 0;
            WorldPose curPose = new WorldPose(pose);
            pathTrail.add(curPose);
         }
      }
      // Check if the aircraft needs to start heading towards a new location
      checkPathPhaseTransition();

      comms.stepSimulation();
      sensors.stepSimulation(pose.getCoordinate());
   }

   public DubinsPath getFlightPath()
   {
      return path;
   }

   /**
    * Get a copy of the list of poses that the UAV has reached while following
    * its current flight path.
    *
    * @return The poses the UAV reached sampled along its current flight path.
    */
   public void getFlightHistoryTrail(List<WorldPose> retVal)
   {
      retVal.addAll(pathTrail);
   }

   public UAVComms getComms()
   {
      return comms;
   }

   public SensorGroup getSensors()
   {
      return sensors;
   }

   private void stepPhysics()
   {
      //We're one frame closer to the next waypoint so decrement the counter
      --numFramesToWypt;

      final double frameSpdMpS = type.getFrameSpd().asMeterPerSecond();

      double turnCoeff = 0;
      switch (path.getPathType().getSegmentType(pathPhase))
      {
      case Left:
      {
         turnCoeff = 1.0;
      }
         break;
      case Right:
      {
         turnCoeff = -1.0;
      }
         break;
      case Straight:
      {
         turnCoeff = 0;
      }
         break;
      }

      double turnRate = turnCoeff * (frameSpdMpS / type.getMinTurnRadius().asMeters());

      pose.getHeading().setAsRadians(pose.getHeading().asRadians() + turnRate);

      final Distance northing = new Distance();
      final Distance easting = new Distance();
      northing.setAsMeters(frameSpdMpS * pose.getHeading().sin());
      easting.setAsMeters(frameSpdMpS * pose.getHeading().cos());
      pose.getCoordinate().translate(northing, easting);

      pose.getHeading().normalize360();
      //logger.trace("{}", this);
   }

   /**
    * This is a temporary method for development testing purposes. It will be
    * deleted once aircraft have a means of selecting their own destinations.
    *
    * @param flyTo
    */
   public void TEMP_setDestination(final WorldPose flyTo)
   {
      computePathTo(flyTo);
   }

   private void computePathTo(final WorldPose flyTo)
   {
      path = DubinsPathGenerator.generate(type.getMinTurnRadius(), pose, flyTo);
      if (path.getPathType() != PathType.NO_PATH)
      {
         pathPhase = PathPhase.Phase1;
         pathPhaseTraveled.setAsMeters(0);
         pathTrail.clear();
         lastTrailSampleTimeAccumulator = 0;

         resetFramesToWaypoint();
      }
      else
      {
         logger.error("UAV {} could not generate a path.", id);
      }
   }

   private void resetFramesToWaypoint()
   {
      final double frameVelocityMpS = type.getMaxSpd().asMeterPerSecond() * (SimTime.SIM_STEP_RATE_MS / 1000.0);
      final double distToWypt = path.getSegmentLength(pathPhase).asMeters();
      numFramesToWypt = (int) (distToWypt / frameVelocityMpS) + 1;
   }

   private void checkPathPhaseTransition()
   {
      //pathPhase is null when no destination is set
      if(numFramesToWypt <= 0 && pathPhase != null)
      {
         switch (pathPhase)
         {
         case Phase1:
            pathPhase = PathPhase.Phase2;
            logger.trace("UAV {} reached waypoint 1, moving towards waypoint 2.", id);
            break;
         case Phase2:
            pathPhase = PathPhase.Phase3;
            logger.trace("UAV {} reached waypoint 2, moving towards waypoint 3.", id);
            break;
         case Phase3:
            logger.trace("UAV {} reached waypoint 3.", id);
            break;
         }
         resetFramesToWaypoint();
      }
   }

   private void initSensors()
   {
      sensors = new SensorGroup();
      for(SensorType st : type.getSensors())
      {
         sensors.addSensor(st);
      }
   }

   @Override
   public String toString()
   {
      return Integer.toString(id) + ": " + pose.toString();
   }
}
