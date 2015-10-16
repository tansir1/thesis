package thesis.core.entities;

import thesis.core.common.WorldCoordinate;

public class Target
{
   private TargetType type;
   private WorldCoordinate position;
   
   public Target(TargetType type)
   {
      if(type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }
      
      this.type = type;
      position = new WorldCoordinate();
   }
   
   public WorldCoordinate getCoordinate()
   {
      return position;
   }
   
   
}
