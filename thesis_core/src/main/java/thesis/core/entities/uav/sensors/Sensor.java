package thesis.core.entities.uav.sensors;

import thesis.core.common.Angle;
import thesis.core.common.Rectangle;
import thesis.core.common.SimTime;
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

      final double degreesPerFrame = type.getMaxSlewRate() * SimTime.SIM_STEP_RATE_S;
      //If the lookDelta < one frame's worth of slewing
      if(Math.abs(lookDelta.asDegrees()) < degreesPerFrame)
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
            slew.setAsDegrees(degreesPerFrame);
         }
         else
         {
            //turn right
            slew.setAsDegrees(-degreesPerFrame);
         }
         pose.getHeading().add(slew);
      }
      pose.getHeading().normalize360();
   }

   private void updateViewRegion()
   {
      final Angle hdg = pose.getHeading();
      final double halfFOVdeg = type.getFov().asDegrees() / 2;
      final double leftAngleDeg = hdg.asDegrees() + halfFOVdeg;
      final double rightAngleDeg = hdg.asDegrees() - halfFOVdeg;

      final double maxRng = type.getMaxRange();
      final double minRng = type.getMinRange();
      final double frustrumHeight = maxRng - minRng;

      final double distToStarePt = pose.getCoordinate().distanceTo(lookAtGoal);
      double midRngDist = 0;
      double fovFar = 0;
      double fovNear = 0;
      if(distToStarePt < (maxRng - frustrumHeight))
      {
         double distToStareM = distToStarePt;

         fovFar = distToStareM + (frustrumHeight / 2);
         fovNear = distToStareM - (frustrumHeight / 2);
         midRngDist = (frustrumHeight / 2.0) + fovNear;
      }
      else
      {
         fovFar = maxRng;
         fovNear = minRng;
         midRngDist = (frustrumHeight / 2.0) + minRng;
      }

      //Update viewpoint center position
      lookAtCur.setCoordinate(pose.getCoordinate());
      lookAtCur.translate(hdg, midRngDist);

      //Update frustrum boundary angles
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
      viewRegion.getTopLeft().translate(leftAngle, fovFar);
      viewRegion.getTopRight().translate(rightAngle, fovFar);
      viewRegion.getBottomLeft().translate(leftAngle, fovNear);
      viewRegion.getBottomRight().translate(rightAngle, fovNear);
   }

   @Override
   public String toString()
   {
      return "Type: " + Integer.toString(type.getTypeID());
   }
}
