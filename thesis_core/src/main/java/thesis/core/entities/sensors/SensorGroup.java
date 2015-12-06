package thesis.core.entities.uav.sensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thesis.core.common.WorldCoordinate;

public class SensorGroup
{
   private List<Sensor> sensors;
   private TargetMgr tgtMgr;

   public SensorGroup(TargetMgr tgtMgr)
   {
      sensors = new ArrayList<Sensor>();
   }

   public Sensor addSensor(SensorType type)
   {
      Sensor sensor = new Sensor(type, tgtMgr);
      sensors.add(sensor);
      return sensor;
   }

   public List<Sensor> getSensors()
   {
      return Collections.unmodifiableList(sensors);
   }

   public List<TargetBelief> stepSimulation(WorldCoordinate hostUAVLocation)
   {
      List<TargetBelief> detections = new ArrayList<TargetBelief>();

      for(Sensor s : sensors)
      {
         detections.addAll(s.stepSimulation(hostUAVLocation));
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
