package thesis.network.messages;

import java.nio.ByteBuffer;

public class InfrastructMsgHdr
{
   public static final int HEADER_SIZE = 3;// 1 byte for msgType, 2 for message
                                           // size

   private InfrastructureMsgType msgType;
   private long msgSz;

   public InfrastructMsgHdr()
   {
      reset();
   }

   public void reset()
   {
      this.msgType = null;
      msgSz = -1;
   }

   public InfrastructureMsgType getMessageType()
   {
      return msgType;
   }

   public void setMessageType(InfrastructureMsgType type)
   {
      msgType = type;
   }

   public void encodeData(ByteBuffer buf)
   {
      buf.put(msgType.getMsgID());
      buf.putLong(msgSz);
   }

   public boolean decodeData(ByteBuffer buf)
   {
      boolean success = false;
      if (buf.limit() >= HEADER_SIZE)
      {
         msgType = InfrastructureMsgType.fromMsgID(buf.get());
         msgSz = buf.getLong();
         success = true;
      }
      return success;
   }

   public long getMessageSize()
   {
      return msgSz;
   }

   public void setMessageSize(long sz)
   {
      this.msgSz = sz;
   }
}
