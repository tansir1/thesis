package thesis.core.uav.auction;

import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.TaskType;

public class BidMessage extends Message
{
   private TaskType taskType;
   private int trueTgtID;
   private double bid;

   public BidMessage(TaskType taskType, int trueTgtID, double bid)
   {
      super(MsgType.AuctionBid);
      this.taskType = taskType;
      this.trueTgtID = trueTgtID;
      this.bid = bid;
   }

   @Override
   protected Message cloneMsgSpecificData()
   {
      return new BidMessage(taskType, trueTgtID, bid);
   }

   public TaskType getTaskType()
   {
      return taskType;
   }

   public int getTrueTgtID()
   {
      return trueTgtID;
   }

   public double getBid()
   {
      return bid;
   }

}
