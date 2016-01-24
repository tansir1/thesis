package thesis.core.entities.uav;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.entities.uav.dubins.DubinsPath;
import thesis.core.entities.uav.dubins.DubinsPathGenerator;
import thesis.core.entities.uav.dubins.PathPhase;
import thesis.core.entities.uav.dubins.PathType;
import thesis.core.utilities.LoggerIDs;

public class Pathing
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_PATHING);

   /**
    * When this amount of simulation time elapses the UAV will record its
    * current pose.
    *
    * @see #pathTrail
    */
   private static long TRAIL_SAMPLE_INTERVAL_MS = 250;

   /**
    * The radius required for the UAV to turn 180 degrees.
    */
   private final double minTurnRadius;

   /**
    * Speed of the UAV in meters/frame.
    */
   private final double frameSpd;


   private WorldPose pose;

   private DubinsPath path;
   private PathPhase pathPhase;

   private long lastTrailSampleTimeAccumulator;
   private List<WorldPose> pathTrail;

   private int numFramesToWypt;
   private int uavID;

   public Pathing(int id, UAVType type)
   {
      this.uavID = id;

      minTurnRadius = type.getMinTurnRadius();
      frameSpd = type.getFrameSpd();

      pathPhase = null;
      pose = new WorldPose();
      pathTrail = new ArrayList<WorldPose>();
      lastTrailSampleTimeAccumulator = 0;
      numFramesToWypt = 0;
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   /**
    * @return The UAV's heading in degrees.
    */
   public double getHeading()
   {
      return pose.getHeading();
   }

   /**
    * @param hdg
    *           The UAV's heading in degrees.
    */
   public void setHeading(float hdg)
   {
      pose.setHeading(hdg);
   }

   public WorldPose getPose()
   {
      return pose;
   }

   /**
    * Step the simulation forward by one frame.
    */
   public void stepSimulation()
   {
      // FIXME This is temporary debug code to prevent the uav from moving after
      // reaching
      // the destination from TEMP_setDestination()
      if (numFramesToWypt > 0)
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

   private void stepPhysics()
   {
      // We're one frame closer to the next waypoint so decrement the counter
      --numFramesToWypt;

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

      double turnRate = turnCoeff * (frameSpd / minTurnRadius);

      double hdgRads = Math.toRadians(pose.getHeading()) + turnRate;
      pose.setHeading((float) Math.toDegrees(hdgRads));

      final double northing = frameSpd * Math.sin(Math.toRadians(pose.getHeading()));
      final double easting = frameSpd * Math.cos(Math.toRadians(pose.getHeading()));
      pose.getCoordinate().translateCart(northing, easting);

      // logger.trace("{}", this);
   }

   private void checkPathPhaseTransition()
   {
      // pathPhase is null when no destination is set
      if (numFramesToWypt <= 0 && pathPhase != null)
      {
         switch (pathPhase)
         {
         case Phase1:
            pathPhase = PathPhase.Phase2;
            logger.trace("UAV {} reached waypoint 1, moving towards waypoint 2.", uavID);
            break;
         case Phase2:
            pathPhase = PathPhase.Phase3;
            logger.trace("UAV {} reached waypoint 2, moving towards waypoint 3.", uavID);
            break;
         case Phase3:
            logger.trace("UAV {} reached waypoint 3.", uavID);
            break;
         }
         resetFramesToWaypoint();
      }
   }

   private void resetFramesToWaypoint()
   {
      final double distToWypt = path.getSegmentLength(pathPhase);
      numFramesToWypt = (int) (distToWypt / frameSpd) + 1;
   }

   public void computePathTo(final WorldPose flyTo)
   {
      path = DubinsPathGenerator.generate(minTurnRadius, pose, flyTo);
      if (path.getPathType() != PathType.NO_PATH)
      {
         pathPhase = PathPhase.Phase1;
         pathTrail.clear();
         lastTrailSampleTimeAccumulator = 0;

         resetFramesToWaypoint();
      }
      else
      {
         logger.error("UAV {} could not generate a path.", uavID);
      }
   }
}
