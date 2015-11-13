package thesis.core.entities.uav;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.LinearSpeed;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.entities.uav.dubins.DubinsPath;
import thesis.core.entities.uav.dubins.DubinsPathGenerator;
import thesis.core.entities.uav.dubins.PathPhase;
import thesis.core.entities.uav.dubins.PathType;
import thesis.core.utilities.LoggerIDs;

public class UAV
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);

   private UAVType type;
   private WorldPose pose;
   private int id;

   private DubinsPath path;
   private PathPhase pathPhase;
   private Distance pathPhaseTraveled;

   public UAV(UAVType type, int id)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.id = id;
      this.type = type;
      pathPhase = null;
      pose = new WorldPose();
      pathPhaseTraveled = new Distance();
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
    * Step the simulation forward by the requested amount of time.
    *
    * @param deltaTimeMS
    *           Advance the simulation forward by this many milliseconds.
    */
   public void stepSimulation(long deltaTimeMS)
   {
      // Move the aircraft according to its speed, heading, and turn rate
      stepPhysics(deltaTimeMS);
      // Check if the aircraft needs to start heading towards a new location
      checkPathPhaseTransition();
   }

   public DubinsPath getFlightPath()
   {
      return path;
   }
   
   private void stepPhysics(long deltaTimeMS)
   {
      final Angle hdg = pose.getHeading();
      final double deltaSeconds = deltaTimeMS / 1000.0;

      switch (path.getPathType().getSegmentType(pathPhase))
      {
      case Left:
      {
         double angularDelta = type.getMaxTurnRt().asRadiansPerSecond() * deltaSeconds;
         hdg.setAsRadians(hdg.asRadians() + angularDelta);
      }
         break;
      case Right:
      {
         double angularDelta = type.getMaxTurnRt().asRadiansPerSecond() * deltaSeconds;
         hdg.setAsRadians(hdg.asRadians() - angularDelta);
      }
         break;
      case Straight:
         // Do not change heading
         break;
      }
      hdg.normalize360();

      final Distance northing = new Distance();
      final Distance easting = new Distance();

      final LinearSpeed spd = type.getMaxSpd();

      double metersTraveled = spd.asMeterPerSecond() * deltaSeconds;
      pathPhaseTraveled.setAsMeters(pathPhaseTraveled.asMeters() + metersTraveled);

      // east distance = time * speed * east component
      easting.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * hdg.cosNorthUp());
      // north distance = time * speed * north component
      northing.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * hdg.sinNorthUp());

      pose.getCoordinate().translate(northing, easting);
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
      }
      else
      {
         logger.error("UAV {} could not generate a path.", id);
      }
   }

   private void checkPathPhaseTransition()
   {
      final WorldCoordinate nextWypt = path.getWaypoint(pathPhase);
      final Distance distToWypt = pose.getCoordinate().distanceTo(nextWypt);

      final double travelPercent = pathPhaseTraveled.asMeters() / path.getSegmentLength(pathPhase).asMeters();

      // UAV must be near the waypoint and traveled more than 90% of the segment
      // length. This prevents
      // a UAV from thinking it has reached the waypoint when it actually needs
      // to overshoot it and
      // turn around to reach the necessary approach angle.
      if (distToWypt.asMeters() < type.getWaypointReachTolerance().asMeters() && travelPercent > 0.9)
      {
         switch (pathPhase)
         {
         case Phase1:
            pathPhase = PathPhase.Phase2;
            pathPhaseTraveled.setAsFeet(0);
            break;
         case Phase2:
            pathPhase = PathPhase.Phase3;
            pathPhaseTraveled.setAsFeet(0);
            break;
         case Phase3:
            // Do nothing
            break;
         }
      }
   }

}
