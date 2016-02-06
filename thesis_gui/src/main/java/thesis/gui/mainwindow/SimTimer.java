package thesis.gui.mainwindow;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.SimTime;
import thesis.core.utilities.LoggerIDs;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SetSimStepRateMsg;

public class SimTimer
{
   private Logger logger;
   private LinkedBlockingQueue<InfrastructureMsg> sendQ;

   public SimTimer()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
   }

   public void connectQueue(LinkedBlockingQueue<InfrastructureMsg> sendQ)
   {
      this.sendQ = sendQ;
   }

   public void step()
   {
      logger.info("Stepping simulation");
      sendRate(-1);
   }

   public void pause()
   {
      logger.info("Pausing simulation");
      sendRate(0);
   }

   public void run(int fastMultiplier)
   {
      int hertz =  SimTime.SIM_STEP_RATE_HZ * fastMultiplier;
      logger.info("Free running simulation at {}hz", hertz);

      sendRate(hertz);
   }

   private void sendRate(int hertz)
   {
      SetSimStepRateMsg msg = new SetSimStepRateMsg();
      msg.setStepRate(hertz);

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
