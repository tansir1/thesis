package thesis.core.world;

import java.text.DecimalFormat;

/**
 * Physical position within the world.
 */
public class WorldCoordinate
{
   /**
    * Distance north in kilometers from the world origin.
    */
   private double north;

   /**
    * Distance east in kilometers from the world origin.
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
      north = 0.0;
      east = 0.0;
   }

   /**
    * Initialize a world coordinate at the given location.
    * 
    * @param north
    *           The distance north in kilometers from the world origin.
    * @param east
    *           The distance east in kilometers from the world origin.
    */
   public WorldCoordinate(double north, double east)
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
   public WorldCoordinate(WorldCoordinate wc)
   {
      this.north = wc.north;
      this.east = wc.east;
   }

   /**
    * Get the distance north from the world origin in kilometers.
    * 
    * @return Distance north in km.
    */
   public double getNorth()
   {
      return north;
   }

   /**
    * Get the distance east from the world origin in kilometers.
    * 
    * @return Distance east in km.
    */
   public double getEast()
   {
      return east;
   }

   /**
    * Set the distance north from the world origin in kilometers.
    * 
    * @param north
    *           Distance north in km.
    */
   public void setNorth(double north)
   {
      this.north = north;
   }

   /**
    * Set the distance north from the world origin in kilometers.
    * 
    * @param north
    *           Distance north in km.
    */
   public void setEast(double east)
   {
      this.east = east;
   }

   /**
    * Set the distance north and east from the world origin in kilometers.
    * 
    * @param north
    *           Distance north in km.
    * @param east
    *           Distance east in km.
    */
   public void setCoordinate(double north, double east)
   {
      this.north = north;
      this.east = east;
   }

   /**
    * Shift the current coordinate position by the specified amount.
    * 
    * @param deltaNorth
    *           Move the coordinate north by this far in kilometers.
    * @param deltaEast
    *           Move the coordinate east by this far in kilometers.
    */
   public void translate(double deltaNorth, double deltaEast)
   {
      this.north += deltaNorth;
      this.east += deltaEast;
   }

   /**
    * Get the absolute bearing angle (in degrees) from this coordinate to the
    * given coordinate.
    * 
    * @param wc
    *           Find the bearing to this coordinate.
    * @return The bearing angle in degrees from this coordinate to the given
    *         coordinate.
    */
   public double bearingTo(WorldCoordinate wc)
   {
      double delNorth = wc.north - north;
      double delEast = wc.east - east;

      return Math.toDegrees(Math.atan2(delNorth, delEast));
   }

   /**
    * Get the linear distance in kilometers between this coordinate and the
    * given coordinate.
    * 
    * @param wc
    *           Get the distance to here.
    * @return The distance between the coordinates in kilometers.
    */
   public double distanceTo(WorldCoordinate wc)
   {
      double delNorth = wc.north - north;
      double delEast = wc.east - east;

      return Math.sqrt(delNorth * delNorth + delEast * delEast);
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(s_PRINT_FORMAT.format(north));
      sb.append("N, ");
      sb.append(s_PRINT_FORMAT.format(east));
      sb.append("E");
      return sb.toString();
   }
}
