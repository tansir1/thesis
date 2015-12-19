package thesis.core.entities.sensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thesis.core.common.WorldCoordinate;
import thesis.core.entities.Target;
import thesis.core.entities.TargetMgr;

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
      return Collections.unmodifiableList(sensors);
   }

   public List<Target> stepSimulation(WorldCoordinate hostUAVLocation)
   {
      List<Target> detections = new ArrayList<Target>();

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
