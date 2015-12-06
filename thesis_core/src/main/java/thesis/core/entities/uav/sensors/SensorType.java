package thesis.core.entities.uav.sensors;

import thesis.core.common.Angle;
import thesis.core.common.AngularSpeed;

/**
 * Performance specification data for a specific type of sensor.
 */
public class SensorType
{
   private int typeID;
   private double minRange;
   private double maxRange;
   private Angle fov;
   private AngularSpeed maxSlewRate;

   public SensorType(int typeID)
   {
      this.typeID = typeID;
      minRange = 0;
      maxRange = 0;
      fov = new Angle();
      maxSlewRate = new AngularSpeed();
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
    * @return The FOV of the sensor.
    */
   public Angle getFov()
   {
      return fov;
   }

   /**
    * Get the maximum rate of slewing for this sensor.
    *
    * @return The maximum slew rate for the sensor.
    */
   public AngularSpeed getMaxSlewRate()
   {
      return maxSlewRate;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fov == null) ? 0 : fov.hashCode());
      long temp;
      temp = Double.doubleToLongBits(maxRange);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((maxSlewRate == null) ? 0 : maxSlewRate.hashCode());
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
      if (fov == null)
      {
         if (other.fov != null)
            return false;
      }
      else if (!fov.equals(other.fov))
         return false;
      if (Double.doubleToLongBits(maxRange) != Double.doubleToLongBits(other.maxRange))
         return false;
      if (maxSlewRate == null)
      {
         if (other.maxSlewRate != null)
            return false;
      }
      else if (!maxSlewRate.equals(other.maxSlewRate))
         return false;
      if (Double.doubleToLongBits(minRange) != Double.doubleToLongBits(other.minRange))
         return false;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
