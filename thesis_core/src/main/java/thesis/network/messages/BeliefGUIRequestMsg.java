package thesis.network.messages;

import java.nio.ByteBuffer;

public class BeliefGUIRequestMsg extends InfrastructureMsg
{

   public BeliefGUIRequestMsg()
   {
      super(InfrastructureMsgType.BeliefGUIRequest);
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      // Nothing to do
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      // Nothing to do
   }

   @Override
   public long getEncodedSize()
   {
      return 0;
   }

}
