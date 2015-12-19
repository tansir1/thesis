package thesis.core.entities.sensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thesis.core.common.WorldCoordinate;

public class SensorGroup
{
   private List<Sensor> sensors;

   public SensorGroup()
   {
      sensors = new ArrayList<Sensor>();
   }

   public Sensor addSensor(SensorType type)
   {
      Sensor sensor = new Sensor(type);
      sensors.add(sensor);
      return sensor;
   }

   public List<Sensor> getSensors()
   {
      return Collections.unmodifiableList(sensors);
   }

   public void stepSimulation(WorldCoordinate hostUAVLocation)
   {
      for(Sensor s : sensors)
      {
         s.stepSimulation(hostUAVLocation);
      }
   }

   public void stareAtAll(WorldCoordinate starePoint)
   {
      for(Sensor s : sensors)
      {
         s.slewToLookAt(starePoint);
      }
   }
}
