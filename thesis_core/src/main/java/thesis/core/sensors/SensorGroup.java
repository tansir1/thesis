package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.WorldCoordinate;

public class SensorGroup
{
   private List<Sensor> sensors;

   public SensorGroup()
   {
      sensors = new ArrayList<Sensor>();
   }

   public void addSensor(Sensor sensor)
   {
      sensors.add(sensor);
   }

   public List<Sensor> getSensors()
   {
      return sensors;
   }

   public List<SensorDetections> stepSimulation(WorldCoordinate hostUAVLocation)
   {
      List<SensorDetections> detections = new ArrayList<SensorDetections>();

      for (Sensor s : sensors)
      {
         detections.add(s.stepSimulation(hostUAVLocation));
      }

      return detections;
   }

   public boolean isInSensorRange(WorldCoordinate coord)
   {
      boolean inRng = false;
      for(Sensor s : sensors)
      {
         inRng = s.isInRange(coord);
         if(inRng)
         {
            break;
         }
      }
      return inRng;
   }

   public boolean isInView(WorldCoordinate coord)
   {
      boolean inView = false;
      for(Sensor s : sensors)
      {
         inView = s.isInView(coord);
         if(inView)
         {
            break;
         }
      }
      return inView;
   }

   public void stareAtAll(WorldCoordinate starePoint)
   {
      for (Sensor s : sensors)
      {
         s.slewToLookAt(starePoint);
      }
   }
}
