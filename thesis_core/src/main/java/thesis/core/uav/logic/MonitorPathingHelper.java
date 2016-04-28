package thesis.core.uav.logic;

import java.util.ArrayList;
import java.util.List;

import thesis.core.belief.TargetBelief;
import thesis.core.common.Circle;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.Pathing;

public class MonitorPathingHelper
{
   //private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   /**
    * If the target moves from the original confirmation coordinate by this
    * percentage of the sensor range then re-compute a route to the target.
    */
   private static final double SENSOR_DISTANCE_RECOMPUTE_PATH_PERCENT = 0.1;

   /**
    * Once the host uav is within this percentage of its max sensor range to the
    * target start orbiting the target.
    */
   public static double SENSOR_DISTANCE_ORBIT_PERCENT = 0.7;

   /**
    * When the UAV gets within this distance to the next waypoint compute a plot
    * to the waypoint after the next.
    */
   private static final double DIST_THRESHOLD_FOR_NEXT_WAYPOINT = 100;

   /**
    * Number of edges to use to approximate a circle for orbiting the target.
    */
   private static final int NUM_ORBIT_EDGES = 10;

   public enum PathingState
   {
      EnRoute, Orbiting
   }

   private PathingState state;

   private List<WorldCoordinate> orbitRoute;
   private int orbitWyptIdx;

   private WorldCoordinate destination;

   private boolean inSensorRng;

   public MonitorPathingHelper()
   {
      orbitRoute = new ArrayList<WorldCoordinate>();

      state = PathingState.EnRoute;
      orbitWyptIdx = 0;
      inSensorRng = false;

      destination = new WorldCoordinate();
   }

   public PathingState getState()
   {
      return state;
   }

   public boolean isInSensorRange()
   {
      return inSensorRng;
   }

   public void stepSimulation(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp, boolean resyncDest)
   {
      if (resyncDest)
      {
         destination.setCoordinate(tgtBelief.getCoordinate());
         pathing.computePathTo(destination);
         state = PathingState.EnRoute;
      }

      checkSensorRange(tgtBelief, pathing, snsrGrp);

      switch (state)
      {
      case EnRoute:
         stepEnRoute(tgtBelief, pathing, snsrGrp);
         break;
      case Orbiting:
         stepOrbit(tgtBelief, pathing, snsrGrp);
         break;
      }
   }

   private void checkSensorRange(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      final double maxRng = snsrGrp.getMaxSensorRange();
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      if (Math.abs(pathing.getCoordinate().distanceTo(tgtBelief.getCoordinate())) < maxOrbitRng)
      {
         inSensorRng = true;
      }
      else
      {
         inSensorRng = false;
      }
   }

   private void checkForReRoute(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      final double maxRng = snsrGrp.getMaxSensorRange();
      final double reRouteThreshold = maxRng * SENSOR_DISTANCE_RECOMPUTE_PATH_PERCENT;

      if (Math.abs(destination.distanceTo(tgtBelief.getCoordinate())) > reRouteThreshold)
      {
         destination.setCoordinate(tgtBelief.getCoordinate());
         pathing.computePathTo(destination);
      }
   }

   private void stepEnRoute(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      checkForReRoute(tgtBelief, pathing, snsrGrp);

      final double maxRng = snsrGrp.getMaxSensorRange();
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      if (inSensorRng)
      {
         Circle orbit = new Circle();
         orbit.getCenter().setCoordinate(destination);
         orbit.setRadius(maxOrbitRng);
         orbitRoute.clear();
         pathing.generateOrbit(orbit, NUM_ORBIT_EDGES, orbitRoute);

         state = PathingState.Orbiting;
         orbitWyptIdx = 0;
         pathing.computePathTo(orbitRoute.get(0));
      }
   }

   private void checkForReOrbit(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      final double maxRng = snsrGrp.getMaxSensorRange();
      final double reRouteThreshold = maxRng * SENSOR_DISTANCE_RECOMPUTE_PATH_PERCENT;
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      if (Math.abs(destination.distanceTo(tgtBelief.getCoordinate())) > reRouteThreshold)
      {
         destination.setCoordinate(tgtBelief.getCoordinate());

         Circle orbit = new Circle();
         orbit.getCenter().setCoordinate(destination);
         orbit.setRadius(maxOrbitRng);
         orbitRoute.clear();
         pathing.generateOrbit(orbit, NUM_ORBIT_EDGES, orbitRoute);
         orbitWyptIdx = 0;
         pathing.computePathTo(orbitRoute.get(0));
      }
   }

   private void stepOrbit(TargetBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      checkForReOrbit(curBelief, pathing, snsrGrp);

      // If we're close to the destination waypoint then plot a course to the
      // following waypoint.
      if (Math.abs(pathing.getCoordinate().distanceTo(orbitRoute.get(orbitWyptIdx))) < DIST_THRESHOLD_FOR_NEXT_WAYPOINT)
      {
         ++orbitWyptIdx;
         if (orbitWyptIdx >= orbitRoute.size())
         {
            orbitWyptIdx = 0;
         }
         pathing.computePathTo(orbitRoute.get(orbitWyptIdx));
      }
   }
}
