package thesis.network.messages;

import java.nio.ByteBuffer;

public class ShutdownMsg extends InfrastructureMsg
{

   public ShutdownMsg()
   {
      super(InfrastructureMsgType.Shutdown);
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      //Nothing to encode
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      //Nothing to decode
   }

   @Override
   public long getEncodedSize()
   {
      return 0;
   }

}
