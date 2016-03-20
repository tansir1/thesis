package thesis.core.uav.auction;

import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.TaskType;

public class AuctionAnnounceMessage extends Message
{
   private TaskType taskType;
   private int trueTgtID;

   public AuctionAnnounceMessage(TaskType taskType, int trueTgtID)
   {
      super(MsgType.AuctionAnnounce);
      this.taskType = taskType;
      this.trueTgtID = trueTgtID;
   }

   @Override
   protected Message cloneMsgSpecificData()
   {
      return new AuctionAnnounceMessage(taskType, trueTgtID);
   }

}
