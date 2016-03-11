package thesis.core.sensors;

import thesis.core.common.Angle;
import thesis.core.common.Trapezoid;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.targets.TargetMgr;

public class Sensor
{
   /**
    * The minimum range of the sensor in meters.
    */
   private final double MIN_RNG;
   /**
    * The max range of the sensor in meters.
    */
   private final double MAX_RNG;
   /**
    * The FOV of the sensor in degrees.
    */
   private final double FOV;

   /**
    * Max speed that the sensor can slew in degrees/frame.
    */
   private final double MAX_SLEW_FRAME_RATE;

   private final TargetMgr tgtMgr;

   private final int type;
   private final WorldPose pose;
   private final WorldCoordinate lookAtGoal;
   private final WorldCoordinate lookAtCur;
   private final Trapezoid viewRegion;

   private int id;

   public Sensor(int type, int id, SensorTypeConfigs cfgs, TargetMgr tgtMgr)
   {
      if (cfgs == null)
      {
         throw new NullPointerException("Type configs cannot be null.");
      }

      if (tgtMgr == null)
      {
         throw new NullPointerException("Target manager cannot be null.");
      }

      this.type = type;
      this.id = id;
      this.tgtMgr = tgtMgr;

      pose = new WorldPose();
      lookAtGoal = new WorldCoordinate();
      lookAtCur = new WorldCoordinate();
      viewRegion = new Trapezoid();

      MIN_RNG = cfgs.getMinRange(type);
      MAX_RNG = cfgs.getMaxRange(type);
      FOV = cfgs.getFOV(type);
      MAX_SLEW_FRAME_RATE = cfgs.getMaxSlewFrameRate(type);
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

   public SensorDetections stepSimulation(WorldCoordinate sensorLocation)
   {
      pose.getCoordinate().setCoordinate(sensorLocation);

      slew();
      updateViewRegion();

      return new SensorDetections(type, tgtMgr.getTargetsInRegion(viewRegion));
   }

   public WorldPose getPose()
   {
      return pose;
   }

   private void slew()
   {
      double desiredAngle = Angle.normalize360(pose.getCoordinate().bearingTo(this.lookAtGoal));

      double lookDelta = Angle.normalize360(pose.getHeading() - desiredAngle);

      //If the lookDelta < one frame's worth of slewing
      if(Math.abs(lookDelta) < MAX_SLEW_FRAME_RATE)
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
            slew = MAX_SLEW_FRAME_RATE;
         }
         else
         {
            //turn right
            slew = -MAX_SLEW_FRAME_RATE;
         }
         pose.setHeading(pose.getHeading() + slew);
      }
   }

   private void updateViewRegion()
   {
      final double hdg = pose.getHeading();
      final double halfFOVdeg = FOV / 2;
      final double leftAngleDeg = hdg + halfFOVdeg;
      final double rightAngleDeg = hdg - halfFOVdeg;

      final double frustrumHeight = MAX_RNG - MIN_RNG;

      final double distToStarePt = pose.getCoordinate().distanceTo(lookAtGoal);
      double midRngDist = 0;
      double fovFar = 0;
      double fovNear = 0;
      if(distToStarePt < (MAX_RNG - frustrumHeight))
      {
         double distToStareM = distToStarePt;

         fovFar = distToStareM + (frustrumHeight / 2);
         fovNear = distToStareM - (frustrumHeight / 2);
         midRngDist = (frustrumHeight / 2.0) + fovNear;
      }
      else
      {
         fovFar = MAX_RNG;
         fovNear = MIN_RNG;
         midRngDist = (frustrumHeight / 2.0) + MIN_RNG;
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
      StringBuilder sb = new StringBuilder();
      sb.append("Type: ");
      sb.append(type);
      sb.append(" Goal: ");
      sb.append(lookAtGoal);
      sb.append(" Cur: ");
      sb.append(lookAtCur);
      sb.append(" - Hdg: ");
      sb.append(String.format("%.2f", pose.getHeading()));
      return sb.toString();
   }
}
