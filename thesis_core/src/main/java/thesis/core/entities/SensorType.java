package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.AngularSpeed;
import thesis.core.common.Distance;

/**
 * Performance specification data for a specific type of sensor.
 */
public class SensorType
{
   private int typeID;
   private Distance minRange;
   private Distance maxRange;
   private Angle fov;
   private AngularSpeed maxSlewRate;

   public SensorType(int typeID)
   {
      this.typeID = typeID;
      minRange = new Distance();
      maxRange = new Distance();
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
    * @return The minimum range of the sensor.
    */
   public Distance getMinRange()
   {
      return minRange;
   }

   /**
    * Get the maximum sensing range of the sensor.
    * 
    * @return The max range of the sensor.
    */
   public Distance getMaxRange()
   {
      return maxRange;
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

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fov == null) ? 0 : fov.hashCode());
      result = prime * result + ((maxRange == null) ? 0 : maxRange.hashCode());
      result = prime * result + ((maxSlewRate == null) ? 0 : maxSlewRate.hashCode());
      result = prime * result + ((minRange == null) ? 0 : minRange.hashCode());
      result = prime * result + typeID;
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
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
      if (maxRange == null)
      {
         if (other.maxRange != null)
            return false;
      }
      else if (!maxRange.equals(other.maxRange))
         return false;
      if (maxSlewRate == null)
      {
         if (other.maxSlewRate != null)
            return false;
      }
      else if (!maxSlewRate.equals(other.maxSlewRate))
         return false;
      if (minRange == null)
      {
         if (other.minRange != null)
            return false;
      }
      else if (!minRange.equals(other.minRange))
         return false;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
