package thesis.core.common;

import java.text.DecimalFormat;

/**
 *Generic distance container providing conversions to multiple unit types.
 */
public class Distance implements Comparable<Distance>
{
   private static DecimalFormat decFormat = new DecimalFormat("0.00");

   /**
    * The distance stored internally in meters.
    */
   private double valueMeters;

   public Distance()
   {
      valueMeters = 0.0;
   }

   public Distance(Distance copy)
   {
      valueMeters = copy.valueMeters;
   }

   public void copy(Distance copy)
   {
      valueMeters = copy.valueMeters;
   }

   public double asMeters()
   {
      return valueMeters;
   }
   
   public double asKilometers()
   {
      return valueMeters * 0.001;
   }

   public double asFeet()
   {
      return valueMeters * 3.28084;
   }

   public void setAsFeet(double feet)
   {
      valueMeters = feet * 0.3048;
   }

   public void setAsMeters(double meters)
   {
      valueMeters = meters;
   }
   
   public void setAsKilometers(double kilometers)
   {
      valueMeters = kilometers * 1000;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(decFormat.format(valueMeters));
      sb.append("m");
      return sb.toString();
   }

   @Override
   public int compareTo(Distance o)
   {
      if(valueMeters < o.valueMeters)
      {
         return -1;
      }
      else if(valueMeters > o.valueMeters)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }
}
