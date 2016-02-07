package thesis.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.InfrastructMsgHdr;
import thesis.network.messages.InfrastructureMsg;

public class ServerComms
{
   private final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN_NET);

   private SocketChannel channel;
   private ServerSocketChannel serverChannel;
   private PartialMsgBuf recvBuf;

   private final int BUFFER_SZ;
   private ByteBuffer sendBuf;

   public ServerComms()
   {
      BUFFER_SZ = 1024 * 1024 * 5;// 5 MB buffer
      sendBuf = ByteBuffer.allocate(BUFFER_SZ);
      recvBuf = new PartialMsgBuf();
   }


   public void listenForClient(String ip, int port)
   {
      if (channel != null)
      {
         logger.warn("Already have a connected client.  Disconnecting first before reconnecting.");
         disconnect();
      }
      SocketAddress listenOnAddr = new InetSocketAddress(ip, port);

      try
      {
         serverChannel = ServerSocketChannel.open();
         serverChannel.bind(listenOnAddr);
         serverChannel.configureBlocking(false);

         while(channel == null)
         {
            channel = serverChannel.accept();
            Thread.sleep(200);
         }

         channel.configureBlocking(false);
         logger.info("Client connected.");
      }
      catch (IOException | InterruptedException e)
      {
         logger.error("Failed to listen for client. Details: {}", e.getMessage());
      }
   }

   public void sendData(List<InfrastructureMsg> msgs)
   {
      int numMsgs = msgs.size();
      InfrastructMsgHdr msgHdr = new InfrastructMsgHdr();
      for (int i = 0; i < numMsgs; ++i)
      {
         InfrastructureMsg msg = msgs.get(i);
         if(msg.getEncodedSize() > BUFFER_SZ)
         {
            throw new IllegalArgumentException("Encoded message size is larger than sending buffer size.");
         }

         msgHdr.setMessageSize(msg.getEncodedSize());
         msgHdr.setMessageType(msg.getMessageType());
         msgHdr.encodeData(sendBuf);
         msg.encodeData(sendBuf);
         sendBuf.flip();

         try
         {
            while (sendBuf.hasRemaining())
            {
               channel.write(sendBuf);
            }
         }
         catch (IOException e)
         {
            logger.error("Failed to send data to client. Details: {}", e.getMessage());
         }
         sendBuf.clear();
      }
   }

   /**
    * @return A list of parsed messages from the server or null if no messages exist.
    */
   public List<InfrastructureMsg> getData()
   {
      List<InfrastructureMsg> data = null;
      try
      {
         int bytesRead = channel.read(recvBuf.getBuffer());
         if(bytesRead != -1)
         {
            data = recvBuf.assembleMessages();
         }
         else
         {
            logger.error("Connection to client is closed.  Disconnecting.");
            disconnect();
         }

      }
      catch (Exception e)
      {
         logger.error("Failed to read data from client.  Details: {}", e.getMessage());
      }

      return data;
   }

   public void disconnect()
   {
      if (channel != null)
      {
         logger.debug("Disconnecting client.");
         try
         {
            channel.close();
            channel = null;
         }
         catch (IOException e)
         {
            logger.error("Failed to disconnect the client. Details: {}", e.getMessage());
         }
      }

      if(serverChannel != null)
      {
         logger.debug("Closing server channel.");
         try
         {
            serverChannel.close();
            serverChannel = null;
         }
         catch (IOException e)
         {
            logger.error("Failed to terminate the server channel. Details: {}", e.getMessage());
         }
      }
   }
}
