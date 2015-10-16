package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.Distance;

/**
 * Performance specification data for a specific type of weapon.
 */
public class WeaponType
{
   private int typeID;
   private Distance minRange;
   private Distance maxRange;
   private Angle fov;
   private int initialCount;

   public WeaponType(int typeID)
   {
      this.typeID = typeID;
      minRange = new Distance();
      maxRange = new Distance();
      fov = new Angle();
      initialCount = 0;
   }

   /**
    * The unique ID categorizing the weapon type.
    * 
    * @return The category type of the weapon.
    */
   public int getTypeID()
   {
      return typeID;
   }

   /**
    * Get the minimum launch range of the sensor.
    * 
    * @return The minimum launch range of the sensor.
    */
   public Distance getMinRange()
   {
      return minRange;
   }

   /**
    * Get the maximum launch range of the sensor.
    * 
    * @return The max launch range of the sensor.
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
    * Get the number of munitions that a host UAV will initially carry at
    * startup for this weapon type.
    * 
    * @return The starting count of weapon stores available on the host UAV.
    */
   public int getInitialCount()
   {
      return initialCount;
   }

   /**
    * Set the initial number of munition stores available for this weapon type.
    * 
    * @param initialCount
    *           The host UAV will be able to launch this many instances of the
    *           weapon type.
    */
   public void setInitialCount(int initialCount)
   {
      this.initialCount = initialCount;
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
      result = prime * result + initialCount;
      result = prime * result + ((maxRange == null) ? 0 : maxRange.hashCode());
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
      WeaponType other = (WeaponType) obj;
      if (fov == null)
      {
         if (other.fov != null)
            return false;
      }
      else if (!fov.equals(other.fov))
         return false;
      if (initialCount != other.initialCount)
         return false;
      if (maxRange == null)
      {
         if (other.maxRange != null)
            return false;
      }
      else if (!maxRange.equals(other.maxRange))
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
