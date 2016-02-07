package thesis.gui.mainwindow;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SetSimStepRateMsg;

public class SimTimer
{
   private static final int PAUSE = -1;
   private static final int STEP = -2;
   private static final int PLAY = -3;

   private Logger logger;
   private LinkedBlockingQueue<InfrastructureMsg> sendQ;
   private int currentDelay;

   public SimTimer()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      currentDelay = -1;
   }

   public void connectQueue(LinkedBlockingQueue<InfrastructureMsg> sendQ)
   {
      this.sendQ = sendQ;
   }

   public void step()
   {
      logger.info("Stepping simulation");
      sendRate(STEP);
   }

   public void pause()
   {
      logger.info("Pausing simulation");
      sendRate(PAUSE);
   }

   public void play()
   {
      logger.info("Resuming simulation.");
      sendRate(PLAY);
   }

   public void run(int interFrameDelayMS)
   {
      if(interFrameDelayMS >= 0)
      {
         currentDelay = interFrameDelayMS;
      }

      if(interFrameDelayMS > 0)
      {
         int hertz = 1000 / interFrameDelayMS;
         logger.info("Free running simulation at {}Hz", hertz);
      }
      else
      {
         logger.info("Free running simulation at CPU speed");
      }

      sendRate(interFrameDelayMS);
   }

   private void sendRate(int interFrameDelayMS)
   {
      SetSimStepRateMsg msg = new SetSimStepRateMsg();
      msg.setInterFrameDelay(interFrameDelayMS);

      if(sendQ != null)
      {
         try
         {
            sendQ.put(msg);
         }
         catch (InterruptedException e)
         {
            logger.error("Failed to enqueue SetSimRateMsg. Details: {}", e.getMessage());
         }
      }
   }

}
