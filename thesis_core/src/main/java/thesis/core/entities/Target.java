package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;

public class Target
{
   private TargetType type;
   private WorldCoordinate position;

   private Angle orientation;

   public Target(TargetType type)
   {
      if(type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      position = new WorldCoordinate();
      orientation = new Angle();
   }

   public TargetType getType()
   {
	   return type;
   }

   public WorldCoordinate getCoordinate()
   {
      return position;
   }

   public Angle getOrientation()
   {
	   return orientation;
   }

	/**
	 * Step the simulation forward by the requested amount of time.
	 *
	 * @param deltaTimeMS
	 *            Advance the simulation forward by this many milliseconds.
	 */
	public void stepSimulation(long deltaTimeMS)
	{
		if(type.isMobile())
		{

		}
	}
}
