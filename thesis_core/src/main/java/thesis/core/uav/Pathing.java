package thesis.core.uav;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.Angle;
import thesis.core.common.Circle;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.uav.dubins.DubinsPath;
import thesis.core.uav.dubins.DubinsPathGenerator;
import thesis.core.uav.dubins.PathPhase;
import thesis.core.uav.dubins.PathType;
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
   private static double TRAIL_SAMPLE_INTERVAL_MS = 250;

   /**
    * Only store this much time worth of trail data. Drop anything older.
    */
   private static double TRAIL_TIME_LIMIT_MS = 75000;

   private static int MAX_TRAIL_ENTRIES = (int) (TRAIL_TIME_LIMIT_MS / TRAIL_SAMPLE_INTERVAL_MS);

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

   private double lastTrailSampleTimeAccumulator;
   private List<WorldPose> pathTrail;

   private int numFramesToWypt;
   private int uavID;

   public Pathing(int id, int type, UAVTypeConfigs uavTypeCfgs)
   {
      this.uavID = id;

      minTurnRadius = uavTypeCfgs.getTurnRadius(type);
      frameSpd = uavTypeCfgs.getFrameSpd(type);

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
   public void setHeading(double hdg)
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

            if (pathTrail.size() > MAX_TRAIL_ENTRIES)
            {
               pathTrail.remove(0);
            }
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

   public void teleportTo(WorldCoordinate wc)
   {
      logger.info("Teleporting uav {} to {}", uavID, wc);
      pose.getCoordinate().setCoordinate(wc);
      if (path != null)
      {
         computePathTo(path.getEndPose());
      }
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
      pose.setHeading(Math.toDegrees(hdgRads));

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
      resetPath(DubinsPathGenerator.generate(minTurnRadius, pose, flyTo));
   }

   public void computePathTo(final WorldCoordinate flyTo)
   {
      double bearingTo = pose.getCoordinate().bearingTo(flyTo);
      WorldPose destPose = new WorldPose(flyTo, bearingTo);
      computePathTo(destPose);
   }
   
   public void computePathByDistance(final WorldPose option1, final WorldPose option2, boolean shortestPath)
   {
      DubinsPath path1 = DubinsPathGenerator.generate(minTurnRadius, pose, option1);
      DubinsPath path2 = DubinsPathGenerator.generate(minTurnRadius, pose, option2);
      
      if(path1.getPathType() != PathType.NO_PATH && path2.getPathType() != PathType.NO_PATH)
      {
         double path1Len = path1.getPathLength();
         double path2Len = path2.getPathLength();
         if(shortestPath)
         {
            if(path1Len < path2Len)
            {
               resetPath(path1);
            }
            else
            {
               resetPath(path2);
            }            
         }
         else
         {
            if(path1Len > path2Len)
            {
               resetPath(path1);
            }
            else
            {
               resetPath(path2);
            }
         }

      }
      else if(path1.getPathType() != PathType.NO_PATH)
      {
         resetPath(path1);
      }
      else if(path2.getPathType() != PathType.NO_PATH)
      {
         resetPath(path2);
      }
      else
      {
         logger.error("UAV {} could not generate a path given two options.", uavID);
      }
   }
   
   private void resetPath(DubinsPath newPath)
   {
      if (newPath.getPathType() != PathType.NO_PATH)
      {
         path = newPath;
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

   public List<WorldCoordinate> computeOrbit(final Circle orbit, final int numCircleEdges)
   {
      List<WorldCoordinate> route = new ArrayList<WorldCoordinate>(numCircleEdges);
      generateOrbit(orbit, numCircleEdges, route);
      return route;
   }

   private boolean rotateClockWise(final Circle orbit, WorldPose orbitStart)
   {
      // Compute the cross product of orbit-center-to-start and a unit vector
      // pointing along the start vector.  This will tell us if the uav
      // should follow the orbit clockwise or counter clockwise

      double centerToStartAngle = Math.toRadians(orbit.getCenter().bearingTo(orbitStart.getCoordinate()));
      double centerToStartDist = orbit.getCenter().distanceTo(orbitStart.getCoordinate());

      double centerToStartN = Math.sin(centerToStartAngle) * centerToStartDist;
      double centerToStartE = Math.cos(centerToStartAngle) * centerToStartDist;

      double startHdg = Math.toRadians(orbitStart.getHeading());
      // Assume unit vector lengths
      double startN = Math.sin(startHdg);
      double startE = Math.cos(startHdg);

      //Assumes flat Z plane
      double crossProductScalar = (centerToStartE * startN) - (centerToStartN * startE);
      //double crossProductScalar = (startE * centerToStartN) - (startN * centerToStartE);
      return crossProductScalar < 0;
   }

   public void generateOrbit(final Circle orbit, final int numCircleEdges, List<WorldCoordinate> waypoints)
   {
      if (numCircleEdges < 3)
      {
         throw new IllegalArgumentException("numCircleEdges must be >= 3");
      }

      WorldPose orbitStartPose = orbit.minTravelToTangent(pose);

      double startAngle = orbit.getCenter().bearingTo(orbitStartPose.getCoordinate());
      double anglePerSegment = 360 / (double) numCircleEdges;

      if (rotateClockWise(orbit, orbitStartPose))
      {
         anglePerSegment = -anglePerSegment;
      }

      waypoints.add(new WorldCoordinate(orbitStartPose.getCoordinate()));
      for (int i = 1; i < numCircleEdges; ++i)
      {
         double degs = startAngle + (i * anglePerSegment);
         degs = Angle.normalize360(degs);
         double rads = Math.toRadians(startAngle + (i * anglePerSegment));
         double north = Math.sin(rads) * orbit.getRadius() + orbit.getCenter().getNorth();
         double east = Math.cos(rads) * orbit.getRadius() + orbit.getCenter().getEast();

         waypoints.add(new WorldCoordinate(north, east));
      }
   }
}
