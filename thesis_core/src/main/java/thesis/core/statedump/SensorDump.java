package thesis.core.statedump;

import thesis.core.common.Trapezoid;
import thesis.core.common.WorldCoordinate;
import thesis.core.sensors.Sensor;

@Deprecated
public class SensorDump
{
   private final int type;//Will be -1 on an update dump
   private final int id;
   private double heading;
   private final WorldCoordinate lookAtGoal;
   private final WorldCoordinate lookAtCur;
   private final Trapezoid viewRegion;


   public SensorDump(Sensor snsr)
   {
      this.type = snsr.getType();
      this.id = snsr.getID();

      heading = 0;
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Trapezoid();
      dumpUpdate(snsr);
   }

   public SensorDump(int type, int id)
   {
      this.type = type;
      this.id = id;

      heading = 0;
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Trapezoid();
   }

   public SensorDump(int id)
   {
      this.type = -1;
      this.id = id;

      heading = 0;
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Trapezoid();
   }

   public void dumpUpdate(Sensor snsr)
   {
      heading = snsr.getAzimuth();
      lookAtCur.setCoordinate(snsr.getViewCenter());
      lookAtGoal.setCoordinate(snsr.getLookAtGoal());
      viewRegion.copy(snsr.getViewFootPrint());
   }

   public void dumpUpdate(SensorDump snsr)
   {
      heading = snsr.getAzimuth();
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
      return heading;
   }

   public void setAzimuth(double az)
   {
      this.heading = az;
   }

   public Trapezoid getViewFootPrint()
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
