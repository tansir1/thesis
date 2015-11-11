package thesis.core.common;

/**
 * Describes the position and orientation of an entity within the world.
 *
 */
public class WorldPose
{
   private WorldCoordinate coord;
   private Angle heading;

   /**
    * Initialize at the origin location with a zero heading.
    */
   public WorldPose()
   {
      coord = new WorldCoordinate();
      heading = new Angle();
   }

   /**
    * Initialize the pose with the specified location and heading.
    * 
    * @param location
    *           Copy this value internally.
    * @param heading
    *           Copy this value internally.
    */
   public WorldPose(final WorldCoordinate location, final Angle heading)
   {
      this.coord = new WorldCoordinate(location);
      this.heading = new Angle(heading);
   }

   public void copy(WorldPose copy)
   {
      this.coord.setCoordinate(copy.coord);
      this.heading.copy(copy.heading);
   }
   
   public WorldCoordinate getCoordinate()
   {
      return coord;
   }

   public Distance getEast()
   {
      return coord.getEast();
   }
   
   public Distance getNorth()
   {
      return coord.getNorth();
   }
   
   public Angle getHeading()
   {
      return heading;
   }
}
