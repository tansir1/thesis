package thesis.core.common;

import java.text.DecimalFormat;

/**
 * Unit agnostic representation of angular speed.
 */
public class AngularSpeed implements Comparable<AngularSpeed>
{
   private static DecimalFormat decFormat = new DecimalFormat("0.00");
   
   /**
    * Internal representation of speed in degrees per second.
    */
   private double degPerSec;
   
   public AngularSpeed()
   {
      degPerSec = 0;
   }
   
   public AngularSpeed(AngularSpeed copy)
   {
      this.degPerSec = copy.degPerSec;
   }
   
   public void copy(AngularSpeed copy)
   {
      this.degPerSec = copy.degPerSec;
   }
   
   public void setAsDegreesPerSecond(double spd)
   {
      this.degPerSec = spd;
   }
   
   public void setAsRadiansPerSecond(double spd)
   {
      this.degPerSec = spd * 57.2958;
   }
   
   public double asRadiansPerSecond()
   {
      return degPerSec * 0.0174533;
   }
   
   public double asDegreesPerSecond()
   {
      return degPerSec;
   }
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(decFormat.format(degPerSec));
      sb.append("deg/s");
      return sb.toString();
   }

   @Override
   public int compareTo(AngularSpeed o)
   {
      if(degPerSec < o.degPerSec)
      {
         return -1;
      }
      else if(degPerSec > o.degPerSec)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(degPerSec);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   /* (non-Javadoc)
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
      AngularSpeed other = (AngularSpeed) obj;
//      if (Double.doubleToLongBits(degPerSec) != Double.doubleToLongBits(other.degPerSec))
//         return false;
      
      final double EPS_THRESHOLD = 0.000001;
      if(Math.abs(degPerSec - other.degPerSec) > EPS_THRESHOLD)
         return false;   
      
      return true;
   }
  
}
