package thesis.core.entities;

/**
 * Performance specification data for a specific type of target.
 */
public class TargetType
{
   private int typeID;
   /**
    * Meters/second
    */
   private double maxSpd;

   public TargetType(int typeID)
   {
      this.typeID = typeID;
      maxSpd = 0;
   }

   /**
    * The unique ID categorizing the target type.
    *
    * @return The category type of the target.
    */
   public int getTypeID()
   {
      return typeID;
   }

   /**
    * Get the maximum speed for this type of target. If zero or negative then
    * this is treated as a static non-moving target.
    *
    * @return The maximum speed for this type of target in meters/second.
    */
   public double getMaxSpeed()
   {
      return maxSpd;
   }

   /**
    * Set the maximum speed for this type of target. If zero or negative then
    * this is treated as a static non-moving target.
    *
    * @param spd
    *           The maximum speed for this type of target in meters/second.
    */
   public void setMaxSpeed(double spd)
   {
      this.maxSpd = spd;
   }

   /**
    * Check if this type of target is mobile or statically fixed. The check is
    * performed by verifying that the max speed of the target is greater than
    * zero.
    *
    * @return True if the target is capable of movement, false otherwise.
    * @see TargetType#getMaxSpeed()
    */
   public boolean isMobile()
   {
      return maxSpd > 0;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(maxSpd);
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
      TargetType other = (TargetType) obj;
      if (Double.doubleToLongBits(maxSpd) != Double.doubleToLongBits(other.maxSpd))
         return false;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
