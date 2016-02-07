package thesis.network.messages;

import java.nio.ByteBuffer;

public class SetSimStepRateMsg extends InfrastructureMsg
{
   /**
    * Delay between frames in milliseconds.
    */
   private int interFrameDelay;

   public SetSimStepRateMsg()
   {
      super(InfrastructureMsgType.SetSimStepRate);
      interFrameDelay = 0;
   }

   public void setInterFrameDelay(int hertz)
   {
      this.interFrameDelay = hertz;
   }

   public int getInterFrameDelay()
   {
      return this.interFrameDelay;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putInt(interFrameDelay);
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      interFrameDelay = buf.getInt();
   }

   @Override
   public short getEncodedSize()
   {
     return (short)Integer.BYTES;
   }

}
