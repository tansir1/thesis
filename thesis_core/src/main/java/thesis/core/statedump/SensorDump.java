package thesis.core.statedump;

import thesis.core.common.Rectangle;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.sensors.Sensor;

public class SensorDump
{
   private final int type;
   private final int id;
   private final WorldPose pose;
   private final WorldCoordinate lookAtGoal;
   private final WorldCoordinate lookAtCur;
   private final Rectangle viewRegion;


   public SensorDump(Sensor snsr)
   {
      this.type = snsr.getType();
      this.id = snsr.getID();

      pose = new WorldPose();
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Rectangle();
      dumpUpdate(snsr);
   }

   public void dumpUpdate(Sensor snsr)
   {
      pose.copy(snsr.getPose());
      lookAtCur.setCoordinate(snsr.getViewCenter());
      lookAtGoal.setCoordinate(snsr.getLookAtGoal());
      viewRegion.copy(snsr.getViewFootPrint());
   }

   public int getType()
   {
      return type;
   }

   public int getID()
   {
      return id;
   }

   /**
    * Get the current azimuth of the sensor in relation to the world's zero
    * degree mark. This angle is in absolute world coordinates.
    *
    * @return The azimuth of the sensor in absolute world coordinates (degrees).
    */
   public double getAzimuth()
   {
      return pose.getHeading();
   }

   public Rectangle getViewFootPrint()
   {
      return viewRegion;
   }

   public WorldCoordinate getViewCenter()
   {
      return lookAtCur;
   }

   public WorldCoordinate getLookAtGoal()
   {
      return lookAtGoal;
   }
}
