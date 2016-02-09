package thesis.network.messages;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import thesis.core.common.WorldPose;
import thesis.core.statedump.TargetDump;

public class TargetsInitMsg extends InfrastructureMsg
{
   private List<TargetDump> tgts;

   public TargetsInitMsg()
   {
      super(InfrastructureMsgType.TargetsInit);
   }

   public void setTargets(List<TargetDump> tgts)
   {
      this.tgts = tgts;
   }

   public List<TargetDump> getTargets()
   {
      return tgts;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
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

   @Override
   public void decodeData(ByteBuffer buf)
   {
      tgts = new ArrayList<TargetDump>();

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

   @Override
   public long getEncodedSize()
   {
      long size = 0;

      size += Integer.BYTES;//numTgts

      int numTgts = tgts.size();
      size += (Integer.BYTES * 2) * numTgts;//id and type
      size += (Double.BYTES * 3) * numTgts;//hdg, north, east
      size += numTgts; //1 byte for all the mobile flags

      return size;
   }

}
