package thesis.core.sensors;

import thesis.core.common.SimTime;

/**
 * Performance specification data for all sensor types.
 */
public class SensorTypeConfigs
{

   /**
    * Field of view in degrees of the sensor.
    */
   private float fov[];
   /**
    * Minimum sensing distance in meters.
    */
   private double minRng[];
   /**
    * Maximum sensing distance in meters.
    */
   private double maxRng[];
   /**
    * Speed that the sensor slews in degrees/second.
    */
   private float slewRate[];

   public SensorTypeConfigs()
   {

   }

   public void copy(SensorTypeConfigs copy)
   {
      int numTgts = copy.fov.length;
      reset(copy.fov.length);

      System.arraycopy(copy.fov, 0, fov, 0, numTgts);
      System.arraycopy(copy.minRng, 0, minRng, 0, numTgts);
      System.arraycopy(copy.maxRng, 0, maxRng, 0, numTgts);
      System.arraycopy(copy.slewRate, 0, slewRate, 0, numTgts);
   }

   public void reset(int numTypes)
   {
      fov = new float[numTypes];
      minRng = new double[numTypes];
      maxRng = new double[numTypes];
      slewRate = new float[numTypes];

      for (int i = 0; i < numTypes; ++i)
      {
         fov[0] = -1f;
         minRng[0] = -1f;
         maxRng[0] = -1f;
         slewRate[0] = 0f;
      }
   }

   public void setSensorData(int snsrType, float fov, double minRng, double maxRng, float slewRt)
   {
      this.fov[snsrType] = fov;
      this.minRng[snsrType] = minRng;
      this.maxRng[snsrType] = maxRng;
      this.slewRate[snsrType] = slewRt;
   }

   /**
    * Get the field of view angle.
    *
    * @param snsrType
    *           The type of the sensor to lookup.
    * @return The FOV of the sensor in degrees.
    */
   public float getFOV(int snsrType)
   {
      return fov[snsrType];
   }

   /**
    * Get the maximum rate of slewing for this sensor.
    *
    * @param snsrType
    *           The type of the sensor to lookup.
    * @return The maximum slew rate for the sensor in degrees/second.
    */
   public float getSlewRate(int snsrType)
   {
      return slewRate[snsrType];
   }

   public int getNumTypes()
   {
      return fov.length;
   }

   /**
    * Get the minimum sensing range of the sensor.
    *
    * @param snsrType
    *           The type of the sensor to lookup.
    * @return The minimum range of the sensor in meters.
    */
   public double getMinRange(int snsrType)
   {
      return minRng[snsrType];
   }

   /**
    * Get the maximum sensing range of the sensor.
    *
    * @param snsrType
    *           The type of the sensor to lookup.
    * @return The max range of the sensor in meters.
    */
   public double getMaxRange(int snsrType)
   {
      return maxRng[snsrType];
   }

   /**
    * Get the maximum rate of slewing for this sensor.
    *
    * @param snsrType
    *           The type of the sensor to lookup.
    * @return The maximum slew rate for the sensor in degrees/frame.
    */
   public float getMaxSlewFrameRate(int snsrType)
   {
      return slewRate[snsrType] * (float)SimTime.SIM_STEP_RATE_S;
   }

   public boolean typeExists(int snsrType)
   {
      return snsrType >= 0 && snsrType < fov.length;
   }
}
