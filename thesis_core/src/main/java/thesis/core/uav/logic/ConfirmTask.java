package thesis.core.uav.logic;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.targets.Target;
import thesis.core.uav.Pathing;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class ConfirmTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   /**
    * If the target moves from the original confirmation coordinate by this
    * percentage of the sensor range then re-compute a route to the target.
    */
   private static final double SENSOR_DISTANCE_REROUTE_PERCENT = 0.1;

   /**
    * Once the host uav is within this percentage of its max sensor range to the
    * target start orbiting the target.
    */
   private static final double SENSOR_DISTANCE_ORBIT_PERCENT = 0.7;

   /**
    * The amount of time required to stare at a target before it is considered confirmed.
    */
   public static final long MILLISECONDS_TO_CONFIRM = 1000 * 10;

   private enum State
   {
      Init, EnRoute, Orbiting, Complete
   }

   private WorldGIS gis;
   private int hostUavId;
   private Random rand;

   private int trueTgtID;
   private State state;
   private WorldCoordinate confirmCoord;

   /**
    * Starting sim time (ms) of when the target was in sensor confirmation distance.
    */
   private long confirmStareStartTime;

   public ConfirmTask(int hostUavId, WorldGIS gis, Random randGen)
   {
      this.hostUavId = hostUavId;
      this.gis = gis;
      this.rand = randGen;

      trueTgtID = Target.INVALID_TGT_ID;
      state = State.Init;
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

      state = State.EnRoute;
   }

   private void checkForReRoute(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);

      final double maxRng = snsrGrp.getMaxSensorRange();
      final double reRouteThreshold = maxRng * SENSOR_DISTANCE_REROUTE_PERCENT;

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

      if (Math.abs(pathing.getCoordinate().distanceTo(tb.getCoordinate())) < maxOrbitRng &&
            pathing.computeOrbit(confirmCoord, maxOrbitRng))
      {
         logger.debug("UAV {} orbiting target {}", hostUavId, trueTgtID);
         state = State.Orbiting;
         confirmStareStartTime = SimTime.CURRENT_SIM_TIME_MS;
         //TODO Inform host uav to not scan cells but only the target's location
         //Stop all sensor scan logic, draw circle around target location instead of frustrum???
      }
   }

   private void stepOrbit(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      TargetBelief tb = curBelief.getTargetBelief(trueTgtID);
      final double maxRng = snsrGrp.getMaxSensorRange();
      final double maxOrbitRng = maxRng * SENSOR_DISTANCE_ORBIT_PERCENT;

      checkForReRoute(curBelief, pathing, snsrGrp);

      if (Math.abs(pathing.getCoordinate().distanceTo(tb.getCoordinate())) < maxOrbitRng)
      {

      }
   }
}
