package thesis.core;

import java.util.Random;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;

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
}
