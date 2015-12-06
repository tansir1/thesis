package thesis.core.entities;

import thesis.core.common.Angle;

/**
 * Performance specification data for a specific type of weapon.
 */
public class WeaponType
{
   private int typeID;
   private double minRange;
   private double maxRange;
   private Angle fov;

   public WeaponType(int typeID)
   {
      this.typeID = typeID;
      minRange = 0;
      maxRange = 0;
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
    * Get the minimum launch range of the weapon.
    *
    * @return The minimum launch range of the weapon in meters.
    */
   public double getMinRange()
   {
      return minRange;
   }

   /**
    * Set the minimum launch range of the weapon.
    *
    * @param minRng
    *           The minimum launch range of the weapon in meters.
    */
   public void setMinRange(double minRng)
   {
      this.minRange = minRng;
   }

   /**
    * Get the maximum launch range of the weapon.
    *
    * @return The max launch range of the weapon in meters.
    */
   public double getMaxRange()
   {
      return maxRange;
   }

   /**
    * Set the maximum launch range of the weapon.
    *
    * @param maxRng
    *           The maximum launch range of the weapon in meters.
    */
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

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fov == null) ? 0 : fov.hashCode());
      long temp;
      temp = Double.doubleToLongBits(maxRange);
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
      WeaponType other = (WeaponType) obj;
      if (fov == null)
      {
         if (other.fov != null)
            return false;
      }
      else if (!fov.equals(other.fov))
         return false;
      if (Double.doubleToLongBits(maxRange) != Double.doubleToLongBits(other.maxRange))
         return false;
      if (Double.doubleToLongBits(minRange) != Double.doubleToLongBits(other.minRange))
         return false;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
