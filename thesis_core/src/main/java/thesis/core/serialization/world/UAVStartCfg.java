package thesis.core.serialization.world;

import thesis.core.common.WorldCoordinate;

/**
 * Configuration data required to initialize a UAV.
 */
public class UAVStartCfg
{
   private int type;
   private WorldCoordinate location;
   private double orientation;

   public UAVStartCfg()
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

   public double getOrientation()
   {
      return orientation;
   }

   public void setOrientation(double angle)
   {
      orientation = angle;
   }
}
