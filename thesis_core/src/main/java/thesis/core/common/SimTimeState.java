package thesis.core.common;

public class SimTimeState
{
   /**
    * The amount of simulated time that has elapsed in milliseconds.
    */
   protected long totalSimTime;

   /**
    * The amount of wall time that has elapsed in milliseconds.
    */
   protected long totalWallTime;

   /**
    * Total number of simulated frames.
    */
   protected long frameCount;

   public SimTimeState()
   {
      totalSimTime = -1;
      totalWallTime = -1;
      frameCount = -1;
   }

   protected void update(long simTime, long wallTime, long frameCnt)
   {
      this.totalSimTime = simTime;
      this.totalWallTime = wallTime;
      this.frameCount = frameCnt;
   }

   public long getSimTime()
   {
      return totalSimTime;
   }

   public long getWallTime()
   {
      return totalWallTime;
   }

   public long getFrameCount()
   {
      return frameCount;
   }
}
