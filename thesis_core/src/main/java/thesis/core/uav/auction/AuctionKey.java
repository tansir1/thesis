package thesis.core.uav.auction;

import thesis.core.uav.logic.TaskType;

@Deprecated
public class AuctionKey
{
   private TaskType task;
   private int targetID;

   public AuctionKey(TaskType task, int targetID)
   {
      this.task = task;
      this.targetID = targetID;
   }

   public TaskType getTaskType()
   {
      return task;
   }

   public int getTargetID()
   {
      return targetID;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + targetID;
      result = prime * result + ((task == null) ? 0 : task.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AuctionKey other = (AuctionKey) obj;
      if (targetID != other.targetID)
         return false;
      if (task != other.task)
         return false;
      return true;
   }

}
