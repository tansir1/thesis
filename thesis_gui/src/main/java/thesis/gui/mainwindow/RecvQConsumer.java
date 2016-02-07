package thesis.gui.mainwindow;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.FullInitReponseMsg;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SimTimeMsg;

public class RecvQConsumer implements Runnable
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN_NET);

   private LinkedBlockingQueue<InfrastructureMsg> recvQ;
   private MainWindow window;

   public RecvQConsumer(MainWindow window, LinkedBlockingQueue<InfrastructureMsg> recvQ)
   {
      this.window = window;
      this.recvQ = recvQ;
   }

   @Override
   public void run()
   {
      while (!recvQ.isEmpty())
      {
         try
         {
            InfrastructureMsg msg = recvQ.take();
            switch (msg.getMessageType())
            {
            case FullInitReponse:
               handleFullInitReponseMsg(msg);
            case SimTime:
               handleSimTime(msg);
               break;
            default:
               logger.error("No handler defined for message {}.", msg.getMessageType());
               break;
            }
         }
         catch (InterruptedException e)
         {
            logger.error("Failed to take message from receiving queue. Details: {}", e.getMessage());
         }
      }
   }

   private void handleSimTime(InfrastructureMsg rawMsg)
   {
      SimTimeMsg msg = (SimTimeMsg)rawMsg;
      window.getSimStatusPanel().update(msg);
   }

   private void handleFullInitReponseMsg(InfrastructureMsg rawMsg)
   {
      FullInitReponseMsg msg = (FullInitReponseMsg)rawMsg;
      window.onFullInitResponseMsg(msg.getSimStateDump(), msg.getEntityTypeConfigs());
   }
}
