package thesis.core.common;

import java.text.DecimalFormat;

/**
 * Unit agnostic representation of linear speed.
 */
public class LinearSpeed implements Comparable<LinearSpeed>
{
   private static DecimalFormat decFormat = new DecimalFormat("0.00");
   
   /**
    * Internal representation of speed in meters / second.
    */
   private double valueMpS;

   public LinearSpeed()
   {
      valueMpS = 0;
   }

   public LinearSpeed(LinearSpeed spd)
   {
      this.valueMpS = spd.valueMpS;
   }

   public void copy(LinearSpeed spd)
   {
      this.valueMpS = spd.valueMpS;
   }

   /**
    * Increase this speed by the amount in the given speed object.
    * 
    * @param add
    *           This amount of speed will be added to the calling instance of
    *           {@link LinearSpeed}. Can be a negative speed.
    */
   public void addSpeed(LinearSpeed add)
   {
      this.valueMpS = add.valueMpS;
   }

   public void setAsMetersPerSecond(double spd)
   {
      this.valueMpS = spd;
   }

   public void setAsMilesPerHour(double spd)
   {
      this.valueMpS = spd * 0.44704;
   }

   public void setAsKilometerPerHour(double spd)
   {
      this.valueMpS = spd * 0.277778;
   }

   public double asMeterPerSecond()
   {
      return valueMpS;
   }

   public double asMilesPerHour()
   {
      return valueMpS * 2.23694;
   }

   public double asKilometerPerHour()
   {
      return valueMpS * 3.6;
   }

   @Override
   public int compareTo(LinearSpeed o)
   {
      if(valueMpS < o.valueMpS)
      {
         return -1;
      }
      else if(valueMpS > o.valueMpS)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(decFormat.format(valueMpS));
      sb.append("m/s");
      return sb.toString();
   }
}
