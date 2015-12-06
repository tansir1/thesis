package thesis.core.entities.uav;

import java.util.HashSet;
import java.util.Set;

import thesis.core.common.SimTime;
import thesis.core.entities.Weapon;
import thesis.core.entities.uav.sensors.SensorType;

/**
 * Performance specification data for a specific type of UAV.
 */
public class UAVType
{
   private int typeID;
   /**
    * Max speed of the UAV in meters/second.
    */
   private double maxSpd;
   private double minTurnRadius;

   private Set<SensorType> sensors;
   private Set<Weapon> weapons;

   // Derived parameters
   /**
    * Max turn rate of the UAV in degrees/second.
    */
   private double maxTurnRt;

   /**
    * Speed of the UAV in meters/frame.
    */
   private double frameSpd;

   public UAVType(int typeID)
   {
      this.typeID = typeID;
      maxSpd = 0;
      maxTurnRt = 0;

      sensors = new HashSet<SensorType>();
      weapons = new HashSet<Weapon>();

      minTurnRadius = 0;
      frameSpd = 0;
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
   public double getMaxSpd()
   {
      return maxSpd;
   }

   /**
    * Set the maximum ground speed of the aircraft.
    *
    * @param spd The maximum speed of the UAV in meters/second.
    */
   public void setMaxSpd(double spd)
   {
      maxSpd = spd;
   }

   /**
    * Get the speed of the UAV per frame of the simulation.
    *
    * @return The speed of the UAV scaled to simulation frame rate in meters/frame.
    */
   public double getFrameSpd()
   {
      return frameSpd;
   }

   /**
    * Set the speed of the UAV per frame of the simulation.
    *
    * @param frameSpd The speed of the UAV scaled to simulation frame rate in meters/frame.
    */
   public void setFrameSpd(double frameSpd)
   {
      this.frameSpd = frameSpd;
   }

   /**
    * Get the minimum radius required for the UAV to turn 180 degrees at max
    * speed.
    *
    * @return The distance required to turn around at max speed in meters.
    */
   public double getMinTurnRadius()
   {
      return minTurnRadius;
   }

   /**
    * Set the minimum radius required for the UAV to turn 180 degrees at max
    * speed.
    *
    * @param radius The distance required to turn around at max speed in meters.
    */
   public void setMinTurnRadius(double radius)
   {
      this.minTurnRadius = radius;
   }

   /**
    * Get the maximum turning rate for the UAV.
    *
    * @return The maximum turning rate.
    */
   public double getMaxTurnRt()
   {
      return maxTurnRt;
   }

   /**
    * Get the set of sensor types onboard the UAV.
    *
    * @return The sensors on the aircraft.
    */
   public Set<SensorType> getSensors()
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
      maxTurnRt = Math.toDegrees(maxSpd / minTurnRadius);

      frameSpd = maxSpd * SimTime.SIM_STEP_RATE_S;
   }
}
