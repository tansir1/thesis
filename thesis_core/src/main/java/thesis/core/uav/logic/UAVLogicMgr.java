package thesis.core.uav.logic;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.belief.WorldBeliefMsg;
import thesis.core.targets.ITrueTargetStatusProvider;
import thesis.core.uav.UAV;
import thesis.core.uav.comms.IMsgTransmitter;
import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.MonitorTask.TaskLogicState;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class UAVLogicMgr
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final int hostUavId;

   private TaskType curTask;

   private SearchTask searchTask;
   private MonitorTask monitorTask;
   private AttackTask attackTask;

   private int numTgtTypes;

   private TargetBelief curTgt;

   private TaskAllocator taskAllocator;

   public UAVLogicMgr(int hostUavId, WorldGIS gis, Random randGen, int numTgtTypes, ITrueTargetStatusProvider trueTgtStatusSvc)
   {
      this.hostUavId = hostUavId;
      this.numTgtTypes = numTgtTypes;
      curTask = null;

      searchTask = new SearchTask(hostUavId, gis, randGen);
      monitorTask = new MonitorTask(hostUavId, trueTgtStatusSvc);
      attackTask = new AttackTask(hostUavId);

      taskAllocator = new TaskAllocator(hostUavId);
   }

   public TaskType getCurrentTaskType()
   {
      return curTask;
   }

   public TargetBelief getCurrentTarget()
   {
      return curTgt;
   }

   public void stepSimulation(WorldBelief curBelief, List<Message> incomingMsgs, UAV hostUAV,
         IMsgTransmitter msgTransmitter)
   {
      for (Message msg : incomingMsgs)
      {
         switch (msg.getType())
         {
         case WorldBelief:
            processBeliefStateMsg(curBelief, msg);
            break;
         default:
            // TODO Log error, this shouldn't be possible unless a new enum type
            // is added
            break;
         }
      }

      taskAllocator.stepSimulation(curBelief, hostUAV);

      boolean reset = false;
      if (hasTargetChanged())
      {
         reset = true;
      }

      if (hasTaskChanged())
      {
         reset = true;
      }

      if (reset)
      {
         switch (curTask)
         {
         case Attack:
            attackTask.Reset(curTgt.getPose(), hostUAV.getPathing(), hostUAV.getSensors(), curTgt.getTrueTargetID());
            break;
         case Monitor:
            // TODO Should we start at confirm or something else?
            monitorTask.reset(curTgt, TaskLogicState.Confirm, hostUAV.getSensors());
            break;
         case Search:
            searchTask.reset(curBelief, hostUAV.getPathing(), hostUAV.getSensors());
            break;
         }
      }

      performTask(curBelief, hostUAV);
   }

   /**
    * Check if the task allocator has assigned a new target for this UAV.
    *
    * @return True if a new target has been allocated.
    */
   private boolean hasTargetChanged()
   {
      boolean changed = false;

      if (taskAllocator.getTarget() == null && curTgt != null)
      {
         changed = true;
         curTgt = null;
      }
      else if (taskAllocator.getTarget() != null && curTgt == null)
      {
         changed = true;
         curTgt = taskAllocator.getTarget();
      }
      else if (taskAllocator.getTarget() == null && curTgt == null)
      {
         // Do nothing, we're still searching
      }
      else if (taskAllocator.getTarget().getTrueTargetID() != curTgt.getTrueTargetID())
      {
         changed = true;
         curTgt = taskAllocator.getTarget();
      }

      return changed;
   }

   /**
    * Check if the allocated task type has changed.
    *
    * @return True if task type has changed.
    */
   private boolean hasTaskChanged()
   {
      boolean changed = false;

      if (curTask == null || curTask != taskAllocator.getTaskType())
      {
         curTask = taskAllocator.getTaskType();
         changed = true;
      }

      return changed;
   }

   private void processBeliefStateMsg(WorldBelief curBelief, Message rawMsg)
   {
      WorldBeliefMsg msg = (WorldBeliefMsg) rawMsg;
      curBelief.mergeBelief(msg.getBelief());
      logger.trace("Merged belief from {} into {}", rawMsg.getOriginatingUAV(), hostUavId);
   }

   private void performTask(WorldBelief curBelief, UAV hostUAV)
   {
      switch (curTask)
      {
      case Search:
         searchTask.stepSimulation(curBelief, hostUAV.getPathing(), hostUAV.getSensors());
         break;
      case Monitor:
         monitorTask.stepSimulation(curTgt, hostUAV.getPathing(), hostUAV.getSensors());
         break;
      case Attack:
         attackTask.stepSimulation(curTgt, hostUAV.getPathing(), hostUAV.getWeapons(), hostUAV.getSensors());
         break;
      }
   }

}
