package thesis.core;

import java.util.Random;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.entities.SensorType;
import thesis.core.entities.WeaponType;

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
}
