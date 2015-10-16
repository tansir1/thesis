package thesis.core.entities;

import thesis.core.common.LinearSpeed;

/**
 * Performance specification data for a specific type of target.
 */
public class TargetType
{
   private int typeID;
   private LinearSpeed maxSpd;

   public TargetType(int typeID)
   {
      this.typeID = typeID;
      maxSpd = new LinearSpeed();
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
    * @return The maximum speed for this type of target.
    */
   public LinearSpeed getMaxSpeed()
   {
      return maxSpd;
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
      return maxSpd.asMeterPerSecond() > 0;
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
      result = prime * result + ((maxSpd == null) ? 0 : maxSpd.hashCode());
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
      TargetType other = (TargetType) obj;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
