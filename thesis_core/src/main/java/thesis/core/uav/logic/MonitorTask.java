package thesis.core.uav.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.Pathing;
import thesis.core.uav.logic.MonitorPathingHelper.PathingState;
import thesis.core.utilities.LoggerIDs;

public class MonitorTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   /**
    * The amount of time required to stare at a target before it is considered
    * confirmed.
    */
   public static final long MILLISECONDS_TO_CONFIRM = 1000 * 10;

   /**
    * The amount of time required to stare at a target before BDA is complete.
    */
   public static final long MILLISECONDS_TO_BDA = 1000 * 10;

   public enum TaskLogicState
   {
      NO_TASK, Confirm, PendingAttack, BDA
   }

   private TaskLogicState logicState;

   private int hostUavId;

   private TargetBelief tgtBelief;

   private WorldCoordinate starePoint;

   /**
    * Starting sim time (ms) of when focused staring at the target began.
    */
   private long stareStartTime;

   private MonitorPathingHelper pathingHelper;

   private boolean resyncDestCoord;

   public MonitorTask(int hostUavId)
   {
      this.hostUavId = hostUavId;

      resyncDestCoord = false;
      tgtBelief = null;
      pathingHelper = new MonitorPathingHelper();

      logicState = TaskLogicState.NO_TASK;
      starePoint = new WorldCoordinate();
   }

   /**
    * @param trueTgtID
    *           Assumes cross-track correlation amongst the swarm. This is the
    *           Id of the 'track' to confirm.
    */
   public void reset(TargetBelief tgtBelief, TaskLogicState logicState, SensorGroup snsrGrp)
   {

      this.logicState = logicState;
      resyncDestCoord = true;

      //Clear out any left over focused scan logic from previous sensor state
      snsrGrp.setFocusedScanning(false);
      stareStartTime = 0;

      starePoint.setCoordinate(tgtBelief.getCoordinate());
      snsrGrp.stareAtAll(starePoint);

      switch(logicState)
      {
      case BDA:
         break;
      case Confirm:
      {
         logger.trace("UAV {} reseting to confirm target {}.", hostUavId, tgtBelief.getTrueTargetID());
      }
         break;
      case NO_TASK:
         break;
      case PendingAttack:
      {
         logger.debug("UAV {} reseting to pending attack of target {}", hostUavId, tgtBelief.getTrueTargetID());
      }
         break;
      }
   }

   public PathingState getState()
   {
      return pathingHelper.getState();
   }

   public void stepSimulation(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      pathingHelper.stepSimulation(tgtBelief, pathing, snsrGrp, resyncDestCoord);

      if(resyncDestCoord)
      {
         resyncDestCoord = false;
      }

      switch(logicState)
      {
      case BDA:
         stepBDA(tgtBelief, pathing, snsrGrp);
         break;
      case Confirm:
         stepConfirm(tgtBelief, pathing, snsrGrp);
         break;
      case NO_TASK:
         break;
      case PendingAttack:
         break;
      }
   }

   private void stepBDA(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      if(stareStartTime == 0 && pathingHelper.isInSensorRange())
      {
         logger.debug("UAV {} began BDA focused scanning target {}", hostUavId, tgtBelief.getTrueTargetID());
         stareStartTime = SimTime.getCurrentSimTimeMS();
         snsrGrp.setFocusedScanning(true);
      }

      if ((SimTime.getCurrentSimTimeMS() - stareStartTime) >= MILLISECONDS_TO_BDA)
      {
         snsrGrp.setFocusedScanning(false);
         logger.debug("UAV {} finished BDA scanning target {}", hostUavId, tgtBelief.getTrueTargetID());
         //reset(tgtBelief, TaskLogicState.PendingAttack, snsrGrp);

         //TODO IF destroyed go to NO_TASK else request another attack
      }
   }

   private void stepConfirm(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      if(stareStartTime == 0 && pathingHelper.isInSensorRange())
      {
         logger.debug("UAV {} began confirmation focused scanning target {}", hostUavId, tgtBelief.getTrueTargetID());
         stareStartTime = SimTime.getCurrentSimTimeMS();
         snsrGrp.setFocusedScanning(true);
      }

      if ((SimTime.getCurrentSimTimeMS() - stareStartTime) >= MILLISECONDS_TO_CONFIRM)
      {
         snsrGrp.setFocusedScanning(false);
         logger.debug("UAV {} finished confirming target {}", hostUavId, tgtBelief.getTrueTargetID());
         reset(tgtBelief, TaskLogicState.PendingAttack, snsrGrp);
      }
   }

   private void stepPendingAttack(TargetBelief tgtBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      //TODO Has target been attack? If so, start BDA
   }
}
