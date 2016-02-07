package thesis.core.statedump;

import java.util.HashMap;
import java.util.Map;

import thesis.core.common.WorldPose;

public class SimStateUpdateDump
{
   private Map<Integer, WorldPose> tgtUpdates;
   private Map<Integer, WorldPose> uavPoseUpdates;
   private Map<Integer, Map<Integer, SensorDump>> uavSnsrs;

   public SimStateUpdateDump()
   {
      tgtUpdates = new HashMap<Integer, WorldPose>();
      uavPoseUpdates = new HashMap<Integer, WorldPose>();
      uavSnsrs = new HashMap<Integer, Map<Integer, SensorDump>>();
   }

   public void setTargetPose(int id, WorldPose pose)
   {
      WorldPose tgtPose = tgtUpdates.get(id);
      if(tgtPose == null)
      {
         tgtPose = new WorldPose();
         tgtUpdates.put(id, tgtPose);
      }
      tgtPose.copy(pose);
   }

   public WorldPose getTargetPose(int id)
   {
      return tgtUpdates.get(id);
   }

   public void setUAVPose(int id, WorldPose pose)
   {
      WorldPose uavPose = uavPoseUpdates.get(id);
      if(uavPose == null)
      {
         uavPose = new WorldPose();
         uavPoseUpdates.put(id, uavPose);
      }
      uavPose.copy(pose);
   }

   public WorldPose getUAVPose(int id)
   {
      return uavPoseUpdates.get(id);
   }

   public void setSensorUpdate(int uavID, SensorDump update)
   {
      Map<Integer, SensorDump> snsrs = uavSnsrs.get(uavID);
      if (snsrs == null)
      {
         snsrs = new HashMap<Integer, SensorDump>();
         uavSnsrs.put(uavID, snsrs);
      }

      SensorDump oldVal = snsrs.get(update.getID());
      if(oldVal == null)
      {
         oldVal = new SensorDump(update.getID());
         snsrs.put(update.getID(), oldVal);
      }
      oldVal.dumpUpdate(update);
   }

   public SensorDump getSensorUpdate(int uavID, int sensorID)
   {
      SensorDump data = null;

      Map<Integer, SensorDump> snsrs = uavSnsrs.get(uavID);
      if (snsrs != null)
      {
         data = snsrs.get(sensorID);
      }
      return data;
   }

   public Map<Integer, WorldPose> getTargets()
   {
      return tgtUpdates;
   }

   public Map<Integer, WorldPose> getUAVPoses()
   {
      return uavPoseUpdates;
   }

   public Map<Integer, Map<Integer, SensorDump>> getSensors()
   {
      return uavSnsrs;
   }

   public int getNumTargets()
   {
      return tgtUpdates.size();
   }

   public int getNumUAVPoses()
   {
      return uavPoseUpdates.size();
   }

   public int getNumSensors(int uavID)
   {
      return uavSnsrs.get(uavID).size();
   }
}
