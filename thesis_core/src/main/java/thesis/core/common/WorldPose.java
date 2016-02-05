package thesis.core.common;

/**
 * Describes the position and orientation of an entity within the world.
 *
 */
public class WorldPose
{
   private WorldCoordinate coord;
   /**
    * Heading in degrees normalized [0,360).
    */
   private double heading;

   /**
    * Initialize at the origin location with a zero heading.
    */
   public WorldPose()
   {
      coord = new WorldCoordinate();
      heading = 0d;
   }

   /**
    * Initialize a new pose by copying the given pose.
    *
    * @param copy
    *           Copy the values of this pose into the new pose.
    */
   public WorldPose(final WorldPose copy)
   {
      coord = new WorldCoordinate(copy.getCoordinate());
      heading = copy.heading;
   }

   /**
    * Initialize the pose with the specified location and heading.
    *
    * @param location
    *           Copy this value internally.
    * @param heading
    *           Copy this value internally (degrees).
    */
   public WorldPose(final WorldCoordinate location, final double heading)
   {
      this.coord = new WorldCoordinate(location);
      this.heading = Angle.normalize360(heading);
   }

   public void copy(WorldPose copy)
   {
      this.coord.setCoordinate(copy.coord);
      this.heading = Angle.normalize360(copy.heading);
   }

   public WorldCoordinate getCoordinate()
   {
      return coord;
   }

   /**
    * Get the distance east from the world origin in meters.
    *
    * @return Distance east.
    */
   public double getEast()
   {
      return coord.getEast();
   }

   /**
    * Get the distance north from the world origin in meters.
    *
    * @return Distance north.
    */
   public double getNorth()
   {
      return coord.getNorth();
   }

   /**
    * @return The heading of the UAV in degrees, [0,360).
    */
   public double getHeading()
   {
      return heading;
   }

   /**
    * Set the heading of the UAV.
    *
    * @param angle
    *           The heading in degrees.
    */
   public void setHeading(double angle)
   {
      this.heading = Angle.normalize360(angle);
   }

   @Override
   public String toString()
   {
      return String.format("[N%.2f, E%.2f, Hdg:%.2f]", coord.getNorth(), coord.getEast(), heading);
   }
}
