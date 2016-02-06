package thesis.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.InfrastructureMsg;

public class ClientComms
{
   private final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN_NET);

   private SocketChannel channel;
   private PartialMsgBuf recvBuf;

   private final int BUFFER_SZ;
   private ByteBuffer sendBuf;

   public ClientComms()
   {
      BUFFER_SZ = 1024 * 1024 * 5;// 5 MB buffer
      sendBuf = ByteBuffer.allocate(BUFFER_SZ);
      recvBuf = new PartialMsgBuf();
   }

   public boolean connect(String ip, int port)
   {
      if (channel != null)
      {
         logger.warn("Already connected to the server.  Disconnecting first before reconnecting.");
         disconnect();
      }

      boolean success = false;
      SocketAddress server = new InetSocketAddress(ip, port);
      logger.info("Connecting to server.");

      try
      {
         channel = SocketChannel.open();
         channel.configureBlocking(false);
         channel.connect(server);

         while (!channel.finishConnect())
         {
            // TODO Add a time limited failure condition
            logger.debug("Waiting on server response...");
            Thread.sleep(200);
         }

         logger.debug("Setting socket options");

         logger.info("Connected to server.");
         success = true;
      }
      catch (IOException | InterruptedException e)
      {
         logger.error("Failed to connect to server.  Details: {}", e.getMessage());
      }
      return success;
   }

   public void sendData(List<InfrastructureMsg> msgs)
   {
      int numMsgs = msgs.size();

      for (int i = 0; i < numMsgs; ++i)
      {
         InfrastructureMsg msg = msgs.get(i);
         if(msg.getEncodedSize() > BUFFER_SZ)
         {
            throw new IllegalArgumentException("Encoded message size is larger than sending buffer size.");
         }

         msg.encodeData(sendBuf);

         try
         {
            while (sendBuf.hasRemaining())
            {
               channel.write(sendBuf);
            }
         }
         catch (IOException e)
         {
            logger.error("Failed to send data to server. Details: {}", e.getMessage());
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
            data = recvBuf.assembleMessages(bytesRead);
         }
         else
         {
            logger.error("Connection to server is closed.  Disconnecting.");
            disconnect();
         }

      }
      catch (IOException e)
      {
         logger.error("Failed to read data from server.  Details: {}", e.getMessage());
      }

      return data;
   }

   public void disconnect()
   {
      if (channel != null)
      {
         logger.debug("Disconnected from server.");
         try
         {
            channel.close();
         }
         catch (IOException e)
         {
            logger.error("Failed to disconnect from server. Details: {}", e.getMessage());
         }
      }
   }
}
