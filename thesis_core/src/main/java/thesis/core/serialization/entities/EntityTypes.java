package thesis.core.serialization.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.core.entities.SensorType;
import thesis.core.entities.TargetType;
import thesis.core.entities.WeaponType;
import thesis.core.entities.uav.UAVType;

public class EntityTypes
{
	private List<SensorType> sensorTypes;
	private List<WeaponType> weaponTypes;
	private Map<Integer, UAVType> uavTypes;
	private Map<Integer, TargetType> targetTypes;

	public EntityTypes()
	{
		sensorTypes = new ArrayList<SensorType>();
		weaponTypes = new ArrayList<WeaponType>();
		uavTypes = new HashMap<Integer, UAVType>();
		targetTypes = new HashMap<Integer, TargetType>();
	}

	public void copy(EntityTypes copy)
	{
		sensorTypes.addAll(copy.sensorTypes);
		weaponTypes.addAll(copy.weaponTypes);

      for(UAVType uavType : copy.uavTypes.values())
      {
         uavTypes.put(uavType.getTypeID(), uavType);
      }

		for(TargetType tt : copy.targetTypes.values())
		{
			targetTypes.put(tt.getTypeID(), tt);
		}
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
    * Retrieve a specific uav type.
    *
    * @param typeID
    *            The ID of the type to retrieve.
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
    *            This data will be stored.
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

	/**
	 * Retrieve a specific target type.
	 *
	 * @param typeID
	 *            The ID of the type to retrieve.
	 * @return The requested target type or null if no such type exists.
	 */
	public TargetType getTargetType(int typeID)
	{
		return targetTypes.get(typeID);
	}

	/**
	 * Store a new type of target.
	 *
	 * @param type
	 *            This data will be stored.
	 */
	public void addTargetType(TargetType type)
	{
		targetTypes.put(type.getTypeID(), type);
	}

	/**
	 * Get an unmodifiable view of all the known target types.
	 *
	 * @return
	 */
	public Collection<TargetType> getAllTargetTypes()
	{
		return Collections.unmodifiableCollection(targetTypes.values());
	}

	/**
	 * Get a modifiable list of all known target types.
	 *
	 * @return The list of known target types.
	 */
	/*
	 * public List<TargetType> getTargetTypes() { return targetTypes; }
	 */

	/**
	 * Retrieve the {@link SensorType} corresponding to the given type ID.
	 *
	 * @param typeID
	 *            Find the {@link SensorType} with this type ID.
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
	 *            Find the {@link WeaponType} with this type ID.
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
