package thesis.core.common;

public class SimTime
{
   /**
    * The time in milliseconds between simulation frames.
    */
   public static long SIM_STEP_RATE_MS = 16;//60Hz update rate

   /**
    * The time in second between simulation frames.
    */
   public static double SIM_STEP_RATE_S = (SIM_STEP_RATE_MS*1.0) / 1000.0;

   /**
    * The amount of simulated time that has elapsed in milliseconds.
    */
   public static long CURRENT_SIM_TIME_MS = 0;

   private static long wallTime = 0;

   /**
    * Increment the simulation time.
    */
   public static void stepSimulation()
   {
      CURRENT_SIM_TIME_MS += SIM_STEP_RATE_MS;
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
}
