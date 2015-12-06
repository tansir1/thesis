package thesis.core.entities.uav.sensors;

import thesis.core.common.Angle;
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
    * @return The azimuth of the sensor in absolute world coordinates (degrees).
    */
   public double getAzimuth()
   {
      return pose.getHeading();
   }

   /**
    * Set the current azimuth of the sensor in relation to the world's zero
    * degree mark. This angle is in absolute world coordinates.
    *
    * @param azimuth The azimuth of the sensor in absolute world coordinates (degrees).
    */
   public void setAzimuth(double azimuth)
   {
      pose.setHeading(azimuth);
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

      slew();
      updateViewRegion();
   }

   private void slew()
   {
      double desiredAngle = Angle.normalize360(pose.getCoordinate().bearingTo(this.lookAtGoal));

      double lookDelta = Angle.normalize360(pose.getHeading() - desiredAngle);

      final double degreesPerFrame = type.getMaxSlewFrameRate();
      //If the lookDelta < one frame's worth of slewing
      if(Math.abs(lookDelta) < degreesPerFrame)
      {
         //Prevent overshooting the look angle
         pose.setHeading(desiredAngle);
      }
      else
      {
         double slew = 0;
         if(((lookDelta+360) % 360) > 180)
         {
            //turn left
            slew = degreesPerFrame;
         }
         else
         {
            //turn right
            slew = -degreesPerFrame;
         }
         pose.setHeading(pose.getHeading() + slew);
      }
   }

   private void updateViewRegion()
   {
      final double hdg = pose.getHeading();
      final double halfFOVdeg = type.getFov() / 2;
      final double leftAngleDeg = hdg + halfFOVdeg;
      final double rightAngleDeg = hdg - halfFOVdeg;

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
      lookAtCur.translatePolar(hdg, midRngDist);

      //Reset all view region locations to the sensor
      viewRegion.getTopLeft().setCoordinate(pose.getCoordinate());
      viewRegion.getTopRight().setCoordinate(pose.getCoordinate());
      viewRegion.getBottomLeft().setCoordinate(pose.getCoordinate());
      viewRegion.getBottomRight().setCoordinate(pose.getCoordinate());

      //Project out from the sensor position along the view heading
      viewRegion.getTopLeft().translatePolar(leftAngleDeg, fovFar);
      viewRegion.getTopRight().translatePolar(rightAngleDeg, fovFar);
      viewRegion.getBottomLeft().translatePolar(leftAngleDeg, fovNear);
      viewRegion.getBottomRight().translatePolar(rightAngleDeg, fovNear);
   }

   @Override
   public String toString()
   {
      return "Type: " + Integer.toString(type.getTypeID());
   }
}
