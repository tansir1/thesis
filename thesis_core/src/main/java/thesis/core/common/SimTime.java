package thesis.core.common;

public class SimTime
{
   public static int SIM_STEP_RATE_HZ = 60;
   /**
    * The time in milliseconds between simulation frames.
    */
   public static double SIM_STEP_RATE_MS = 1000.0 / SIM_STEP_RATE_HZ;

   /**
    * The time in second between simulation frames.
    */
   public static double SIM_STEP_RATE_S = SIM_STEP_RATE_MS / 1000.0;

   /**
    * The amount of simulated time that has elapsed in milliseconds.
    */
   private static long currentSimTimeMS = 0;

   private static long wallTime = 0;

   private static long frameCnt = 0;

   private static SimTimeState timeState = new SimTimeState();

   public static void resetSimulation()
   {
      currentSimTimeMS = 0;
      wallTime = 0;
      frameCnt = 0;
   }
   
   /**
    * Increment the simulation time.
    */
   public static void stepSimulation()
   {
      currentSimTimeMS += SIM_STEP_RATE_MS;
      frameCnt++;
   }

   public static void incrementWallTime(long elapsed)
   {
      wallTime += elapsed;
   }

   /**
    * @return The length in real wall time that the simulation has been running in milliseconds.
    */
   public static long getWallTime()
   {
      return wallTime;
   }

   public static SimTimeState getTimeState()
   {
      timeState.update(currentSimTimeMS, wallTime, frameCnt);
      return timeState;
   }

   public static long getCurrentSimTimeMS()
   {
      return currentSimTimeMS;
   }
}
