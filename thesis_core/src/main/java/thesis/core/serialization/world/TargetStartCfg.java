package thesis.core.serialization.world;

import thesis.core.common.WorldCoordinate;

/**
 * Configuration data required to initialize a target.
 */
public class TargetStartCfg
{
   private int type;
   private WorldCoordinate location;
   private double orientation;

   public TargetStartCfg()
   {
      type = -1;
      location = new WorldCoordinate();
      orientation = 0;
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

   public double getOrientation()
   {
      return orientation;
   }

   public void setOrientation(double angle)
   {
      orientation = angle;
   }
}
