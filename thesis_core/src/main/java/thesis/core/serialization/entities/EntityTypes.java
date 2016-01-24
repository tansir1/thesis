package thesis.core.serialization.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.core.entities.WeaponType;
import thesis.core.entities.uav.UAVType;
import thesis.core.sensors.SensorProbs;
import thesis.core.sensors.SensorType;
import thesis.core.targets.TargetTypeConfigs;

public class EntityTypes
{
   private List<SensorType> sensorTypes;
   private List<WeaponType> weaponTypes;
   private Map<Integer, UAVType> uavTypes;
   private TargetTypeConfigs targetTypes;
   private SensorProbs sensorProbs;

   public EntityTypes()
   {
      sensorTypes = new ArrayList<SensorType>();
      weaponTypes = new ArrayList<WeaponType>();
      uavTypes = new HashMap<Integer, UAVType>();
      targetTypes = new TargetTypeConfigs();
      sensorProbs = new SensorProbs();
   }

   public void copy(EntityTypes copy)
   {
      sensorTypes.addAll(copy.sensorTypes);
      weaponTypes.addAll(copy.weaponTypes);

      for (UAVType uavType : copy.uavTypes.values())
      {
         uavTypes.put(uavType.getTypeID(), uavType);
      }

      targetTypes.copy(copy.targetTypes);
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
    * @return Get the probabilities of sensor to target detection and
    *         identification.
    */
   public SensorProbs getSensorProbabilities()
   {
      return sensorProbs;
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
    * Retrieve a specific uav type.
    *
    * @param typeID
    *           The ID of the type to retrieve.
    * @return The requested uav type or null if no such type exists.
    */
   public UAVType getUAVType(int typeID)
   {
      return uavTypes.get(typeID);
   }

   /**
    * Store a new type of UAV.
    *
    * @param type
    *           This data will be stored.
    */
   public void addUAVType(UAVType type)
   {
      uavTypes.put(type.getTypeID(), type);
   }

   /**
    * Get an unmodifiable view of all the known UAV types.
    *
    * @return
    */
   public Collection<UAVType> getAllUAVTypes()
   {
      return Collections.unmodifiableCollection(uavTypes.values());
   }

   public TargetTypeConfigs getTargetTypes()
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
      for (SensorType st : sensorTypes)
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
      for (WeaponType wt : weaponTypes)
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
