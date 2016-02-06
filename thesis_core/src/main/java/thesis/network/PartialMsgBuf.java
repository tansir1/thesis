package thesis.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import thesis.network.messages.InfrastructMsgFact;
import thesis.network.messages.InfrastructMsgHdr;
import thesis.network.messages.InfrastructureMsg;

public class PartialMsgBuf
{
   private final int BUFFER_SZ;
   private ByteBuffer buf;

   private InfrastructMsgHdr msgHdr;

   public PartialMsgBuf()
   {
      BUFFER_SZ = 1024 * 1024 * 5;// 5 MB buffer
      buf = ByteBuffer.allocate(BUFFER_SZ);

      msgHdr = new InfrastructMsgHdr();
   }

   public ByteBuffer getBuffer()
   {
      return buf;
   }

   private void decodeHeader()
   {
      if(buf.limit() > InfrastructMsgHdr.HEADER_SIZE)
      {
         msgHdr.decodeData(buf);
      }
   }

   public List<InfrastructureMsg> assembleMessages(int bytesRead)
   {
      List<InfrastructureMsg> msgs = new ArrayList<InfrastructureMsg>();

      if(msgHdr.getMessageSize() > -1)
      {
         do
         {
            if (buf.limit() >= msgHdr.getMessageSize())
            {
               InfrastructureMsg msg = InfrastructMsgFact.createMessage(msgHdr.getMessageType());
               msgs.add(msg);
               msgHdr.reset();
               buf.compact();
               decodeHeader();
            }
         }while (msgHdr.getMessageSize() > -1);
      }
      else
      {
         decodeHeader();
      }
      return msgs;
   }
}
