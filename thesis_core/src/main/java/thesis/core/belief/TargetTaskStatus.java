package thesis.core.belief;

import thesis.core.uav.UAV;
import thesis.core.uav.logic.TaskState;

public class TargetTaskStatus
{

   private int monitorUAV;
   private int monitorUAVScore;
   private TaskState monitorState;
   private int interestedMonitorUAV;
   private int interestedMonitorUAVScore;

   private int attackUAV;
   private int attackUAVScore;
   private TaskState attackState;
   private int interestedAttackUAV;
   private int interestedAttackUAVScore;

   private long monitorUpdateTimestamp;
   private long attackUpdateTimestamp;

   private boolean destroyed;

   public TargetTaskStatus()
   {
      reset();
   }

   public void reset()
   {
      destroyed = false;

      monitorUAV = UAV.NULL_UAV_ID;
      monitorUAVScore = -1;
      interestedMonitorUAV = UAV.NULL_UAV_ID;
      interestedMonitorUAVScore = -1;
      monitorState = TaskState.NO_TASK;

      attackUAV = UAV.NULL_UAV_ID;
      attackUAVScore = -1;
      attackState = TaskState.NO_TASK;
      interestedAttackUAV = UAV.NULL_UAV_ID;
      interestedAttackUAVScore = -1;

      monitorUpdateTimestamp = 0;
      attackUpdateTimestamp = 0;
   }

   public void copyFrom(TargetTaskStatus copyMe)
   {
      monitorUAV = copyMe.monitorUAV;
      monitorUAVScore = copyMe.monitorUAVScore;
      monitorState = copyMe.monitorState;
      interestedMonitorUAV = copyMe.interestedMonitorUAV;
      interestedMonitorUAVScore = copyMe.interestedMonitorUAVScore;

      attackUAV = copyMe.attackUAV;
      attackUAVScore = copyMe.attackUAVScore;
      attackState = copyMe.attackState;
      interestedAttackUAV = copyMe.interestedAttackUAV;
      interestedAttackUAVScore = copyMe.interestedAttackUAVScore;

      monitorUpdateTimestamp = copyMe.monitorUpdateTimestamp;
      attackUpdateTimestamp = copyMe.attackUpdateTimestamp;

      destroyed = copyMe.destroyed;
   }

   public boolean isDestroyed()
   {
      return destroyed;
   }

   public void setDestroyed(boolean destroyed)
   {
      this.destroyed = destroyed;
   }

   public int getMonitorUAV()
   {
      return monitorUAV;
   }

   public void setMonitorUAV(int monitorUAV)
   {
      this.monitorUAV = monitorUAV;
   }

   public int getMonitorUAVScore()
   {
      return monitorUAVScore;
   }

   public void setMonitorUAVScore(int monitorUAVScore)
   {
      this.monitorUAVScore = monitorUAVScore;
   }

   public int getInterestedMonitorUAV()
   {
      return interestedMonitorUAV;
   }

   public void setInterestedMonitorUAV(int interestedMonitorUAV)
   {
      this.interestedMonitorUAV = interestedMonitorUAV;
   }

   public int getInterestedMonitorUAVScore()
   {
      return interestedMonitorUAVScore;
   }

   public void setInterestedMonitorUAVScore(int interestedMonitorUAVScore)
   {
      this.interestedMonitorUAVScore = interestedMonitorUAVScore;
   }

   public int getAttackUAV()
   {
      return attackUAV;
   }

   public void setAttackUAV(int attackUAV)
   {
      this.attackUAV = attackUAV;
   }

   public int getAttackUAVScore()
   {
      return attackUAVScore;
   }

   public void setAttackUAVScore(int attackUAVScore)
   {
      this.attackUAVScore = attackUAVScore;
   }

   public int getInterestedAttackUAV()
   {
      return interestedAttackUAV;
   }

   public void setInterestedAttackUAV(int interestedAttackUAV)
   {
      this.interestedAttackUAV = interestedAttackUAV;
   }

   public int getInterestedAttackUAVScore()
   {
      return interestedAttackUAVScore;
   }

   public void setInterestedAttackUAVScore(int interestedAttackUAVScore)
   {
      this.interestedAttackUAVScore = interestedAttackUAVScore;
   }

   public long getMonitorUpdateTimestamp()
   {
      return monitorUpdateTimestamp;
   }

   public long getAttackUpdateTimestamp()
   {
      return attackUpdateTimestamp;
   }

   public void setAttackUpdateTimestamp(long updateTimestamp)
   {
      this.attackUpdateTimestamp = updateTimestamp;
   }

   public void setMonitorUpdateTimestamp(long updateTimestamp)
   {
      this.monitorUpdateTimestamp = updateTimestamp;
   }

   public TaskState getMonitorState()
   {
      return monitorState;
   }

   public void setMonitorState(TaskState monitorState)
   {
      this.monitorState = monitorState;
   }

   public TaskState getAttackState()
   {
      return attackState;
   }

   public void setAttackState(TaskState attackState)
   {
      this.attackState = attackState;
   }

   // public void merge(TargetTaskStatus other)
   // {
   // long curTime = SimTime.getCurrentSimTimeMS();
   //
   // if(other.attackUpdateTimestamp > attackUpdateTimestamp)
   // {
   // destroyed = other.destroyed;
   // //updateTimestamp = curTime;
   // attackUpdateTimestamp = other.attackUpdateTimestamp;
   // }
   //
   // if (other.monitorUAVScore > monitorUAVScore)
   // {
   // monitorUAV = other.monitorUAV;
   // monitorUAVScore = other.monitorUAVScore;
   // monitorState = other.monitorState;
   // monitorUpdateTimestamp = curTime;
   // //updateTimestamp = curTime;
   // }
   //
   // if (other.interestedMonitorUAVScore > interestedMonitorUAVScore)
   // {
   // interestedMonitorUAV = other.interestedMonitorUAV;
   // interestedMonitorUAVScore = other.interestedMonitorUAVScore;
   // updateTimestamp = curTime;
   // }
   //
   // if (other.attackUAVScore > attackUAVScore)
   // {
   // attackUAV = other.attackUAV;
   // attackUAVScore = other.attackUAVScore;
   // attackState = other.attackState;
   // updateTimestamp = curTime;
   // }
   //
   // if (other.interestedAttackUAVScore > interestedAttackUAVScore)
   // {
   // interestedAttackUAV = other.interestedAttackUAV;
   // interestedAttackUAVScore = other.interestedAttackUAVScore;
   // updateTimestamp = curTime;
   // }
   // }

   public void merge(TargetTaskStatus other)
   {
      boolean copyOtherData = false;
      if (other.monitorState == TaskState.Complete && monitorState != other.monitorState)
      {
         // Someone external completed the task
         copyOtherData = true;
      }
      else if (other.monitorUAVScore > monitorUAVScore && other.monitorState != TaskState.Complete
            && monitorState != TaskState.Complete)
      {
         // Everyone is bidding on the task still
         copyOtherData = true;
      }

      if (copyOtherData)
      {
         monitorUAV = other.monitorUAV;
         monitorUAVScore = other.monitorUAVScore;
         monitorState = other.monitorState;
         monitorUpdateTimestamp = other.monitorUpdateTimestamp;
      }
      copyOtherData = false;

      // if (other.interestedMonitorUAVScore > interestedMonitorUAVScore)
      // {
      // interestedMonitorUAV = other.interestedMonitorUAV;
      // interestedMonitorUAVScore = other.interestedMonitorUAVScore;
      // updateTimestamp = curTime;
      // }

      if (other.attackState == TaskState.Complete && attackState != other.attackState)
      {
         // Someone external completed the task
         copyOtherData = true;
      }
      else if (other.attackUAVScore > attackUAVScore && other.attackState != TaskState.Complete
            && attackState != TaskState.Complete)
      {
         // Everyone is bidding on the task still
         copyOtherData = true;
      }

      if (copyOtherData)
      {
         attackUAV = other.attackUAV;
         attackUAVScore = other.attackUAVScore;
         attackState = other.attackState;
         attackUpdateTimestamp = other.attackUpdateTimestamp;
         destroyed = other.destroyed;
      }

      // if (other.interestedAttackUAVScore > interestedAttackUAVScore)
      // {
      // interestedAttackUAV = other.interestedAttackUAV;
      // interestedAttackUAVScore = other.interestedAttackUAVScore;
      // updateTimestamp = curTime;
      // }
   }
}
