package thesis.core.entities.sensors;

import java.util.List;

import thesis.core.targets.Target;

public class SensorDetections
{
   private int sensorType;
   private List<Target> tgtsInFOV;

   public SensorDetections(int sensorType, List<Target> tgtsInFOV)
   {
      this.sensorType = sensorType;
      this.tgtsInFOV = tgtsInFOV;
   }

   public int getSensorType()
   {
      return sensorType;
   }

   public List<Target> getTgtsInFOV()
   {
      return tgtsInFOV;
   }
}
