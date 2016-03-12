package thesis.core.uav.logic;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.WorldBelief;
import thesis.core.uav.UAV;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.WorldBeliefMsg;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class UAVLogicMgr
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final int hostUavId;

   private TaskType curTask;

   private SearchTask searchTask;
   private ConfirmTask confirmTask;

   public UAVLogicMgr(int hostUavId, WorldGIS gis, Random randGen)
   {
      this.hostUavId = hostUavId;
      curTask = TaskType.Search;

      searchTask = new SearchTask(hostUavId, gis, randGen);
      confirmTask = new ConfirmTask(hostUavId, gis, randGen);
   }

   public TaskType getCurrentTaskType()
   {
      return curTask;
   }

   public void stepSimulation(WorldBelief curBelief, List<Message> incomingMsgs, UAV hostUAV)
   {
      for(Message msg : incomingMsgs)
      {
         switch(msg.getType())
         {
         case AuctionAnnounce:
            break;
         case AuctionBid:
            break;
         case AuctionLose:
            break;
         case AuctionWin:
            break;
         case WorldBelief:
            processBeliefStateMsg(curBelief, msg);
            break;
         default:
            //TODO Log error, this shouldn't be possible unless a new enum type is added
            break;
         }
      }

      performTask(curBelief, hostUAV);
   }

   private void processBeliefStateMsg(WorldBelief curBelief, Message rawMsg)
   {
      WorldBeliefMsg msg = (WorldBeliefMsg)rawMsg;
      curBelief.mergeBelief(msg.getBelief());
      logger.trace("Merged belief from {} into {}", rawMsg.getOriginatingUAV(), hostUavId);
   }

   private void performTask(WorldBelief curBelief, UAV hostUAV)
   {
      switch(curTask)
      {
      case Search:
         searchTask.stepSimulation(curBelief, hostUAV.getPathing(), hostUAV.getSensors());
         break;
      case Confirm:
         confirmTask.stepSimulation(curBelief, hostUAV.getPathing(), hostUAV.getSensors());
      }
   }
}
