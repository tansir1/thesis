package thesis.core.entities;

import java.util.HashSet;
import java.util.Set;

import thesis.core.common.AngularSpeed;
import thesis.core.common.LinearSpeed;

/**
 * Performance specification data for a specific type of UAV.
 */
public class UAVType
{
   private int typeID;
   private LinearSpeed maxSpd;
   private AngularSpeed maxTurnRt;
   private Set<Sensor> sensors;
   private Set<Weapon> weapons;

   public UAVType(int typeID)
   {
      this.typeID = typeID;
      maxSpd = new LinearSpeed();
      maxTurnRt = new AngularSpeed();

      sensors = new HashSet<Sensor>();
      weapons = new HashSet<Weapon>();
   }

   /**
    * The unique ID categorizing the UAV type.
    * 
    * @return The category type of the UAV.
    */
   public int getTypeID()
   {
      return typeID;
   }

   /**
    * Get the maximum ground speed of the aircraft.
    * 
    * @return The maximum speed of the UAV.
    */
   public LinearSpeed getMaxSpd()
   {
      return maxSpd;
   }

   /**
    * Get the maximum turning rate for the UAV.
    * 
    * @return The maximum turning rate.
    */
   public AngularSpeed getMaxTurnRt()
   {
      return maxTurnRt;
   }

   /**
    * Get the set of sensors onboard the UAV.
    * 
    * @return The sensors on the aircraft.
    */
   public Set<Sensor> getSensors()
   {
      return sensors;
   }

   /**
    * Get the set of weapons onboard the UAV.
    * 
    * @return The weapons on the aircraft.
    */
   public Set<Weapon> getWeapons()
   {
      return weapons;
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
      UAVType other = (UAVType) obj;
      if (typeID != other.typeID)
         return false;
      return true;
   }

}
