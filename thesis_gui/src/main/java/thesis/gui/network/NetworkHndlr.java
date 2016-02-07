package thesis.gui.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.ServerComms;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.RequestFullStateDumpMsg;

public class NetworkHndlr implements Runnable
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN_NET);

   /**
    * The rate at which the network will be polled for incoming messages and
    * transmit outgoing messages.
    */
   private final int NETWORK_RATE_MS = 250;

   private ServerComms comms;
   private ScheduledExecutorService execSvc;

   private LinkedBlockingQueue<InfrastructureMsg> outQ;
   private LinkedBlockingQueue<InfrastructureMsg> inQ;

   public NetworkHndlr()
   {
      comms = new ServerComms();

      execSvc = Executors.newSingleThreadScheduledExecutor();
   }

   public void start(LinkedBlockingQueue<InfrastructureMsg> outQ, LinkedBlockingQueue<InfrastructureMsg> inQ,
         String serverIP, int serverPort)
   {
      this.outQ = outQ;
      this.inQ = inQ;

      ClientListenerTask listener = new ClientListenerTask(comms, serverIP, serverPort, this);
      execSvc.submit(listener);
   }

   protected void enqueueSelf()
   {
      RequestFullStateDumpMsg msg = new RequestFullStateDumpMsg();
      outQ.add(msg);
      execSvc.scheduleAtFixedRate(this, 0, NETWORK_RATE_MS, TimeUnit.MILLISECONDS);
   }

   @Override
   public void run()
   {
      List<InfrastructureMsg> sendMsgs = new ArrayList<InfrastructureMsg>();
      while (!outQ.isEmpty())
      {
         try
         {
            sendMsgs.add(outQ.take());
         }
         catch (InterruptedException e)
         {
            logger.error("Failed to pop message from outgoing queue.  Details: {}", e.getMessage());
         }
      }

      if (!sendMsgs.isEmpty())
      {
         logger.trace("Transmitting data to client.");
         comms.sendData(sendMsgs);
      }

      logger.trace("Polling for received data.");
      List<InfrastructureMsg> recvMsgs = comms.getData();
      if (recvMsgs != null)
      {
         for (InfrastructureMsg inMsg : recvMsgs)
         {
            try
            {
               inQ.put(inMsg);
            }
            catch (InterruptedException e)
            {
               logger.error("Failed to place incoming messages into receiving queue. Details: {}", e.getMessage());
            }
         }
      }
   }
}
