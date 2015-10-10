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
      angleDeg = angleDeg % 360.0;//Java allows modulo with floating point values
      if(angleDeg < 0)
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
      if(angleDeg < 0)
      {
         angleDeg += 360.0;
      }
      angleDeg -= 180.0;
   }
   
   public boolean isBetween(Angle leftBnd, Angle rightBnd)
   {
      //Make copies so as to not modify the originals
      Angle left = new Angle(leftBnd);
      Angle right = new Angle(rightBnd);
      
      left.normalizeNegPiToPi();
      right.normalizeNegPiToPi();
      
      //Make sure the right bound is greater than the left bound
      if(left.asDegrees() > right.asDegrees())
      {
         double temp = left.asDegrees();
         left.copy(right);
         right.setAsDegrees(temp);
      }
      
      Angle me = new Angle(this);
      me.normalizeNegPiToPi();
      
      return left.asDegrees() < me.asDegrees() && me.asDegrees() < right.asDegrees();
   }
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(decFormat.format(angleDeg));
      sb.append("\u00b0"); //Unicode character for degree symbol
      return sb.toString();
   }
}
