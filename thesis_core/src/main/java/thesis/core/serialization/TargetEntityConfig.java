package thesis.core.serialization;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;

/**
 * Configuration data required to initialize a target.
 */
public class TargetEntityConfig
{
   private int type;
   private WorldCoordinate location;
   private Angle orientation;
   
   public TargetEntityConfig()
   {
      type = -1;
      location = new WorldCoordinate();
      orientation = new Angle();
   }
   
   public void setTargetType(int type)
   {
      this.type = type;
   }
   
   public int getTargetType()
   {
      return type;
   }
   
   public WorldCoordinate getLocation()
   {
      return location;
   }
   
   public Angle getOrientation()
   {
      return orientation;
   }
}
