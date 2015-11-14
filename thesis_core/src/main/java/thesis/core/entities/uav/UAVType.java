package thesis.core.entities.uav;

import java.util.HashSet;
import java.util.Set;

import thesis.core.SimModel;
import thesis.core.common.AngularSpeed;
import thesis.core.common.Distance;
import thesis.core.common.LinearSpeed;
import thesis.core.entities.Sensor;
import thesis.core.entities.Weapon;

/**
 * Performance specification data for a specific type of UAV.
 */
public class UAVType
{
   private int typeID;
   private LinearSpeed maxSpd;
   private Distance minTurnRadius;

   private Set<Sensor> sensors;
   private Set<Weapon> weapons;

   // Derived parameters
   private AngularSpeed maxTurnRt;
   private LinearSpeed frameSpd;

   public UAVType(int typeID)
   {
      this.typeID = typeID;
      maxSpd = new LinearSpeed();
      maxTurnRt = new AngularSpeed();

      sensors = new HashSet<Sensor>();
      weapons = new HashSet<Weapon>();

      minTurnRadius = new Distance();
      frameSpd = new LinearSpeed();
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
    * Get the speed of the UAV per frame of the simulation.
    * 
    * @return The speed of the UAV scaled to simulation frame rate.
    */
   public LinearSpeed getFrameSpd()
   {
      return frameSpd;
   }

   /**
    * Get the minimum radius required for the UAV to turn 180 degrees at max
    * speed.
    * 
    * @return The distance required to turn around at max speed.
    */
   public Distance getMinTurnRadius()
   {
      return minTurnRadius;
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

   public void init()
   {
      // final double timeToTurnAround = Math.PI /
      // maxTurnRt.asRadiansPerSecond();
      // final double arcLength = timeToTurnAround * maxSpd.asMeterPerSecond();
      // minTurnRadius.setAsMeters(arcLength / Math.PI);
      // minTurnRadius.setAsMeters(maxSpd.asMeterPerSecond() /
      // maxTurnRt.asRadiansPerSecond());
      maxTurnRt.setAsRadiansPerSecond(maxSpd.asMeterPerSecond() / minTurnRadius.asMeters());

      frameSpd.setAsMetersPerSecond(maxSpd.asMeterPerSecond() * (SimModel.SIM_STEP_RATE_MS / 1000.0));
   }
}