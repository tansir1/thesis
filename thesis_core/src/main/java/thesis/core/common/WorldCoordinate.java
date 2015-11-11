package thesis.core.common;

import java.text.DecimalFormat;

/**
 * Physical position within the world.
 */
public class WorldCoordinate
{
   /**
    * Distance north from the world origin.
    */
   private Distance north;

   /**
    * Distance east from the world origin.
    */
   private Distance east;

   /**
    * Formatter for printing coordinate data in toString().
    *
    * @see toString
    */
   static final DecimalFormat s_PRINT_FORMAT = new DecimalFormat("0.000");

   /**
    * Initialize a world coordinate at the origin.
    */
   public WorldCoordinate()
   {
      north = new Distance();
      east = new Distance();
   }

   /**
    * Initialize a world coordinate at the given location.
    *
    * @param north
    *           The distance north from the world origin. The value is copied.
    * @param east
    *           The distance east from the world origin. The value is copied.
    */
   public WorldCoordinate(Distance north, Distance east)
   {
      this.north = new Distance();
      this.east = new Distance();
      this.north.copy(north);
      this.east.copy(east);
   }

   /**
    * Initialize a world coordinate by cloning the given coordinate.
    *
    * @param wc
    *           Clone this coordinate.
    */
   public WorldCoordinate(WorldCoordinate wc)
   {
      this.north = new Distance();
      this.east = new Distance();
      this.north.copy(wc.north);
      this.east.copy(wc.east);
   }

   /**
    * Get the distance north from the world origin.
    *
    * @return Distance north.
    */
   public Distance getNorth()
   {
      return north;
   }

   /**
    * Get the distance east from the world origin.
    *
    * @return Distance east.
    */
   public Distance getEast()
   {
      return east;
   }

   /**
    * Set the distance north from the world origin.
    *
    * @param north
    *           Distance north.
    */
   public void setNorth(Distance north)
   {
      this.north.copy(north);
   }

   /**
    * Set the distance east from the world origin.
    *
    * @param north
    *           Distance east.
    */
   public void setEast(Distance east)
   {
      this.east.copy(east);
   }

   /**
    * Set the distance north and east from the world origin.
    *
    * @param north
    *           Distance north.
    * @param east
    *           Distance east.
    */
   public void setCoordinate(Distance north, Distance east)
   {
      this.north.copy(north);
      this.east.copy(east);
   }

   /**
    * Set the distance north and east from the world origin by copying the given
    * coordinate.
    *
    * @param copy
    *           The position in this coordinate will be copied into the calling
    *           coordinate.
    */
   public void setCoordinate(WorldCoordinate copy)
   {
      this.north.copy(copy.north);
      this.east.copy(copy.east);
   }

   /**
    * Shift the current coordinate position by the specified amount.
    *
    * @param deltaNorth
    *           Move the coordinate north by this far.
    * @param deltaEast
    *           Move the coordinate east by this far.
    */
   public void translate(Distance deltaNorth, Distance deltaEast)
   {
      this.north.add(deltaNorth);
      this.east.add(deltaEast);
   }

   /**
    * Get the absolute bearing angle from this coordinate to the given
    * coordinate.
    *
    * @param wc
    *           Find the bearing to this coordinate.
    * @return The bearing angle from this coordinate to the given coordinate.
    */
   public Angle bearingTo(WorldCoordinate wc)
   {
      Distance delNorth = new Distance(wc.north);
      Distance delEast = new Distance(wc.east);

      delNorth.subtract(north);
      delEast.subtract(east);

      Angle retVal = new Angle();
      retVal.setAsRadians(Math.atan2(delNorth.asMeters(), delEast.asMeters()));
      return retVal;
   }

   /**
    * Get the absolute bearing from the origin to this coordinate.
    * 
    * @return The bearing angle from the origin to this coordinate.
    */
   public Angle bearingFromOrigin()
   {
      WorldCoordinate origin = new WorldCoordinate();
      return origin.bearingTo(this);
   }

   /**
    * Get the linear distance between this coordinate and the given coordinate.
    *
    * @param wc
    *           Get the distance to here.
    * @return The distance between the coordinates in kilometers.
    */
   public Distance distanceTo(WorldCoordinate wc)
   {
      Distance delNorth = new Distance(wc.north);
      Distance delEast = new Distance(wc.east);

      delNorth.subtract(north);
      delEast.subtract(east);

      double distInM = Math.sqrt(delNorth.asMeters() * delNorth.asMeters() + delEast.asMeters() * delEast.asMeters());
      Distance retVal = new Distance();
      retVal.setAsMeters(distInM);
      return retVal;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[N");
      sb.append(north);
      sb.append(", E");
      sb.append(east);
      sb.append("]");
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
      result = prime * result + ((east == null) ? 0 : east.hashCode());
      result = prime * result + ((north == null) ? 0 : north.hashCode());
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
      WorldCoordinate other = (WorldCoordinate) obj;
      if (east == null)
      {
         if (other.east != null)
            return false;
      }
      else if (!east.equals(other.east))
         return false;
      if (north == null)
      {
         if (other.north != null)
            return false;
      }
      else if (!north.equals(other.north))
         return false;
      return true;
   }

}
