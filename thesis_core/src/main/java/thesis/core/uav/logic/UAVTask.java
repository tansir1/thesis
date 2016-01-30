package thesis.core.uav.logic;

public abstract class UAVTask
{
   private TaskType taskType;

   public UAVTask(TaskType taskType)
   {
      this.taskType = taskType;
   }

   public TaskType getTaskType()
   {
      return taskType;
   }

   public abstract void stepSimulation();
}
