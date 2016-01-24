package thesis.core.serialization.world;

import thesis.core.common.WorldCoordinate;

/**
 * Configuration data required to initialize a UAV.
 */
public class UAVEntityConfig
{
   private int type;
   private WorldCoordinate location;
   private float orientation;

   public UAVEntityConfig()
   {
      type = -1;
      location = new WorldCoordinate();
      orientation = 0;
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

   public float getOrientation()
   {
      return orientation;
   }

   public void setOrientation(float angle)
   {
      orientation = angle;
   }
}
