package thesis.core.entities.uav.sensors;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.Rectangle;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class Sensor
{
   private SensorType type;
   private WorldPose pose;
   private WorldCoordinate lookAtGoal;
   private WorldCoordinate lookAtCur;
   private Rectangle viewRegion;

   public Sensor(SensorType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      pose = new WorldPose();
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Rectangle();
   }

   public SensorType getType()
   {
      return type;
   }

   /**
    * Get the current azimuth of the sensor in relation to the world's zero
    * degree mark. This angle is in absolute world coordinates.
    *
    * @return The azimuth of the sensor in absolute world coordinates.
    */
   public Angle getAzimuth()
   {
      return pose.getHeading();
   }

   public void slewToLookAt(WorldCoordinate lookAt)
   {
      this.lookAtGoal.setCoordinate(lookAt);
   }

   public Rectangle getViewFootPrint()
   {
      return viewRegion;
   }

   public WorldCoordinate getViewCenter()
   {
      return lookAtCur;
   }

   public void stepSimulation(WorldCoordinate sensorLocation)
   {
      pose.getCoordinate().setCoordinate(sensorLocation);
      pose.getHeading().normalize360();

      slew();
      updateViewRegion();
   }

   private void slew()
   {
      Angle desiredAngle = pose.getCoordinate().bearingTo(this.lookAtGoal);
      desiredAngle.normalize360();

      Angle lookDelta = new Angle(pose.getHeading());
      lookDelta.subtract(desiredAngle);

      if(Math.abs(lookDelta.asDegrees()) < (type.getMaxSlewRate().asDegreesPerFrame()))
      {
         //Prevent overshooting the look angle
         pose.getHeading().copy(desiredAngle);
      }
      else
      {
         Angle slew = new Angle();
         if(((lookDelta.asDegrees()+360) % 360) > 180)
         {
            //turn left
            slew.setAsDegrees(type.getMaxSlewRate().asDegreesPerFrame());
         }
         else
         {
            //turn right
            slew.setAsDegrees(-type.getMaxSlewRate().asDegreesPerFrame());
         }
         pose.getHeading().add(slew);
      }
      pose.getHeading().normalize360();
   }

   private void updateViewRegion()
   {
      Angle hdg = pose.getHeading();
      double halfFOVdeg = type.getFov().asDegrees() / 2;
      double leftAngleDeg = hdg.asDegrees() + halfFOVdeg;
      double rightAngleDeg = hdg.asDegrees() - halfFOVdeg;

      Angle leftAngle = new Angle();
      Angle rightAngle = new Angle();

      leftAngle.setAsDegrees(leftAngleDeg);
      rightAngle.setAsDegrees(rightAngleDeg);

      //Reset all view region locations to the sensor
      viewRegion.getTopLeft().setCoordinate(pose.getCoordinate());
      viewRegion.getTopRight().setCoordinate(pose.getCoordinate());
      viewRegion.getBottomLeft().setCoordinate(pose.getCoordinate());
      viewRegion.getBottomRight().setCoordinate(pose.getCoordinate());

      //Project out from the sensor position along the view heading
      viewRegion.getTopLeft().translate(hdg, type.getMaxRange());
      viewRegion.getTopRight().translate(hdg, type.getMaxRange());
      viewRegion.getBottomLeft().translate(hdg, type.getMinRange());
      viewRegion.getBottomRight().translate(hdg, type.getMinRange());


      final double maxRng = type.getMaxRange().asMeters();
      final double minRng = type.getMinRange().asMeters();
      final double midRng = ((maxRng - minRng) / 2.0) + minRng;
      final Distance midRngDist = new Distance();
      midRngDist.setAsMeters(midRng);
      lookAtCur.setCoordinate(pose.getCoordinate());
      lookAtCur.translate(hdg, midRngDist);
   }

   @Override
   public String toString()
   {
      return "Type: " + Integer.toString(type.getTypeID());
   }
}
