package thesis.network.messages;

import java.nio.ByteBuffer;

public class SetSimStepRateMsg extends InfrastructureMsg
{
   /**
    * Step rate in hertz.
    */
   private int rateInHz;

   public SetSimStepRateMsg()
   {
      super(InfrastructureMsgType.SetSimStepRate);
      rateInHz = 0;
   }

   public void setStepRate(int hertz)
   {
      this.rateInHz = hertz;
   }

   public int getStepRate()
   {
      return this.rateInHz;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putInt(rateInHz);
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      rateInHz = buf.getInt();
   }

   @Override
   public short getEncodedSize()
   {
     return (short)Integer.BYTES;
   }

}
