package thesis.core.uav.logic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.WorldBelief;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.WorldBeliefMsg;
import thesis.core.utilities.LoggerIDs;

public class UAVLogicMgr
{
   private static Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   private final int hostUavId;

   public UAVLogicMgr(int hostUavId)
   {
      this.hostUavId = hostUavId;
   }

   public void stepSimulation(WorldBelief curBelief, List<Message> incomingMsgs)
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
   }

   private void processBeliefStateMsg(WorldBelief curBelief, Message rawMsg)
   {
      WorldBeliefMsg msg = (WorldBeliefMsg)rawMsg;
      curBelief.mergeBelief(msg.getBelief());
      logger.trace("Merged belief from {} into {}", rawMsg.getOriginatingUAV(), hostUavId);
   }
}
