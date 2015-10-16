package thesis.core.serialization.entities;

import java.util.ArrayList;
import java.util.List;

import thesis.core.entities.SensorType;
import thesis.core.entities.WeaponType;

public class EntityTypes
{
   private List<SensorType> sensorTypes;
   private List<WeaponType> weaponTypes;

   public EntityTypes()
   {
      sensorTypes = new ArrayList<SensorType>();
      weaponTypes = new ArrayList<WeaponType>();
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
}
