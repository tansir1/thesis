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
   
   //TODO Add a PID or something to control slewing to angle after timesteps are defined
   //TODO Add SlewToAngle function to set new target angle
}
