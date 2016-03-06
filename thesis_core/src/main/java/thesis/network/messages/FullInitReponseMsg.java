package thesis.network.messages;

import java.nio.ByteBuffer;
import java.util.List;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.Rectangle;
import thesis.core.common.WorldPose;
import thesis.core.statedump.SensorDump;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.TargetDump;
import thesis.core.statedump.UAVDump;
import thesis.core.world.Havens;
import thesis.core.world.World;

@Deprecated
public class FullInitReponseMsg extends InfrastructureMsg
{
   private SimStateDump simState;
   private EntityTypeCfgs entTypesCfg;

   public FullInitReponseMsg()
   {
      super(InfrastructureMsgType.FullInitReponse);
   }

   public void setSimStateDump(SimStateDump simState)
   {
      this.simState = simState;
   }

   public void setEntityTypeConfigs(EntityTypeCfgs typeCfgs)
   {
      this.entTypesCfg = typeCfgs;
   }

   public SimStateDump getSimStateDump()
   {
      return simState;
   }

   public EntityTypeCfgs getEntityTypeConfigs()
   {
      return entTypesCfg;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      encodeWorld(buf);
      encodeTargets(buf);
      encodeUAVs(buf);
      // TODO finish encoding
      // uavs
      // entity types data
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      simState = new SimStateDump();
      entTypesCfg = new EntityTypeCfgs();

      decodeWorld(buf);
      decodeTargets(buf);
      decodeUAVs(buf);
      // todo finish decoding
      // uavs
      // entity types data
   }

   @Override
   public long getEncodedSize()
   {
      long size = 0;
      size += getWorldByteSize();
      size += getTargetByteSize();
      size += getUAVByteSize();
      return size;
   }

   private void encodeWorld(ByteBuffer buf)
   {
      World world = simState.getWorld();
      buf.putInt(world.getWorldGIS().getColumnCount());
      buf.putInt(world.getWorldGIS().getRowCount());
      buf.putDouble(world.getWorldGIS().getHeight());
      buf.putDouble(world.getWorldGIS().getWidth());

      Havens havens = world.getHavens();
      int numHavens = havens.getNumHavens();

      buf.putInt(numHavens);
      for (int i = 0; i < numHavens; ++i)
      {
         CellCoordinate cell = havens.getHavenByIndx(i);
         buf.putInt(cell.getColumn());
         buf.putInt(cell.getRow());
      }

      int numRoads = world.getRoadNetwork().getNumTraversable();
      buf.putInt(numRoads);
      for (CellCoordinate cell : world.getRoadNetwork().getTraversableCells())
      {
         buf.putInt(cell.getColumn());
         buf.putInt(cell.getRow());
      }
   }

   private void decodeWorld(ByteBuffer buf)
   {
      World world = simState.getWorld();
      int cols = buf.getInt();
      int rows = buf.getInt();
      double height = buf.getDouble();
      double width = buf.getDouble();
      world.getWorldGIS().reset(width, height, rows, cols);

      int numHavens = buf.getInt();
      world.getHavens().reset(numHavens);
      for (int i = 0; i < numHavens; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getHavens().setHavenByIndx(i, row, col);
      }

      int numRoads = buf.getInt();
      world.getRoadNetwork().reset(rows, cols);
      for (int i = 0; i < numRoads; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getRoadNetwork().setTraversable(row, col, true);
      }
   }

   private long getWorldByteSize()
   {
      World world = simState.getWorld();
      long size = 0;

      // world gis
      size += Integer.BYTES * 2;
      size += Double.BYTES * 2;

      // havens
      int numHavens = world.getHavens().getNumHavens();
      size += Integer.BYTES; // numHavens value
      size += (Integer.BYTES * 2) * numHavens;

      // road network
      int numRoads = world.getRoadNetwork().getNumTraversable();
      size += Integer.BYTES; // numRoads value
      size += (Integer.BYTES * 2) * numRoads;

      return size;
   }

   private void encodeTargets(ByteBuffer buf)
   {
      List<TargetDump> tgts = simState.getTargets();
      buf.putInt(tgts.size());
      for (TargetDump tgt : tgts)
      {
         buf.putInt(tgt.getId());
         buf.putInt(tgt.getType());
         buf.putDouble(tgt.getPose().getHeading());
         buf.putDouble(tgt.getPose().getNorth());
         buf.putDouble(tgt.getPose().getEast());
         if (tgt.isMobile())
         {
            buf.put((byte) 1);
         }
         else
         {
            buf.put((byte) 0);
         }
      }
   }

   private void decodeTargets(ByteBuffer buf)
   {
      List<TargetDump> tgts = simState.getTargets();

      int numTgts = buf.getInt();
      for (int i = 0; i < numTgts; ++i)
      {
         int id = buf.getInt();
         int type = buf.getInt();
         double hdg = buf.getDouble();
         double north = buf.getDouble();
         double east = buf.getDouble();
         byte mobileNum = buf.get();

         boolean mobile = false;
         if (mobileNum == 1)
         {
            mobile = true;
         }
         WorldPose pose = new WorldPose();
         pose.setHeading(hdg);
         pose.getCoordinate().setCoordinate(north, east);
         TargetDump tgtDump = new TargetDump(id, type, mobile, pose);

         tgts.add(tgtDump);
      }
   }

   private long getTargetByteSize()
   {
      List<TargetDump> tgts = simState.getTargets();
      long size = 0;

      size += Integer.BYTES;// numTgts

      int numTgts = tgts.size();
      size += (Integer.BYTES * 2) * numTgts;// id and type
      size += (Double.BYTES * 3) * numTgts;// hdg, north, east
      size += numTgts; // 1 byte for all the mobile flags

      return size;
   }

   private void encodeUAVs(ByteBuffer buf)
   {
      List<UAVDump> uavs = simState.getUAVs();
      buf.putInt(uavs.size());
      for (UAVDump uav : uavs)
      {
         buf.putInt(uav.getID());
         buf.putInt(uav.getType());
         buf.putDouble(uav.getPose().getHeading());
         buf.putDouble(uav.getPose().getNorth());
         buf.putDouble(uav.getPose().getEast());

         List<SensorDump> snsrs = uav.getSensors();
         buf.putInt(snsrs.size());
         for (SensorDump snsr : snsrs)
         {
            buf.putInt(snsr.getID());
            buf.putInt(snsr.getType());
            buf.putDouble(snsr.getAzimuth());
            buf.putDouble(snsr.getLookAtGoal().getNorth());
            buf.putDouble(snsr.getLookAtGoal().getEast());
            buf.putDouble(snsr.getViewCenter().getNorth());
            buf.putDouble(snsr.getViewCenter().getEast());

            Rectangle rect = snsr.getViewFootPrint();
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

   private void decodeUAVs(ByteBuffer buf)
   {
      List<UAVDump> uavs = simState.getUAVs();

      int numUAVs = buf.getInt();
      for (int i = 0; i < numUAVs; ++i)
      {
         int id = buf.getInt();
         int type = buf.getInt();
         double hdg = buf.getDouble();
         double north = buf.getDouble();
         double east = buf.getDouble();

         WorldPose pose = new WorldPose();
         pose.setHeading(hdg);
         pose.getCoordinate().setCoordinate(north, east);
         UAVDump uavDump = new UAVDump(type, id, pose);

         uavs.add(uavDump);

         int numSnsrs = buf.getInt();
         for (int j = 0; j < numSnsrs; ++j)
         {
            int snsrID = buf.getInt();
            int snsrType = buf.getInt();
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

            SensorDump snsrDump = new SensorDump(snsrType, snsrID);
            snsrDump.setAzimuth(snsrHdg);
            snsrDump.getLookAtGoal().setCoordinate(lookAtGoalN, lookAtGoalE);
            snsrDump.getViewCenter().setCoordinate(lookAtCurN, lookAtCurE);
            snsrDump.getViewFootPrint().getBottomLeft().setCoordinate(blN, blE);
            snsrDump.getViewFootPrint().getBottomRight().setCoordinate(brN, brE);
            snsrDump.getViewFootPrint().getTopLeft().setCoordinate(tlN, tlE);
            snsrDump.getViewFootPrint().getTopRight().setCoordinate(trN, trE);
            uavDump.getSensors().add(snsrDump);
         }
      }
   }

   private long getUAVByteSize()
   {
      List<UAVDump> uavs = simState.getUAVs();
      long size = 0;

      size += Integer.BYTES;// numUAVs

      int numUAVs = uavs.size();
      size += (Integer.BYTES * 2) * numUAVs;// id and type
      size += (Double.BYTES * 3) * numUAVs;// hdg, north, east

      for(UAVDump uav : uavs)
      {
         size += Integer.BYTES; //sensor list count

         int numSnsrs = uav.getSensors().size();
         size += Integer.BYTES * numSnsrs * 2; //type and id
         size += Double.BYTES * numSnsrs * 5;//lookatgoal and cur N/E, heading
         size += Double.BYTES * numSnsrs * 8;//N/E for each corner of view footprint
      }

      return size;
   }
}
