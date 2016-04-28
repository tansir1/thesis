package thesis.core.uav.auction;

import thesis.core.belief.TargetBelief;
import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.TaskType;

@Deprecated
public class AuctionAnnounceMessage extends Message
{
   private TaskType taskType;
   private TargetBelief tgtBelief;

   public AuctionAnnounceMessage(TaskType taskType, TargetBelief tgtBelief)
   {
      super(MsgType.AuctionAnnounce);
      this.taskType = taskType;
      this.tgtBelief = new TargetBelief(tgtBelief);
   }

   @Override
   protected Message cloneMsgSpecificData()
   {
      return new AuctionAnnounceMessage(taskType, tgtBelief);
   }

   public TaskType getTaskType()
   {
      return taskType;
   }

   public int getTargetID()
   {
      return tgtBelief.getTrueTargetID();
   }

   public TargetBelief getBelief()
   {
      return tgtBelief;
   }
}
