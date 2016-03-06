package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;

import thesis.core.belief.WorldBelief;
import thesis.core.common.Rectangle;
import thesis.core.common.WorldCoordinate;
import thesis.core.world.WorldGIS;

public class SensorGroup
{
   private List<Sensor> sensors;
   private SensorScanLogic scanner;
   private WorldGIS gis;

   public SensorGroup(SensorScanLogic scanner, WorldGIS gis)
   {
      this.scanner = scanner;
      this.gis = gis;
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

   public void stepSimulation(WorldCoordinate hostUAVLocation, WorldBelief belief, long simTime)
   {
      for(Sensor s : sensors)
      {
         s.stepSimulation(hostUAVLocation);
         Rectangle fov = s.getViewFootPrint();
         scanner.simulateScan(s.getType(), s.getAzimuth(), belief, gis.getCellsInRectangle(fov), simTime);
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
