package thesis.sim;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.statedump.SimStateDump;
import thesis.core.utilities.LoggerIDs;
import thesis.network.ClientComms;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SimTimeMsg;
import thesis.sim.utilities.SimAppConfig;

public class ThesisSimApp
{
   /**
    * If the time between now and the last network processing is greater than
    * this value (milliseconds) then process the network communications.
    */
   private final long NETWORK_INTERVAL_MS = 500;

   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

   private SimModel simModel;
   private ClientComms network;
   private volatile boolean terminateApp;
   private boolean pause;

   private long lastNetworkTime;
   private long frameCnt;

   private SimStateDump simStateDump;

   public ThesisSimApp()
   {
      network = new ClientComms();
      terminateApp = false;
      pause = false;

      lastNetworkTime = 0;
      frameCnt = -1;

      simStateDump = new SimStateDump();
   }

   public boolean init(SimAppConfig cfg, SimModel simModel)
   {
      boolean success = true;

      if (cfg.isEnableNetwork())
      {
         success = network.connect(cfg.getServerIP(), cfg.getServerPort());
      }

      this.simModel = simModel;
      simStateDump.init(simModel);

      return success;
   }

   public void terminateSim()
   {
      terminateApp = true;
   }

   private void processIncomingMessages()
   {
      if(!network.isReady())
      {
         return;
      }

      logger.trace("Processing data from server");
      List<InfrastructureMsg> msgs = network.getData();
      if(msgs != null)
      {
         for (InfrastructureMsg msg : network.getData())
         {
            switch (msg.getMessageType())
            {
            default:
               logger.warn("No handlers exist for messages of type {}.", msg.getMessageType());
               break;
            }
         }
      }

   }

   private void transmitData()
   {
      if(!network.isReady())
      {
         return;
      }

      logger.trace("Transmitting data to server");
      List<InfrastructureMsg> msgs = new ArrayList<InfrastructureMsg>();

      SimTimeMsg simTimeMsg = new SimTimeMsg();
      simTimeMsg.setFrameCount(frameCnt);
      simTimeMsg.setSimTime(SimTime.CURRENT_SIM_TIME_MS);
      simTimeMsg.setSimWallTime(SimTime.getWallTime());
      msgs.add(simTimeMsg);

      simStateDump.getUpdateMsgs(msgs);

      network.sendData(msgs);
   }

   public void runSim()
   {
      long wallTime = 0;
      boolean processNetwork = false;

      long lastUpdateTime = 0;
      long frameTime = 0;
      while (!terminateApp)
      {
         wallTime = System.currentTimeMillis();
         frameCnt++;

         if ((wallTime - lastNetworkTime) > NETWORK_INTERVAL_MS)
         {
            lastNetworkTime = wallTime;
            processNetwork = true;
            processIncomingMessages();
         }

         if (!pause)
         {
            simModel.stepSimulation();
         }

         if (processNetwork)
         {
            simStateDump.update(simModel);
            transmitData();
            processNetwork = false;
         }

         wallTime = System.currentTimeMillis();

         frameTime = wallTime - lastUpdateTime;
         lastUpdateTime = wallTime;

         // Compute how much time is left from now until the next scheduled
         // frame and sleep.
         long remainingStepTime = SimTime.SIM_STEP_RATE_MS - frameTime;
         if (remainingStepTime > 0)
         {
            try
            {
               Thread.sleep(remainingStepTime);
            }
            catch (InterruptedException e)
            {
               logger.error("Failed to sleep and delay for the remaining steady frame time.  Details: {}",
                     e.getMessage());
            }
         }
      }

      logger.info("Terminating simulation.");
      network.disconnect();
   }

}
