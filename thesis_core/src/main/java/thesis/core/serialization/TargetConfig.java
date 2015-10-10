package thesis.core.serialization;

import thesis.core.common.Angle;
import thesis.core.world.WorldCoordinate;

public class TargetConfig
{
   private WorldCoordinate location;
   private Angle orientation;
   
   public TargetConfig()
   {
      location = new WorldCoordinate();
      orientation = new Angle();
   }
}
