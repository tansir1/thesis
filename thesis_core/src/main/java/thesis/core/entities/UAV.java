package thesis.core.entities;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.LinearSpeed;
import thesis.core.common.WorldCoordinate;

public class UAV
{
   private UAVType type;
   private WorldCoordinate position;

   private Angle orientation;

   public UAV(UAVType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      position = new WorldCoordinate();
      orientation = new Angle();
   }

   public UAVType getType()
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
    *           Advance the simulation forward by this many milliseconds.
    */
   public void stepSimulation(long deltaTimeMS)
   {
      Distance northing = new Distance();
      Distance easting = new Distance();

      double deltaSeconds = deltaTimeMS / 1000.0;
      LinearSpeed spd = type.getMaxSpd();

      // east distance = time * speed * east component
      easting.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * orientation.cosNorthUp());
      // north distance = time * speed * north component
      northing.setAsMeters(deltaSeconds * spd.asMeterPerSecond() * orientation.sinNorthUp());
System.out.print("N: " + northing + " E: " + easting + "\n");
      position.translate(northing, easting);
   }
}
