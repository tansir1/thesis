package thesis.core.common;

public class SimTime
{
   /**
    * The time in milliseconds between simulation frames.
    */
   public static long SIM_STEP_RATE_MS = 16;//60Hz update rate

   /**
    * The amount of simulated time that has elapsed in milliseconds.
    */
   public static long CURRENT_SIM_TIME_MS = 0;

   private static long startTime = 0;

   /**
    * Increment the simulation time.
    */
   public static void stepSimulation()
   {
      if(startTime == 0)
      {
         startTime = System.currentTimeMillis();
      }

      CURRENT_SIM_TIME_MS += SIM_STEP_RATE_MS;
   }

   /**
    * @return The length in real wall time that the simulation has been running in milliseconds.
    */
   public static long getWallTime()
   {
      return System.currentTimeMillis() - startTime;
   }
}
