package thesis.network.messages;

import java.nio.ByteBuffer;

public abstract class InfrastructureMsg
{
   private InfrastructureMsgType msgType;

   public InfrastructureMsg(InfrastructureMsgType msgType)
   {
      this.msgType = msgType;
   }

   public InfrastructureMsgType getMessageType()
   {
      return msgType;
   }

   public abstract void encodeData(ByteBuffer buf);
   public abstract void decodeData(ByteBuffer buf);
   public abstract int getEncodedSize();
}
