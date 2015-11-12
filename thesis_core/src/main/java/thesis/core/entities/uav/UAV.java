package thesis.core.entities.uav;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.LinearSpeed;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class UAV
{
   private UAVType type;
   private WorldPose pose;

   public UAV(UAVType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;
   }

   public UAVType getType()
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

   public WorldPose getPose()
   {
      return pose;
   }
   
   /**
    * Step the simulation forward by the requested amount of time.
    *
    * @param deltaTimeMS
    *           Advance the simulation forward by this many milliseconds.
    */
   public void stepSimulation(long deltaTimeMS)
   {
      Distance northing = new Distance();
      Distance easting = new Distance();

      double deltaSeconds = deltaTimeMS / 1000.0;
      LinearSpeed spd = type.getMaxSpd();

      Angle hdg = pose.getHeading();
      
      // east distance = time * speed * east component
      easting.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * hdg.cosNorthUp());
      // north distance = time * speed * north component
      northing.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * hdg.sinNorthUp());

      pose.getCoordinate().translate(northing, easting);
   }
}
