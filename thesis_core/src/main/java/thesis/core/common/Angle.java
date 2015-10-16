package thesis.core.common;

import java.text.DecimalFormat;

public class Angle
{
   private static final DecimalFormat decFormat = new DecimalFormat("0.00");

   /**
    * Internally store the angle in degrees.
    */
   private double angleDeg;

   public Angle()
   {
      angleDeg = 0.0;
   }

   public Angle(Angle copy)
   {
      angleDeg = copy.angleDeg;
   }

   public void copy(Angle copy)
   {
      angleDeg = copy.angleDeg;
   }

   public void setAsDegrees(double deg)
   {
      angleDeg = deg;
   }

   public void setAsRadians(double rad)
   {
      angleDeg = Math.toDegrees(rad);
   }

   public double asDegrees()
   {
      return angleDeg;
   }

   public double asRadians()
   {
      return Math.toRadians(angleDeg);
   }

   /**
    * Normalize the angle such that it is bounded by [0,360).
    */
   public void normalize360()
   {
      angleDeg = angleDeg % 360.0;// Java allows modulo with floating point
                                  // values
      if (angleDeg < 0)
      {
         angleDeg += 360.0;
      }
   }

   /**
    * Normalize the angle such that it is bounded by [-180,180).
    */
   public void normalizeNegPiToPi()
   {
      angleDeg = (angleDeg + 180.0) % 360;
      if (angleDeg < 0)
      {
         angleDeg += 360.0;
      }
      angleDeg -= 180.0;
   }

   public boolean isBetween(Angle leftBnd, Angle rightBnd)
   {
      // Make copies so as to not modify the originals
      Angle left = new Angle(leftBnd);
      Angle right = new Angle(rightBnd);

      left.normalizeNegPiToPi();
      right.normalizeNegPiToPi();

      // Make sure the right bound is greater than the left bound
      if (left.asDegrees() > right.asDegrees())
      {
         double temp = left.asDegrees();
         left.copy(right);
         right.setAsDegrees(temp);
      }

      Angle me = new Angle(this);
      me.normalizeNegPiToPi();

      return left.asDegrees() < me.asDegrees() && me.asDegrees() < right.asDegrees();
   }

   /**
    * Add the value of the given angle to the calling angle.
    * 
    * @param add
    *           This amount will be added to the calling angle.
    */
   public void add(Angle add)
   {
      this.angleDeg += add.angleDeg;
   }

   /**
    * Subtract the value of the given angle from the calling angle.
    * 
    * @param subtract
    *           This amount will be subtracted from the calling angle.
    */
   public void subtract(Angle subtract)
   {
      this.angleDeg -= subtract.angleDeg;
   }

   /**
    * Return half the value of this angle.
    * 
    * @return The value of this angle divided by 2.
    */
   public Angle halfAngle()
   {
      Angle half = new Angle(this);
      half.angleDeg *= 0.5;
      return half;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(decFormat.format(angleDeg));
      sb.append("\u00b0"); // Unicode character for degree symbol
      return sb.toString();
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
      long temp;
      temp = Double.doubleToLongBits(angleDeg);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      Angle other = (Angle) obj;
      // if (Double.doubleToLongBits(angleDeg) !=
      // Double.doubleToLongBits(other.angleDeg))
      // return false;
      final double EPS_THRESHOLD = 0.000000001;
      if (Math.abs(angleDeg - other.angleDeg) > EPS_THRESHOLD)
         return false;

      return true;
   }

}
