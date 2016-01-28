package thesis.core.uav;

/**
 * Performance specification data for all UAV types.
 */
public class UAVTypeConfigs
{

   /**
    * Cruise speed of the UAVs in meters per second.
    */
   private float spds[];

   /**
    * The minimum radius required for the UAV to turn 180 degrees (meters).
    */
   private float turnRadius[];

   public UAVTypeConfigs()
   {

   }

   public void reset(int numTypes)
   {
      spds = new float[numTypes];
      turnRadius = new float[numTypes];

      for (int i = 0; i < numTypes; ++i)
      {
         spds[i] = -1f;
         turnRadius[i] = -1f;
      }
   }

   /**
    * @param type
    *           Type ID.
    * @param spd
    *           Cruise speed in m/s.
    * @param turnRadius
    *           Radius in meters to turn 180 degrees.
    */
   public void setUAVData(int type, float spd, float turnRadius)
   {
      this.spds[type] = spd;
      this.turnRadius[type] = turnRadius;
   }

   public float getSpeed(int type)
   {
      return spds[type];
   }

   public float getTurnRadius(int type)
   {
      return turnRadius[type];
   }

   /**
    * Get the maximum turning rate for the UAV.
    *
    * @param type
    *           The UAV type.
    * @return The maximum turning rate (degrees / sec).
    */
   public float getMaxTurnRt(int type)
   {
      float spd = spds[type];
      float radius = turnRadius[type];
      return (float) Math.toDegrees(spd / radius);
   }
}
