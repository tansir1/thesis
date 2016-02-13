package thesis.gui.mainwindow;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.BeliefGUIResponseMsg;
import thesis.network.messages.FullInitReponseMsg;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SimStateUpdateMsg;
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
            case BeliefGUIResponse:
               handleBeliefGUIResponseMsg(msg);
               break;
            case FullInitReponse:
               handleFullInitReponseMsg(msg);
               break;
            case SimStateUpdate:
               handleSimStateUpdateMsg(msg);
               break;
            case SimTime:
               handleSimTime(msg);
               break;
            default:
               logger.error("No handler defined for message {}.", msg.getMessageType());
               break;
            }
         }
         catch (Exception e)
         {
            logger.error("Failed to handle message from receiving queue. Details: {}", e.getMessage());
         }
      }
   }

   private void handleSimTime(InfrastructureMsg rawMsg)
   {
      logger.trace("Received sim time update.");
      SimTimeMsg msg = (SimTimeMsg)rawMsg;
      window.getSimStatusPanel().update(msg);
   }

   private void handleFullInitReponseMsg(InfrastructureMsg rawMsg)
   {
      logger.info("Received full simulation init response msg.");
      FullInitReponseMsg msg = (FullInitReponseMsg)rawMsg;
      window.onFullInitResponseMsg(msg.getSimStateDump(), msg.getEntityTypeConfigs());
   }

   private void handleSimStateUpdateMsg(InfrastructureMsg rawMsg)
   {
      logger.trace("Received simulation update message.");
      SimStateUpdateMsg msg = (SimStateUpdateMsg)rawMsg;
      window.onSimStateUpdate(msg.getUpdateDump());
   }

   private void handleBeliefGUIResponseMsg(InfrastructureMsg rawMsg)
   {
      logger.trace("Received Belief GUI response message.");
      BeliefGUIResponseMsg msg = (BeliefGUIResponseMsg)rawMsg;
      window.onBeliefGUIResponse(msg.getBelief());
   }
}
