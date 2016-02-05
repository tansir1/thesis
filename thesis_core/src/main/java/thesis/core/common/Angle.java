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
    * Get the cosine of the angle after adjusting the 0 value as being straight
    * up to north.
    *
    * @param angle
    *           The angle to compute in degrees.
    * @return The cosine of the current angle value.
    */
   public static double cosNorthUp(double angle)
   {
      return Math.cos(Math.toRadians(angle + 90));
   }

   /**
    * Get the sine of the angle after adjusting the 0 value as being straight up
    * to north.
    *
    * @param angle
    *           The angle to compute in degrees.
    * @return The sine of the current angle value.
    */
   public static double sinNorthUp(double angle)
   {
      return Math.sin(Math.toRadians(angle + 90));
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
