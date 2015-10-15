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
}
