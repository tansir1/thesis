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

   public WeaponType(int typeID)
   {
      this.typeID = typeID;
      minRange = new Distance();
      maxRange = new Distance();
      fov = new Angle();
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
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
