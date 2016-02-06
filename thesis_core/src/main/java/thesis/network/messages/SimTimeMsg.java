package thesis.network.messages;

import java.nio.ByteBuffer;

public class SimTimeMsg extends InfrastructureMsg
{
   private long simWallTime;
   private long simTime;
   private long frameCount;

   public SimTimeMsg()
   {
      super(InfrastructureMsgType.SimTime);
      simWallTime = 0;
      simTime = 0;
      frameCount = 0;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putLong(simWallTime);
      buf.putLong(simTime);
      buf.putLong(frameCount);
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      simWallTime = buf.getLong();
      simTime = buf.getLong();
      frameCount = buf.getLong();
   }

   @Override
   public short getEncodedSize()
   {
      return 3 * Long.BYTES;
   }

   public long getSimWallTime()
   {
      return simWallTime;
   }

   public void setSimWallTime(long simWallTime)
   {
      this.simWallTime = simWallTime;
   }

   public long getSimTime()
   {
      return simTime;
   }

   public void setSimTime(long simTime)
   {
      this.simTime = simTime;
   }

   public long getFrameCount()
   {
      return frameCount;
   }

   public void setFrameCount(long frameCount)
   {
      this.frameCount = frameCount;
   }

}
