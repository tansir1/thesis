package thesis.network.messages;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import thesis.core.common.Rectangle;
import thesis.core.common.WorldPose;
import thesis.core.statedump.SensorDump;
import thesis.core.statedump.SimStateUpdateDump;

@Deprecated
public class SimStateUpdateMsg extends InfrastructureMsg
{
   private SimStateUpdateDump updateDump;

   public SimStateUpdateMsg()
   {
      super(InfrastructureMsgType.SimStateUpdate);
   }

   public void setUpdateDump(SimStateUpdateDump dump)
   {
      this.updateDump = dump;
   }

   public SimStateUpdateDump getUpdateDump()
   {
      return updateDump;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      Map<Integer, WorldPose> uavPoses = updateDump.getUAVPoses();
      Map<Integer, WorldPose> tgtPoses = updateDump.getTargets();
      Map<Integer, Map<Integer, SensorDump>> sensorsMap = updateDump.getSensors();

      buf.putInt(uavPoses.size());
      for (Entry<Integer, WorldPose> entry : uavPoses.entrySet())
      {
         int uavID = entry.getKey();
         buf.putInt(uavID);
         buf.putDouble(entry.getValue().getHeading());
         buf.putDouble(entry.getValue().getNorth());
         buf.putDouble(entry.getValue().getEast());
      }

      buf.putInt(tgtPoses.size());
      for (Entry<Integer, WorldPose> entry : tgtPoses.entrySet())
      {
         buf.putInt(entry.getKey());
         buf.putDouble(entry.getValue().getHeading());
         buf.putDouble(entry.getValue().getNorth());
         buf.putDouble(entry.getValue().getEast());
      }

      for (Entry<Integer, Map<Integer, SensorDump>> uav : sensorsMap.entrySet())
      {
         buf.putInt(uav.getKey());// uav id
         buf.putInt(uav.getValue().size());//num sensors
         for (Entry<Integer, SensorDump> sensor : uav.getValue().entrySet())
         {
            SensorDump snsrDump = sensor.getValue();
            buf.putInt(sensor.getKey());// sensor id

            buf.putDouble(snsrDump.getAzimuth());
            buf.putDouble(snsrDump.getLookAtGoal().getNorth());
            buf.putDouble(snsrDump.getLookAtGoal().getEast());
            buf.putDouble(snsrDump.getViewCenter().getNorth());
            buf.putDouble(snsrDump.getViewCenter().getEast());

            Rectangle rect = snsrDump.getViewFootPrint();
            buf.putDouble(rect.getBottomLeft().getNorth());
            buf.putDouble(rect.getBottomLeft().getEast());
            buf.putDouble(rect.getBottomRight().getNorth());
            buf.putDouble(rect.getBottomRight().getEast());
            buf.putDouble(rect.getTopLeft().getNorth());
            buf.putDouble(rect.getTopLeft().getEast());
            buf.putDouble(rect.getTopRight().getNorth());
            buf.putDouble(rect.getTopRight().getEast());
         }
      }

   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      updateDump = new SimStateUpdateDump();
      WorldPose tempPose = new WorldPose();

      int numUAVs = buf.getInt();
      for (int i = 0; i < numUAVs; ++i)
      {
         int uavID = buf.getInt();
         double hdg = buf.getDouble();
         double north = buf.getDouble();
         double east = buf.getDouble();
         tempPose.setHeading(hdg);
         tempPose.getCoordinate().setCoordinate(north, east);
         updateDump.setUAVPose(uavID, tempPose);
      }

      int numTgts = buf.getInt();
      for (int i = 0; i < numTgts; ++i)
      {
         int tgtID = buf.getInt();
         double hdg = buf.getDouble();
         double north = buf.getDouble();
         double east = buf.getDouble();
         tempPose.setHeading(hdg);
         tempPose.getCoordinate().setCoordinate(north, east);
         updateDump.setTargetPose(tgtID, tempPose);
      }

      for(int i=0; i<numUAVs; ++i)
      {
         int uavID = buf.getInt();
         int numSnsrs = buf.getInt();
         for(int j=0;j<numSnsrs;++j)
         {
            int snsrID = buf.getInt();
            double snsrHdg = buf.getDouble();
            double lookAtGoalN = buf.getDouble();
            double lookAtGoalE = buf.getDouble();
            double lookAtCurN = buf.getDouble();
            double lookAtCurE = buf.getDouble();

            double blN = buf.getDouble();
            double blE = buf.getDouble();
            double brN = buf.getDouble();
            double brE = buf.getDouble();
            double tlN = buf.getDouble();
            double tlE = buf.getDouble();
            double trN = buf.getDouble();
            double trE = buf.getDouble();


            SensorDump snsrDump = new SensorDump(snsrID);
            snsrDump.setAzimuth(snsrHdg);
            snsrDump.getLookAtGoal().setCoordinate(lookAtGoalN, lookAtGoalE);
            snsrDump.getViewCenter().setCoordinate(lookAtCurN, lookAtCurE);
            snsrDump.getViewFootPrint().getBottomLeft().setCoordinate(blN, blE);
            snsrDump.getViewFootPrint().getBottomRight().setCoordinate(brN, brE);
            snsrDump.getViewFootPrint().getTopLeft().setCoordinate(tlN, tlE);
            snsrDump.getViewFootPrint().getTopRight().setCoordinate(trN, trE);

            updateDump.setSensorUpdate(uavID, snsrDump);
         }
      }
   }

   @Override
   public long getEncodedSize()
   {
      int numUAVs = updateDump.getNumUAVPoses();
      int numTgts = updateDump.getNumTargets();

      long size =0;
      size += Integer.BYTES;//numUAVs
      size += Integer.BYTES * numUAVs;//uav ID
      size += Double.BYTES * numUAVs * 3;//hdg, north, east

      size += Integer.BYTES;//numTgts
      size += Integer.BYTES * numTgts;//tgt ID
      size += Double.BYTES * numTgts * 3;//hdg, north, east

      //Compute sensor byte sizes
      for(Integer id : updateDump.getUAVPoses().keySet())
      {
         size += Integer.BYTES * 2;//uav id, num sensors

         int numSnsrs = updateDump.getNumSensors(id);
         size += Integer.BYTES * numSnsrs; //id
         size += Double.BYTES * numSnsrs * 5;//lookatgoal and cur N/E, heading
         size += Double.BYTES * numSnsrs * 8;//N/E for each corner of view footprint
      }

      return size;
   }

}
