package thesis.sim;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.SimStateUpdateDump;
import thesis.core.uav.UAV;
import thesis.core.utilities.LoggerIDs;
import thesis.network.ClientComms;
import thesis.network.messages.BeliefGUIRequestMsg;
import thesis.network.messages.BeliefGUIResponseMsg;
import thesis.network.messages.FullInitReponseMsg;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.SetSimStepRateMsg;
import thesis.network.messages.SimStateUpdateMsg;
import thesis.network.messages.SimTimeMsg;
import thesis.sim.utilities.SimAppConfig;

public class ThesisSimApp
{
   /**
    * If the time between now and the last network processing is greater than
    * this value (milliseconds) then process the network communications.
    */
   private final long NETWORK_INTERVAL_MS = 25;

   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

   private SimModel simModel;
   private ClientComms network;
   private volatile boolean terminateApp;
   private boolean pause;
   private boolean stepOneFrame;

   private long lastNetworkTime;
   private long frameCnt;

   /**
    * User specified delay between frames in milliseconds.
    */
   private int interFrameDelayMS;

   private SimStateDump simStateDump;
   private SimStateUpdateDump simStateUpdateDump;
   private boolean serverReadyForUpdates;

   public ThesisSimApp()
   {
      network = new ClientComms();
      terminateApp = false;
      pause = true;
      interFrameDelayMS = 250;
      stepOneFrame = false;

      lastNetworkTime = 0;
      frameCnt = -1;

      simStateDump = new SimStateDump();
      simStateUpdateDump = new SimStateUpdateDump();
      serverReadyForUpdates = false;
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
      if (!network.isReady())
      {
         return;
      }

      logger.trace("Processing data from server");
      List<InfrastructureMsg> msgs = network.getData();
      if (msgs != null)
      {
         for (InfrastructureMsg msg : msgs)
         {
            switch (msg.getMessageType())
            {
            case BeliefGUIRequest:
               processBeliefGUIRequest(msg);
               break;
            case RequestFullStateDump:
               processRequestFullStateDump();
               break;
            case SetSimStepRate:
               processSetSimStepRateMsg(msg);
               break;
            case Shutdown:
               processShutdownMsg();
               break;
            default:
               logger.warn("No handlers exist for messages of type {}.", msg.getMessageType());
               break;
            }
         }
      }

   }

   private void transmitData()
   {
      if (!network.isReady() || !serverReadyForUpdates)
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

      SimStateUpdateMsg simUpdateMsg = new SimStateUpdateMsg();
      simStateDump.fillUpdateDump(simStateUpdateDump);
      simUpdateMsg.setUpdateDump(simStateUpdateDump);
      msgs.add(simUpdateMsg);

      network.sendData(msgs);
   }

   public void runSim()
   {
      long wallTime = 0;
      boolean processNetwork = false;

      while (!terminateApp)
      {
         // logger.trace("---Frame {}---", frameCnt);
         wallTime = System.currentTimeMillis();

         if ((wallTime - lastNetworkTime) > NETWORK_INTERVAL_MS)
         {
            lastNetworkTime = wallTime;
            processNetwork = true;
            processIncomingMessages();
         }

         if (!pause || stepOneFrame)
         {
            if (stepOneFrame)
            {
               stepOneFrame = false;
               logger.info("Stepping one frame.");
            }

            frameCnt++;
            simModel.stepSimulation();
         }

         if (processNetwork)
         {
            simStateDump.update(simModel);
            transmitData();
            processNetwork = false;
         }

         if (interFrameDelayMS > 0)
         {
            try
            {
               // logger.trace("sleeping {}", interFrameDelayMS);
               Thread.sleep(interFrameDelayMS);

            }
            catch (InterruptedException e)
            {
               logger.error("Failed to sleep between frames.  Details: {}", e.getMessage());
            }
         }
      }

      logger.info("Terminating simulation.");
      network.disconnect();
   }

   private void processSetSimStepRateMsg(InfrastructureMsg rawMsg)
   {
      SetSimStepRateMsg msg = (SetSimStepRateMsg) rawMsg;
      int interFrameDelay = msg.getInterFrameDelay();

      if (interFrameDelay == -1)
      {
         pause = true;
         logger.info("Simulation paused.");
      }

      if (interFrameDelay == -2)
      {
         stepOneFrame = true;
         if (!pause)
         {
            pause = true;
            logger.info("Simulation paused.");
         }
      }

      if (interFrameDelay == -3)
      {
         pause = false;
         logger.info("Simulation unpaused.  Running with {}ms inter-frame delay.", interFrameDelayMS);
      }

      if (interFrameDelay >= 0 && interFrameDelay != this.interFrameDelayMS)
      {
         logger.info("Inter-frame delay changed from {}ms to {}ms.", this.interFrameDelayMS, interFrameDelay);
         this.interFrameDelayMS = interFrameDelay;
      }

   }

   private void processRequestFullStateDump()
   {
      FullInitReponseMsg msg = new FullInitReponseMsg();
      msg.setSimStateDump(simStateDump);
      msg.setEntityTypeConfigs(simModel.getEntityTypeCfgs());
      network.sendData(msg);
      serverReadyForUpdates = true;
   }

   private void processShutdownMsg()
   {
      logger.debug("Received shutdown message.");
      terminateApp = true;
   }

   private void processBeliefGUIRequest(InfrastructureMsg rawMsg)
   {
      logger.trace("Received Belief GUI request message.");
      BeliefGUIRequestMsg msg = (BeliefGUIRequestMsg)rawMsg;
      int uavID = msg.getUAVID();


      UAV uav = simModel.getUAVManager().getUAV(uavID);
      if(uav != null)
      {
         logger.trace("Sending Belief GUI response message.");
         BeliefGUIResponseMsg response = new BeliefGUIResponseMsg();
         response.setWorldBelief(uav.getBelief());
         network.sendData(response);
      }

   }
}
