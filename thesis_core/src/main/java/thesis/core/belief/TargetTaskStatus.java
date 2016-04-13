package thesis.core.belief;

import thesis.core.common.SimTime;
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

   private long updateTimestamp;

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

      updateTimestamp = 0;
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

      updateTimestamp = copyMe.updateTimestamp;

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

   public long getUpdateTimestamp()
   {
      return updateTimestamp;
   }

   public void setUpdateTimestamp(long updateTimestamp)
   {
      this.updateTimestamp = updateTimestamp;
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

   public void merge(TargetTaskStatus other)
   {
      long curTime = SimTime.getCurrentSimTimeMS();

      if(other.updateTimestamp > updateTimestamp)
      {
         destroyed = other.destroyed;
         updateTimestamp = curTime;
      }

      if (other.monitorUAVScore > monitorUAVScore)
      {
         monitorUAV = other.monitorUAV;
         monitorUAVScore = other.monitorUAVScore;
         monitorState = other.monitorState;
         updateTimestamp = curTime;
      }

      if (other.interestedMonitorUAVScore > interestedMonitorUAVScore)
      {
         interestedMonitorUAV = other.interestedMonitorUAV;
         interestedMonitorUAVScore = other.interestedMonitorUAVScore;
         updateTimestamp = curTime;
      }

      if (other.attackUAVScore > attackUAVScore)
      {
         attackUAV = other.attackUAV;
         attackUAVScore = other.attackUAVScore;
         attackState = other.attackState;
         updateTimestamp = curTime;
      }

      if (other.interestedAttackUAVScore > interestedAttackUAVScore)
      {
         interestedAttackUAV = other.interestedAttackUAV;
         interestedAttackUAVScore = other.interestedAttackUAVScore;
         updateTimestamp = curTime;
      }
   }
}
