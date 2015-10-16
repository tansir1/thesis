package thesis.core.entities;

import thesis.core.common.Angle;

public class Sensor
{
   private SensorType type;
   private Angle azimuth;

   public Sensor(SensorType type)
   {
      if (type == null)
      {
         throw new NullPointerException("type cannot be null.");
      }

      this.type = type;

      azimuth = new Angle();
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
      return azimuth;
   }

   // TODO Add a PID or something to control slewing to angle after timesteps
   // are defined
   // TODO Add SlewToAngle function to set new target angle

   public String toString()
   {
      return "Type: " + Integer.toString(type.getTypeID());
   }
}
