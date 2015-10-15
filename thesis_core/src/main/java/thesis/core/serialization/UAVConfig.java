package thesis.core.serialization;

import thesis.core.common.Angle;
import thesis.core.world.WorldCoordinate;

/**
 * Configuration data required to initialize a UAV.
 */
public class UAVConfig
{
   private int type;
   private WorldCoordinate location;
   private Angle orientation;
   
   public UAVConfig()
   {
      type = -1;
      location = new WorldCoordinate();
      orientation = new Angle();
   }
   
   public void setUAVType(int type)
   {
      this.type = type;
   }
   
   public int getUAVType()
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
