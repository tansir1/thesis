package thesis.core.common;

public class Angle
{
   /**
    * Normalize the angle such that it is bounded by [0,360).
    *
    * @param angle
    *           The angle to compute in degrees.
    * @return The normalized angle.
    */
   public static double normalize360(double angle)
   {
      angle = angle % 360d;// Java allows modulo with floating point
      // values
      if (angle < 0)
      {
         angle += 360.0;
      }
      return angle;
   }

   /**
    * Normalize the angle such that it is bounded by [-180,180).
    *
    * @param angle
    *           The angle to compute in degrees.
    * @return The normalized angle.
    */
   public static double normalizeNegPiToPi(double angle)
   {
      angle = (angle + 180d) % 360d;
      if (angle < 0)
      {
         angle += 360d;
      }
      angle -= 180d;
      return angle;
   }

   /**
    * Check if an angle is bounded by two other angles.
    *
    * @param angle
    *           Test if this angle (degrees) is between the other two angles.
    * @param leftBnd
    *           The left angular bound (degrees).
    * @param rightBnd
    *           The right angular bound (degrees).
    * @return True if the given angle is between the two bound angles.
    */
   public static boolean isBetween(double angle, double leftBnd, double rightBnd)
   {
      leftBnd = normalizeNegPiToPi(leftBnd);
      rightBnd = normalizeNegPiToPi(rightBnd);

      // Make sure the right bound is greater than the left bound
      if (leftBnd > rightBnd)
      {
         double temp = leftBnd;
         leftBnd = rightBnd;
         rightBnd = temp;
      }

      angle = normalizeNegPiToPi(angle);

      return leftBnd < angle && angle < rightBnd;
   }

   /**
    * Converts a cartesian angle to a standard map angle. 0 cart -> 90 east. 90
    * cart -> 0 north. 180 cart -> 270 west. 270 cart -> 180 south.
    *
    * @param angle
    *           The cartesian angle to convert.
    * @return The map angle.
    */
   public static double cartesianAngleToNorthUp(double angle)
   {
      angle = normalize360(angle);
      angle = 90 - angle;
      angle = normalize360(angle);
      return angle;
   }

   /**
    * Create a string containing the numeric value of the angle and a degree
    * symbol.
    *
    * @param angle
    *           The angle to convert into a string.
    * @return A string containing the numeric value of the angle and a degree
    *         symbol
    */
   public static String stringHelper(double angle)
   {
      return String.format("%.3f\u00b0", angle);
   }

}
