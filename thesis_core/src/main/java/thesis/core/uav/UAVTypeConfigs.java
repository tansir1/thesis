package thesis.core.uav;

import thesis.core.common.SimTime;

/**
 * Performance specification data for all UAV types.
 */
public class UAVTypeConfigs
{

   /**
    * Cruise speed of the UAVs in meters per second.
    */
   private double spds[];

   /**
    * The minimum radius required for the UAV to turn 180 degrees (meters).
    */
   private double turnRadius[];

   public UAVTypeConfigs()
   {

   }

   public void reset(int numTypes)
   {
      spds = new double[numTypes];
      turnRadius = new double[numTypes];

      for (int i = 0; i < numTypes; ++i)
      {
         spds[i] = -1f;
         turnRadius[i] = -1f;
      }
   }

   public int getNumTypes()
   {
      return spds.length;
   }

   /**
    * @param type
    *           Type ID.
    * @param spd
    *           Cruise speed in m/s.
    * @param turnRadius
    *           Radius in meters to turn 180 degrees.
    */
   public void setUAVData(int type, double spd, double turnRadius)
   {
      this.spds[type] = spd;
      this.turnRadius[type] = turnRadius;
   }

   /**
    * Get the maximum ground speed of the aircraft.
    *
    * @param type
    *           The type of the UAV to lookup.
    *
    * @return The maximum speed of the UAV.
    */
   public double getSpeed(int type)
   {
      return spds[type];
   }

   /**
    * Get the minimum radius required for the UAV to turn 180 degrees at max
    * speed.
    *
    * @param type
    *           The type of the UAV to lookup.
    * @return The distance required to turn around at max speed in meters.
    */
   public double getTurnRadius(int type)
   {
      return turnRadius[type];
   }

   /**
    * Get the speed of the UAV per frame of the simulation.
    *
    * @param type
    *           The type of the UAV to lookup.
    * @return The speed of the UAV scaled to simulation frame rate in
    *         meters/frame.
    */
   public double getFrameSpd(int type)
   {
      return spds[type] * SimTime.SIM_STEP_RATE_S;
   }

   /**
    * Get the maximum turning rate for the UAV.
    *
    * @param type
    *           The UAV type.
    * @return The maximum turning rate (degrees / sec).
    */
   public double getMaxTurnRt(int type)
   {
      double spd = spds[type];
      double radius = turnRadius[type];
      return Math.toDegrees(spd / radius);
   }
}
