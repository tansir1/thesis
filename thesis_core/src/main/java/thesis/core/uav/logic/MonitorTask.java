package thesis.core.uav.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.common.SimTime;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.targets.ITrueTargetStatusProvider;
import thesis.core.uav.Pathing;
import thesis.core.uav.UAV;
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

   /**
    * The amount of time a UAV will wait for a pending attack before dropping the task.
    */
   public static final long MILLISECONDS_TO_DROP_PENDING = 1000 * 120;
   
   public enum TaskLogicState
   {
      NO_TASK, Confirm, PendingAttack, BDA
   }

   private TaskLogicState logicState;

   private int hostUavId;

   private WorldCoordinate starePoint;

   /**
    * Starting sim time (ms) of when focused staring at the target began.
    */
   private long stareStartTime;

   private MonitorPathingHelper pathingHelper;

   private boolean resyncDestCoord;
   private ITrueTargetStatusProvider trueTgtStatSvc;
   
   /**
    * Prevent a race condition between timestamps of attack requests vs receiver believing it's complete and not merging.
    */
   private boolean sendAttackRequest;
   private int numRepeatRequests;

   public MonitorTask(int hostUavId, ITrueTargetStatusProvider trueTgtStatSvc)
   {
      this.hostUavId = hostUavId;
      this.trueTgtStatSvc = trueTgtStatSvc;

      resyncDestCoord = false;
      pathingHelper = new MonitorPathingHelper();

      logicState = TaskLogicState.NO_TASK;
      starePoint = new WorldCoordinate();
      
      sendAttackRequest = false;
      numRepeatRequests = 0;
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

      // Clear out any left over focused scan logic from previous sensor state
      snsrGrp.setFocusedScanning(false);
      stareStartTime = -1;

      starePoint.setCoordinate(tgtBelief.getCoordinate());
      snsrGrp.stareAtAll(starePoint);

      sendAttackRequest = false;
      numRepeatRequests = 0;
      
      switch (logicState)
      {
      case BDA:
      {
         logger.debug("UAV {} reseting to BDA of target {}.", hostUavId, tgtBelief.getTrueTargetID());
      }
         break;
      case Confirm:
      {
         logger.debug("UAV {} reseting to confirm target {}.", hostUavId, tgtBelief.getTrueTargetID());
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

      if (resyncDestCoord)
      {
         resyncDestCoord = false;
      }

      switch (logicState)
      {
      case BDA:
         stepBDA(tgtBelief, snsrGrp);
         break;
      case Confirm:
         stepConfirm(tgtBelief, snsrGrp);
         break;
      case NO_TASK:
         break;
      case PendingAttack:
         stepPendingAttack(tgtBelief, snsrGrp);
         break;
      }
      
//      if((logicState == TaskLogicState.Confirm || logicState == TaskLogicState.NO_TASK) &&
//            !trueTgtStatSvc.isAlive(tgtBelief.getTrueTargetID()))
//      {
//         logger.debug("UAV {} missed attack complete message for target {}, breaking out of monitor.", hostUavId, tgtBelief.getTrueTargetID());
//         reset(tgtBelief, TaskLogicState.NO_TASK, snsrGrp);
//      }
   }

   private void stepBDA(TargetBelief tgtBelief, SensorGroup snsrGrp)
   {
      long totalStareTime = SimTime.getCurrentSimTimeMS() - stareStartTime;
      if(pathingHelper.isInSensorRange() && stareStartTime < 0)
      {
         logger.debug("UAV {} began BDA focused scanning target {}", hostUavId, tgtBelief.getTrueTargetID());
         stareStartTime = SimTime.getCurrentSimTimeMS();
         snsrGrp.setFocusedScanning(true);
      }
      else if (stareStartTime > 0 && totalStareTime >= MILLISECONDS_TO_BDA)
      {
         if(!trueTgtStatSvc.isAlive(tgtBelief.getTrueTargetID()))
         {
            logger.debug("UAV {} finished BDA scanning target {}.  It is destroyed.", hostUavId, tgtBelief.getTrueTargetID());
            tgtBelief.getTaskStatus().setMonitorUAV(UAV.NULL_UAV_ID);
            tgtBelief.getTaskStatus().setMonitorUAVScore(-1);
            tgtBelief.getTaskStatus().setMonitorState(TaskState.Complete);
            tgtBelief.getTaskStatus().setMonitorUpdateTimestamp(SimTime.getCurrentSimTimeMS());
            tgtBelief.getTaskStatus().setDestroyed(true);
            reset(tgtBelief, TaskLogicState.NO_TASK, snsrGrp);
         }
         else
         {
            logger.debug("UAV {} finished BDA scanning target {}.  It is NOT destroyed.", hostUavId, tgtBelief.getTrueTargetID());
            requestAttack(tgtBelief);
            reset(tgtBelief, TaskLogicState.PendingAttack, snsrGrp);
         }
      }
   }

   private void stepConfirm(TargetBelief tgtBelief, SensorGroup snsrGrp)
   {     
      long totalStareTime = SimTime.getCurrentSimTimeMS() - stareStartTime;
      if (pathingHelper.isInSensorRange() && stareStartTime < 0)
      {
         logger.debug("UAV {} began confirmation focused scanning target {}", hostUavId, tgtBelief.getTrueTargetID());
         tgtBelief.getTaskStatus().setMonitorState(TaskState.Performing);
         stareStartTime = SimTime.getCurrentSimTimeMS();
         snsrGrp.setFocusedScanning(true);
      }
      else if (stareStartTime > 0 && totalStareTime >= MILLISECONDS_TO_CONFIRM)
      {
         logger.debug("UAV {} finished confirming target {}", hostUavId, tgtBelief.getTrueTargetID());
         
         reset(tgtBelief, TaskLogicState.PendingAttack, snsrGrp);
         sendAttackRequest = true;
      }
      
      
   }

   private void stepPendingAttack(TargetBelief tgtBelief, SensorGroup snsrGrp)
   {
      if(sendAttackRequest)
      {
         requestAttack(tgtBelief);
      }
      
      if(stareStartTime < 0)
      {
         stareStartTime = SimTime.getCurrentSimTimeMS();
      }
      
      long totalStareTime = SimTime.getCurrentSimTimeMS() - stareStartTime;

      long repeatAttackReqTimeFraction = (long)(MILLISECONDS_TO_DROP_PENDING * 0.2);
      long repeatAttackReqestTime = stareStartTime + (numRepeatRequests * repeatAttackReqTimeFraction);
      
      if (tgtBelief.getTaskStatus().getAttackState() == TaskState.Complete ||
            !trueTgtStatSvc.isAlive(tgtBelief.getTrueTargetID()))
      {
         logger.debug("UAV {} notes that pending attack for target {} has been completed.", hostUavId, tgtBelief.getTrueTargetID());
         
         //In case the attacker destroyed the target but was out of comms range
         //tgtBelief.getTaskStatus().setAttackState(TaskState.Complete);
         //tgtBelief.getTaskStatus().setAttackUpdateTimestamp(SimTime.getCurrentSimTimeMS()+1);
         
         reset(tgtBelief, TaskLogicState.BDA, snsrGrp);
      }
      else if (stareStartTime > 0 && totalStareTime >= MILLISECONDS_TO_DROP_PENDING)
      {
         logger.warn("UAV {} has given up waiting for a pending attack on target {}.", hostUavId, tgtBelief.getTrueTargetID());
         tgtBelief.getTaskStatus().setMonitorState(TaskState.Open);
         tgtBelief.getTaskStatus().setMonitorUAV(-1);
         tgtBelief.getTaskStatus().setMonitorUAVScore(-1);
         tgtBelief.getTaskStatus().setMonitorUpdateTimestamp(SimTime.getCurrentSimTimeMS());
         reset(tgtBelief, TaskLogicState.NO_TASK, snsrGrp);
      }
      else if (stareStartTime > 0 && totalStareTime >= repeatAttackReqestTime)
      {
         logger.warn("UAV {} repeating attack request in belief model.", hostUavId);
         sendAttackRequest = true;
      }
   }

   private void requestAttack(TargetBelief tgtBelief)
   {
      tgtBelief.getTaskStatus().setAttackUAV(UAV.NULL_UAV_ID);
      tgtBelief.getTaskStatus().setAttackUAVScore(0);
      tgtBelief.getTaskStatus().setAttackState(TaskState.Open);
      tgtBelief.getTaskStatus().setAttackUpdateTimestamp(SimTime.getCurrentSimTimeMS());
      
      sendAttackRequest = false;
      ++numRepeatRequests;
   }
}
