package thesis.core;

import java.util.List;
import java.util.Random;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.entities.TargetType;
import thesis.core.entities.Weapon;
import thesis.core.entities.WeaponType;
import thesis.core.entities.uav.UAVType;
import thesis.core.entities.uav.sensors.SensorType;
import thesis.core.serialization.entities.EntityTypes;

/**
 * A collection of utility functions for running unit tests.
 */
public class TestUtils
{
   private static final Random rand = new Random(42);

   /**
    * Generates a random world coordinate within a 100x100 km box.
    *
    * @return A random world coordinate.
    */
   public static WorldCoordinate randWorldCoord()
   {
      WorldCoordinate wc = new WorldCoordinate();
      Distance north = new Distance();
      Distance east = new Distance();

      north.setAsMeters(rand.nextDouble() * 100000);
      east.setAsMeters(rand.nextDouble() * 100000);

      wc.setCoordinate(north, east);

      return wc;
   }

   /**
    * Generates a random sensor type.
    *
    * @return A random sensor type.
    */
   public static SensorType randSensorType()
   {
      SensorType st = new SensorType(rand.nextInt());

      st.getMinRange().setAsMeters(rand.nextDouble() * 100);
      // Add min range to guarantee max > min
      st.getMaxRange().setAsMeters(st.getMinRange().asMeters() + rand.nextDouble() * 2000);
      st.getFov().setAsDegrees(rand.nextDouble() * 120 + 10);// min of 10 degree
                                                             // FOV
      st.getMaxSlewRate().setAsDegreesPerSecond(rand.nextDouble() * 10 + 1);
      ;// min 1 deg/s

      return st;
   }

   /**
    * Generates a random weapon type.
    *
    * @return A random weapon type.
    */
   public static WeaponType randWeaponType()
   {
      WeaponType wt = new WeaponType(rand.nextInt());

      wt.getMinRange().setAsMeters(rand.nextDouble() * 100);
      // Add min range to guarantee max > min
      wt.getMaxRange().setAsMeters(wt.getMinRange().asMeters() + rand.nextDouble() * 2000);
      wt.getFov().setAsDegrees(rand.nextDouble() * 120 + 10);// min of 10 degree

      return wt;
   }

   /**
    * Generates a random target type.
    *
    * @return A random target type.
    */
   public static TargetType randTargetType()
   {
      TargetType tt = new TargetType(rand.nextInt());
      tt.getMaxSpeed().setAsMetersPerSecond(rand.nextDouble() * 5);
      return tt;
   }

   /**
    * Generates a random UAV type.
    *
    * @return A random UAV type.
    */
   public static UAVType randUAVType(List<WeaponType> wpns, List<SensorType> sensors)
   {
      UAVType uavType = new UAVType(rand.nextInt());
      uavType.getMaxSpd().setAsMetersPerSecond(rand.nextDouble() * 20);
      uavType.getMinTurnRadius().setAsMeters((rand.nextDouble() * 1000) + 200);
      uavType.init();

      int numWpnTypes = (int) (wpns.size() * rand.nextDouble());
      for (int i = 0; i < numWpnTypes; ++i)
      {
         WeaponType wpnType = wpns.get(rand.nextInt(wpns.size()));
         Weapon wpn = new Weapon(wpnType);
         wpn.setQuantity(rand.nextInt(5) + 1);
         uavType.getWeapons().add(wpn);
      }

      int numSensorTypes = (int) (sensors.size() * rand.nextDouble());
      for (int i = 0; i < numSensorTypes; ++i)
      {
         SensorType sensorType = sensors.get(rand.nextInt(sensors.size()));
         uavType.getSensors().add(sensorType);
      }

      return uavType;
   }

   public static EntityTypes randEntityTypes()
   {
      return randEntityTypes(3,3,4,7);
   }

   public static EntityTypes randEntityTypes(int numSensorTypes, int numWeaponTypes, int numUAVTypes, int numTgtTypes)
   {
      EntityTypes entTypes = new EntityTypes();

      for(int i=0; i<numSensorTypes; ++i)
      {
         entTypes.getSensorTypes().add(TestUtils.randSensorType());
      }

      for(int i=0; i<numWeaponTypes; ++i)
      {
         entTypes.getWeaponTypes().add(TestUtils.randWeaponType());
      }

      for (int i = 0; i < numUAVTypes; ++i)
      {
         entTypes.addUAVType(TestUtils.randUAVType(entTypes.getWeaponTypes(), entTypes.getSensorTypes()));
      }

      for (int i=0; i<numTgtTypes; ++i)
      {
         entTypes.addTargetType(TestUtils.randTargetType());
      }

      return entTypes;
   }
}
