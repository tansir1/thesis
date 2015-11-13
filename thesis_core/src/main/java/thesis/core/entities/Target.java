package thesis.core.entities;

import thesis.core.SimModel;
import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.LinearSpeed;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class Target
{
   private TargetType type;
   private WorldPose pose;

   public Target(TargetType type)
   {
      if(type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      pose = new WorldPose();
   }

   public TargetType getType()
   {
	   return type;
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   public Angle getHeading()
   {
	   return pose.getHeading();
   }

	/**
	 * Step the simulation forward by {@link SimModel#SIM_STEP_RATE_MS} amount of time.
	 	 */
	public void stepSimulation()
	{
		if(type.isMobile())
		{
			Distance northing = new Distance();
			Distance easting = new Distance();

			double deltaSeconds = SimModel.SIM_STEP_RATE_MS / 1000.0;
			LinearSpeed spd = type.getMaxSpeed();

	      // east distance = time * speed * east component
	      easting.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * pose.getHeading().cosNorthUp());
	      // north distance = time * speed * north component
	      northing.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * pose.getHeading().sinNorthUp());

			pose.getCoordinate().translate(northing, easting);
		}

	}
}
