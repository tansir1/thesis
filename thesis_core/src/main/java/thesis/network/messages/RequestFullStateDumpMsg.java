package thesis.network.messages;

import java.nio.ByteBuffer;

@Deprecated
public class RequestFullStateDumpMsg extends InfrastructureMsg
{

   public RequestFullStateDumpMsg()
   {
      super(InfrastructureMsgType.RequestFullStateDump);
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      // No data
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      // No data
   }

   @Override
   public long getEncodedSize()
   {
      return 0;
   }

}
