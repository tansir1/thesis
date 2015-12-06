package thesis.core.entities.uav.sensors;

import thesis.core.common.SimTime;

/**
 * Performance specification data for a specific type of sensor.
 */
public class SensorType
{
   private int typeID;
   private double minRange;
   private double maxRange;
   private double fov;
   /**
    * Max speed that the sensor can slew in degrees/second.
    */
   private double maxSlewRate;

   /**
    * Max speed that the sensor can slew in degrees/frame.
    */
   private double maxSlewRateFrame;

   public SensorType(int typeID)
   {
      this.typeID = typeID;
      minRange = 0;
      maxRange = 0;
      fov = 0;
      maxSlewRate = 0;
   }

   /**
    * The unique ID categorizing the sensor type.
    *
    * @return The category type of the sensor.
    */
   public int getTypeID()
   {
      return typeID;
   }

   /**
    * Get the minimum sensing range of the sensor.
    *
    * @return The minimum range of the sensor in meters.
    */
   public double getMinRange()
   {
      return minRange;
   }

   public void setMinRange(double minRng)
   {
      this.minRange = minRng;
   }

   /**
    * Get the maximum sensing range of the sensor.
    *
    * @return The max range of the sensor in meters.
    */
   public double getMaxRange()
   {
      return maxRange;
   }

   public void setMaxRange(double maxRng)
   {
      this.maxRange = maxRng;
   }

   /**
    * Get the field of view angle.
    *
    * @return The FOV of the sensor in degrees.
    */
   public double getFov()
   {
      return fov;
   }

   /**
    * Set the field of view angle.
    *
    * @param fov
    *           The FOV of the sensor in degrees.
    */
   public void setFov(double fov)
   {
      this.fov = fov;
   }

   /**
    * Get the maximum rate of slewing for this sensor.
    *
    * @return The maximum slew rate for the sensor in degrees/second.
    */
   public double getMaxSlewRate()
   {
      return maxSlewRate;
   }

   /**
    * Set the maximum rate of slewing for this sensor.
    *
    * @return slewRate The maximum slew rate for the sensor in degrees/second.
    */
   public void setMaxSlewRate(double slewRate)
   {
      maxSlewRate = slewRate;
      maxSlewRateFrame = slewRate * SimTime.SIM_STEP_RATE_S;
   }

   /**
    * Get the maximum rate of slewing for this sensor.
    *
    * @return The maximum slew rate for the sensor in degrees/frame.
    */
   public double getMaxSlewFrameRate()
   {
      return maxSlewRateFrame;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(fov);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(maxRange);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(maxSlewRate);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(maxSlewRateFrame);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(minRange);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + typeID;
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SensorType other = (SensorType) obj;
      if (Double.doubleToLongBits(fov) != Double.doubleToLongBits(other.fov))
         return false;
      if (Double.doubleToLongBits(maxRange) != Double.doubleToLongBits(other.maxRange))
         return false;
      if (Double.doubleToLongBits(maxSlewRate) != Double.doubleToLongBits(other.maxSlewRate))
         return false;
      if (Double.doubleToLongBits(maxSlewRateFrame) != Double.doubleToLongBits(other.maxSlewRateFrame))
         return false;
      if (Double.doubleToLongBits(minRange) != Double.doubleToLongBits(other.minRange))
         return false;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
