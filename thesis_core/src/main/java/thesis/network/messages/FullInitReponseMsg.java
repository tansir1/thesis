package thesis.network.messages;

import java.nio.ByteBuffer;
import java.util.List;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.TargetDump;
import thesis.core.world.Havens;
import thesis.core.world.World;

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

      //TODO finish encoding
      //uavs
      //entity types data
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      simState = new SimStateDump();
      entTypesCfg = new EntityTypeCfgs();

      decodeWorld(buf);
      decodeTargets(buf);

      //todo finish decoding
      //uavs
      //entity types data
   }

   @Override
   public short getEncodedSize()
   {
      short size = 0;
      size += getWorldByteSize();
      size += getTargetByteSize();

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
      for(int i=0; i<numHavens; ++i)
      {
         CellCoordinate cell = havens.getHavenByIndx(i);
         buf.putInt(cell.getColumn());
         buf.putInt(cell.getRow());
      }

      int numRoads = world.getRoadNetwork().getNumTraversable();
      buf.putInt(numRoads);
      for(CellCoordinate cell : world.getRoadNetwork().getTraversableCells())
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
      for(int i=0; i<numHavens; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getHavens().setHavenByIndx(i, row, col);
      }

      int numRoads = buf.getInt();
      world.getRoadNetwork().reset(rows, cols);
      for(int i=0; i<numRoads; ++i)
      {
         int col = buf.getInt();
         int row = buf.getInt();
         world.getRoadNetwork().setTraversable(row, col, true);
      }
   }

   private short getWorldByteSize()
   {
      World world = simState.getWorld();
      short size = 0;

      //world gis
      size += Integer.BYTES * 2;
      size += Double.BYTES * 2;

      //havens
      int numHavens = world.getHavens().getNumHavens();
      size += Integer.BYTES; //numHavens value
      size += (Integer.BYTES * 2) * numHavens;

      //road network
      int numRoads = world.getRoadNetwork().getNumTraversable();
      size += Integer.BYTES; //numRoads value
      size += (Integer.BYTES * 2) * numRoads;

      return size;
   }

   private void encodeTargets(ByteBuffer buf)
   {
      List<TargetDump> tgts = simState.getTargets();
      buf.putInt(tgts.size());
      for(TargetDump tgt : tgts)
      {
         buf.putInt(tgt.getId());
         buf.putInt(tgt.getType());
         buf.putDouble(tgt.getPose().getHeading());
         buf.putDouble(tgt.getPose().getNorth());
         buf.putDouble(tgt.getPose().getEast());
         if(tgt.isMobile())
         {
            buf.put((byte)1);
         }
         else
         {
            buf.put((byte)0);
         }
      }
   }

   private void decodeTargets(ByteBuffer buf)
   {
      List<TargetDump> tgts = simState.getTargets();

      int numTgts = buf.getInt();
      for(int i=0; i<numTgts; ++i)
      {
         int id = buf.getInt();
         int type = buf.getInt();
         double hdg = buf.getDouble();
         double north = buf.getDouble();
         double east = buf.getDouble();
         byte mobileNum = buf.get();

         boolean mobile = false;
         if(mobileNum == 1)
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

   private short getTargetByteSize()
   {
      List<TargetDump> tgts = simState.getTargets();
      short size = 0;

      size += Integer.BYTES;//numTgts

      int numTgts = tgts.size();
      size += (Integer.BYTES * 2) * numTgts;//id and type
      size += (Double.BYTES * 3) * numTgts;//hdg, north, east
      size += numTgts; //1 byte for all the mobile flags

      return size;
   }
}
