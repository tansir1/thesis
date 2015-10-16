package thesis.core.common;

import java.text.DecimalFormat;

/**
 * Generic distance container providing conversions to multiple unit types.
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

   /**
    * Add the given distance to the calling distance.
    * 
    * @param addMe
    *           This amount will be added to the calling instance of
    *           {@link Distance}.
    */
   public void add(Distance addMe)
   {
      valueMeters += addMe.valueMeters;
   }

   /**
    * Subtract the given distance from the calling distance.
    * 
    * @param subtractMe
    *           This amount will be subtracted from the calling instance of
    *           {@link Distance}.
    */
   public void subtract(Distance subtractMe)
   {
      valueMeters -= subtractMe.valueMeters;
   }

   /**
    * Negate this distance value.
    * 
    * If the distance is 10 then it becomes -10 and vice versa. Mostly used for
    * subtracting distances by adding a negated distance.
    */
   public void negate()
   {
      valueMeters = -valueMeters;
   }

   /**
    * Scale this distance by the given scaling factor.
    * 
    * @param scalingFactor
    *           Multiply the distance by this percentage.
    */
   public void scale(double scalingFactor)
   {
      valueMeters *= scalingFactor;
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
      if (valueMeters < o.valueMeters)
      {
         return -1;
      }
      else if (valueMeters > o.valueMeters)
      {
         return 1;
      }
      else
      {
         return 0;
      }
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
      temp = Double.doubleToLongBits(valueMeters);
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
      Distance other = (Distance) obj;
      //if (Double.doubleToLongBits(valueMeters) != Double.doubleToLongBits(other.valueMeters))
        // return false;
      
      final double EPS_THRESHOLD = 0.000001;
      if(Math.abs(valueMeters - other.valueMeters) > EPS_THRESHOLD)
         return false;
      
      return true;
   }

}
