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
    * Initialize a new pose by copying the given pose.
    * 
    * @param copy Copy the values of this pose into the new pose.
    */
   public WorldPose(final WorldPose copy)
   {
      coord = new WorldCoordinate(copy.getCoordinate());
      heading = new Angle(copy.getHeading());
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
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[N");
      sb.append(coord.getNorth());
      sb.append(", E");
      sb.append(coord.getEast());
      sb.append(", Hdg: ");
      sb.append(heading);
      sb.append("]");
      return sb.toString();
   }
}
