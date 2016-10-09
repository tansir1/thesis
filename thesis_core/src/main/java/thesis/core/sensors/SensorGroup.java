package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;

import thesis.core.belief.WorldBelief;
import thesis.core.common.Trapezoid;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.world.WorldGIS;

public class SensorGroup
{
   private List<Sensor> sensors;
   private SensorScanLogic scanner;
   private WorldGIS gis;
   private double maxSensingDistance;

   public SensorGroup(SensorScanLogic scanner, WorldGIS gis)
   {
      this.scanner = scanner;
      this.gis = gis;
      sensors = new ArrayList<Sensor>();
      maxSensingDistance = -1;
   }

   public void addSensor(Sensor sensor)
   {
      sensors.add(sensor);
      maxSensingDistance = Math.max(maxSensingDistance, sensor.getMaxRange());
   }

   public List<Sensor> getSensors()
   {
      return sensors;
   }

   public void stepSimulation(WorldPose hostUAVPose, WorldBelief belief, long simTime)
   {
      for (Sensor s : sensors)
      {
         s.stepSimulation(hostUAVPose);
         Trapezoid fov = s.getViewFootPrint();
         if (!s.isFocusedScanning())
         {
            scanner.simulateScan(s.getType(), s.getAzimuth(), belief, gis.getCellsInRectangle(fov), simTime);
            // TODO Implement focused scan detection logic...if any
         }
      }
   }

   public void stareAtAll(WorldCoordinate starePoint)
   {
      for (Sensor s : sensors)
      {
         s.slewToLookAt(starePoint);
      }
   }

   public boolean isFocusedScanning()
   {
      boolean focused = false;
      for (Sensor s : sensors)
      {
         if (s.isFocusedScanning())
         {
            focused = true;
            break;
         }
      }
      return focused;
   }

   public void setFocusedScanning(boolean focused)
   {
      for (Sensor s : sensors)
      {
         s.setFocusedScanning(focused);
      }
   }

   /**
    * Get the maximum sensing distance.
    *
    * @return Max sensing distance in meters.
    */
   public double getMaxSensorRange()
   {
      return maxSensingDistance;
   }

   public double getBestScanProb(int tgtType)
   {
      double bestProb = -1;
      SensorProbs sp = scanner.getSensorProbabilities();
      for (Sensor s : sensors)
      {
         bestProb = Math.max(bestProb, sp.getSensorConfirmProb(s.getType(), tgtType));
      }
      return bestProb;
   }
}
