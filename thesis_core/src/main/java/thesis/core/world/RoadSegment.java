package thesis.core.world;

import thesis.core.common.WorldCoordinate;

public class RoadSegment
{
   private WorldCoordinate start;
   private WorldCoordinate end;

   public RoadSegment()
   {
      start = new WorldCoordinate();
      end = new WorldCoordinate();
   }

   public WorldCoordinate getStart()
   {
      return start;
   }

   public WorldCoordinate getEnd()
   {
      return end;
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
      result = prime * result + ((end == null) ? 0 : end.hashCode());
      result = prime * result + ((start == null) ? 0 : start.hashCode());
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
      RoadSegment other = (RoadSegment) obj;
      if (end == null)
      {
         if (other.end != null)
            return false;
      }
      else if (!end.equals(other.end))
         return false;
      if (start == null)
      {
         if (other.start != null)
            return false;
      }
      else if (!start.equals(other.start))
         return false;
      return true;
   }

}
