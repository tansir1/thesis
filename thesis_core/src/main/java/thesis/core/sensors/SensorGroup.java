package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.WorldCoordinate;
import thesis.core.targets.TargetMgr;

public class SensorGroup
{
   private List<Sensor> sensors;
   private TargetMgr tgtMgr;

   public SensorGroup(TargetMgr tgtMgr)
   {
      sensors = new ArrayList<Sensor>();
      this.tgtMgr = tgtMgr;
   }

   public Sensor addSensor(SensorType type)
   {
      Sensor sensor = new Sensor(type, tgtMgr);
      sensors.add(sensor);
      return sensor;
   }

   public List<Sensor> getSensors()
   {
      return sensors;
   }

   public List<SensorDetections> stepSimulation(WorldCoordinate hostUAVLocation)
   {
      List<SensorDetections> detections = new ArrayList<SensorDetections>();

      for(Sensor s : sensors)
      {
         detections.add(s.stepSimulation(hostUAVLocation));
      }

      return detections;
   }

   public void stareAtAll(WorldCoordinate starePoint)
   {
      for(Sensor s : sensors)
      {
         s.slewToLookAt(starePoint);
      }
   }
}
