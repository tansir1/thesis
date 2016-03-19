package thesis.core.uav.logic;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.Circle;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.targets.Target;
import thesis.core.uav.Pathing;
import thesis.core.utilities.LoggerIDs;

public class ConfirmTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

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
    * The amount of time required to stare at a target before it is considered
    * confirmed.
    */
   public static final long MILLISECONDS_TO_CONFIRM = 1000 * 10;

   /**
    * When the UAV gets within this distance to the next waypoint compute a plot
    * to the waypoint after the next.
    */
   private static final double DIST_THRESHOLD_FOR_NEXT_WAYPOINT = 100;

   /**
    * Number of edges to use to approximate a circle for orbiting the target.
    */
   private static final int NUM_ORBIT_EDGES = 10;

   public enum State
   {
      Init, EnRoute, Orbiting, Complete
   }

   private int hostUavId;

   private int trueTgtID;
   private State state;
   private WorldCoordinate confirmCoord;

   /**
    * Starting sim time (ms) of when the target was in sensor confirmation
    * distance.
    */
   private long confirmStareStartTime;

   private List<WorldCoordinate> orbitRoute;
   private int orbitWyptIdx;

   public ConfirmTask(int hostUavId)
   {
      this.hostUavId = hostUavId;

      orbitRoute = new ArrayList<WorldCoordinate>();

      trueTgtID = Target.INVALID_TGT_ID;
      state = State.Init;
      orbitWyptIdx = 0;
   }

   /**
    * @param trueTgtID
    *           Assumes cross-track correlation amongst the swarm. This is the
    *           Id of the 'track' to confirm.
    */
   public void reset(int trueTgtID)
   {
      logger.debug("UAV {} reseting to confirm target {}", hostUavId, trueTgtID);
      this.trueTgtID = trueTgtID;
      state = State.Init;

      confirmStareStartTime = 0;
      orbitRoute.clear();
      orbitWyptIdx = 0;
   }

   public State getState()
   {
      return state;
   }

   public void stepSimulation(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      if (trueTgtID == Target.INVALID_TGT_ID)
      {
         // No target to confirm. Abort.
         return;
      }

      switch (state)
      {
      case Init:
         stepInitState(curBelief, pathing, snsrGrp);
         break;
      case EnRoute:
         stepEnRoute(curBelief, pathing, snsrGrp);
         break;
      case Orbiting:
         stepOrbit(curBelief, pathing, snsrGrp);
         break;
      case Complete:
         break;
      }
   }

   private void stepInitState(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      logger.trace("UAV {} initializing confirmation task to target {}.", hostUavId, trueTgtID);
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);
      confirmCoord = tb.getCoordinate();
      pathing.computePathTo(confirmCoord);

      snsrGrp.stareAtAll(confirmCoord);

      //Clear out any left over focused scan logic from previous sensor state
      snsrGrp.setFocusedScanning(false);

      state = State.EnRoute;
   }

   private void checkForReRoute(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);

      final double maxRng = snsrGrp.getMaxSensorRange();
      final double reRouteThreshold = maxRng * SENSOR_DISTANCE_RECOMPUTE_PATH_PERCENT;

      if (Math.abs(confirmCoord.distanceTo(tb.getCoordinate())) > reRouteThreshold)
      {
         confirmCoord = tb.getCoordinate();
         pathing.computePathTo(confirmCoord);
      }
   }

   private void stepEnRoute(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);

      checkForReRoute(curBelief, pathing, snsrGrp);

      final double maxRng = snsrGrp.getMaxSensorRange();
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      if (Math.abs(pathing.getCoordinate().distanceTo(tb.getCoordinate())) < maxOrbitRng)
      {
         logger.debug("UAV {} orbiting target {}", hostUavId, trueTgtID);

         Circle orbit = new Circle();
         orbit.getCenter().setCoordinate(confirmCoord);
         orbit.setRadius(maxOrbitRng);
         orbitRoute.clear();
         pathing.generateOrbit(orbit, NUM_ORBIT_EDGES, orbitRoute);

         state = State.Orbiting;
         orbitWyptIdx = 0;
         pathing.computePathTo(orbitRoute.get(0));
         confirmStareStartTime = SimTime.CURRENT_SIM_TIME_MS;

         snsrGrp.setFocusedScanning(true);
      }
   }

   private void checkForReOrbit(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);

      final double maxRng = snsrGrp.getMaxSensorRange();
      final double reRouteThreshold = maxRng * SENSOR_DISTANCE_RECOMPUTE_PATH_PERCENT;
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      if (Math.abs(confirmCoord.distanceTo(tb.getCoordinate())) > reRouteThreshold)
      {
         confirmCoord = tb.getCoordinate();

         Circle orbit = new Circle();
         orbit.getCenter().setCoordinate(confirmCoord);
         orbit.setRadius(maxOrbitRng);
         orbitRoute.clear();
         pathing.generateOrbit(orbit, NUM_ORBIT_EDGES, orbitRoute);
         orbitWyptIdx = 0;
         pathing.computePathTo(orbitRoute.get(0));
      }
   }

   private void stepOrbit(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {

      if ((SimTime.CURRENT_SIM_TIME_MS - confirmStareStartTime) >= MILLISECONDS_TO_CONFIRM)
      {
         snsrGrp.setFocusedScanning(false);
         state = State.Complete;
         logger.debug("UAV {} finished confirming target {}", hostUavId, trueTgtID);
         // Reset pathing to something?
         // FIXME Trigger the next auction
         return;
      }


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
