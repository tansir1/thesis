package thesis.core.common;

import java.text.DecimalFormat;

/**
 * Physical position within the world.
 */
public class WorldCoordinate
{
   /**
    * Distance north from the world origin in meters.
    */
   private double north;

   /**
    * Distance east from the world origin in meters.
    */
   private double east;

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
      north = 0;
      east = 0;
   }

   /**
    * Initialize a world coordinate at the given location.
    *
    * @param north
    *           The distance north from the world origin in meters.
    * @param east
    *           The distance east from the world origin in meters.
    */
   public WorldCoordinate(final double north, final double east)
   {
      this.north = north;
      this.east = east;
   }

   /**
    * Initialize a world coordinate by cloning the given coordinate.
    *
    * @param wc
    *           Clone this coordinate.
    */
   public WorldCoordinate(final WorldCoordinate wc)
   {
      this.north = wc.north;
      this.east = wc.east;
   }

   /**
    * Get the distance north from the world origin in meters.
    *
    * @return Distance north.
    */
   public double getNorth()
   {
      return north;
   }

   /**
    * Get the distance east from the world origin in meters.
    *
    * @return Distance east.
    */
   public double getEast()
   {
      return east;
   }

   /**
    * Set the distance north from the world origin.
    *
    * @param north
    *           Distance north in meters.
    */
   public void setNorth(final double north)
   {
      this.north = north;
   }

   /**
    * Set the distance east from the world origin.
    *
    * @param north
    *           Distance east in meters.
    */
   public void setEast(final double east)
   {
      this.east = east;
   }

   /**
    * Set the distance north and east from the world origin.
    *
    * @param north
    *           Distance north in meters.
    * @param east
    *           Distance east in meters.
    */
   public void setCoordinate(final double north, final double east)
   {
      this.north = north;
      this.east = east;
   }

   /**
    * Set the distance north and east from the world origin by copying the given
    * coordinate.
    *
    * @param copy
    *           The position in this coordinate will be copied into the calling
    *           coordinate.
    */
   public void setCoordinate(final WorldCoordinate copy)
   {
      this.north = copy.north;
      this.east = copy.east;
   }

   /**
    * Shift the current coordinate position by the specified amount.
    *
    * @param deltaNorth
    *           Move the coordinate north (meters) by this far.
    * @param deltaEast
    *           Move the coordinate east (meters) by this far.
    */
   public void translateCart(final double deltaNorth, final double deltaEast)
   {
      this.north += deltaNorth;
      this.east += deltaEast;
   }

   /**
    * Shift the current coordinate position by the specified amount.
    *
    * @param heading
    *           Move along this heading (degrees).
    * @param distance
    *           Move this far along the heading (meters).
    */
   public void translatePolar(final double heading, final double distance)
   {
      double deltaNorth = Math.sin(Math.toRadians(heading)) * distance;
      double deltaEast = Math.cos(Math.toRadians(heading)) * distance;
      translateCart(deltaNorth, deltaEast);
   }

   /**
    * Get the absolute bearing angle from this coordinate to the given
    * coordinate.
    *
    * @param wc
    *           Find the bearing to this coordinate.
    * @return The bearing angle from this coordinate to the given coordinate in
    *         degrees.
    */
   public double bearingTo(final WorldCoordinate wc)
   {
      double delNorth = wc.north - north;
      double delEast = wc.east - east;
      double bearing = Math.toDegrees(Math.atan2(delNorth, delEast));
      return Angle.normalize360(bearing);
   }

   /**
    * Get the absolute bearing from the origin to this coordinate.
    *
    * @return The bearing angle from the origin to this coordinate in degrees.
    */
   public double bearingFromOrigin()
   {
      WorldCoordinate origin = new WorldCoordinate();
      return origin.bearingTo(this);
   }

   /**
    * Get the linear distance between this coordinate and the given coordinate.
    *
    * @param wc
    *           Get the distance to here.
    * @return The distance between the coordinates in meters.
    */
   public double distanceTo(final WorldCoordinate wc)
   {
      final double delNorth = wc.north - north;
      final double delEast = wc.east - east;

      return Math.sqrt(delNorth * delNorth + delEast * delEast);
   }

   /**
    * Translate this coordinate towards the specified coordinate.
    *
    * @param moveTowards
    *           Translate towards this coordinate.
    * @param percentage
    *           The percentage of the distance towards the coordinate to
    *           translate.
    */
   public void interpolateTowards(final WorldCoordinate moveTowards, double percentage)
   {
      final double delNorth = moveTowards.north - north;
      final double delEast = moveTowards.east - east;
      translateCart(delNorth * percentage, delEast * percentage);
   }

   @Override
   public String toString()
   {
      return String.format("[N%.2fm, E%.2fm]", north, east);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(east);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(north);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

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
      if (Double.doubleToLongBits(east) != Double.doubleToLongBits(other.east))
         return false;
      if (Double.doubleToLongBits(north) != Double.doubleToLongBits(other.north))
         return false;
      return true;
   }

}
