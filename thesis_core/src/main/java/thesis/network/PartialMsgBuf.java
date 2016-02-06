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

   public List<InfrastructureMsg> assembleMessages()
   {
      List<InfrastructureMsg> msgs = new ArrayList<InfrastructureMsg>();
      boolean incompleteMsg = false;
      int numBytesToParse = buf.position();
      while(numBytesToParse >= InfrastructMsgHdr.HEADER_SIZE && !incompleteMsg)
      {
         buf.flip();

         msgHdr.decodeData(buf);
         if((InfrastructMsgHdr.HEADER_SIZE + msgHdr.getMessageSize()) <= numBytesToParse)
         {
            InfrastructureMsg msg = InfrastructMsgFact.createMessage(msgHdr.getMessageType());
            msg.decodeData(buf);
            msgs.add(msg);
            msgHdr.reset();
            buf.compact();
            numBytesToParse = buf.position();
         }
         else
         {
            //Not enough bytes to decode message, reset the position to the end of the buffer and wait for more bytes.
            incompleteMsg = true;
            buf.position(numBytesToParse);
         }
      }
      return msgs;
   }
}
