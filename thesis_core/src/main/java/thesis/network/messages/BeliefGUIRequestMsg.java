package thesis.network.messages;

import java.nio.ByteBuffer;

public class BeliefGUIRequestMsg extends InfrastructureMsg
{
   private int uavID;
   public BeliefGUIRequestMsg()
   {
      super(InfrastructureMsgType.BeliefGUIRequest);
      uavID = -1;
   }

   public int getUAVID()
   {
      return uavID;
   }

   public void setUAVID(int uavID)
   {
      this.uavID = uavID;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putInt(uavID);
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      uavID = buf.getInt();
   }

   @Override
   public long getEncodedSize()
   {
      return Integer.BYTES;
   }

}
