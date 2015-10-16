package thesis.core.serialization.entities;

import java.util.ArrayList;
import java.util.List;

import thesis.core.entities.SensorType;
import thesis.core.entities.TargetType;
import thesis.core.entities.UAVType;
import thesis.core.entities.WeaponType;

public class EntityTypes
{
   private List<SensorType> sensorTypes;
   private List<WeaponType> weaponTypes;
   private List<UAVType> uavTypes;
   private List<TargetType> targetTypes;

   public EntityTypes()
   {
      sensorTypes = new ArrayList<SensorType>();
      weaponTypes = new ArrayList<WeaponType>();
      uavTypes = new ArrayList<UAVType>();
      targetTypes = new ArrayList<TargetType>();
   }

   /**
    * Get a modifiable list of all known sensor types.
    * 
    * @return The list of known sensor types.
    */
   public List<SensorType> getSensorTypes()
   {
      return sensorTypes;
   }

   /**
    * Get a modifiable list of all known weapon types.
    * 
    * @return The list of known weapon types.
    */
   public List<WeaponType> getWeaponTypes()
   {
      return weaponTypes;
   }

   /**
    * Get a modifiable list of all known UAV types.
    * 
    * @return The list of known UAV types.
    */
   public List<UAVType> getUAVTypes()
   {
      return uavTypes;
   }

   /**
    * Get a modifiable list of all known target types.
    * 
    * @return The list of known target types.
    */
   public List<TargetType> getTargetTypes()
   {
      return targetTypes;
   }
   
   /**
    * Retrieve the {@link SensorType} corresponding to the given type ID.
    * 
    * @param typeID
    *           Find the {@link SensorType} with this type ID.
    * @return The requested {@link SensorType} or null if no such type exists.
    */
   public SensorType getSensorType(int typeID)
   {
      SensorType find = null;
      for(SensorType st : sensorTypes)
      {
         if (st.getTypeID() == typeID)
         {
            find = st;
            break;
         }
      }
      return find;
   }
   
   /**
    * Retrieve the {@link WeaponType} corresponding to the given type ID.
    * 
    * @param typeID
    *           Find the {@link WeaponType} with this type ID.
    * @return The requested {@link WeaponType} or null if no such type exists.
    */
   public WeaponType getWeaponType(int typeID)
   {
      WeaponType find = null;
      for(WeaponType wt : weaponTypes)
      {
         if (wt.getTypeID() == typeID)
         {
            find = wt;
            break;
         }
      }
      return find;
   }
}
