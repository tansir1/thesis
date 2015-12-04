package thesis.core.entities.uav.sensors;

import thesis.core.common.Angle;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class Sensor
{
   private SensorType type;
   private WorldPose pose;
   private WorldCoordinate lookAt;

   public Sensor(SensorType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      pose = new WorldPose();
      lookAt = new WorldCoordinate();
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
      this.lookAt.setCoordinate(lookAt);
   }

   public void stepSimulation(WorldCoordinate sensorLocation)
   {
      pose.getCoordinate().setCoordinate(sensorLocation);
      pose.getHeading().normalize360();

      Angle desiredAngle = pose.getCoordinate().bearingTo(this.lookAt);
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

   @Override
   public String toString()
   {
      return "Type: " + Integer.toString(type.getTypeID());
   }
}
