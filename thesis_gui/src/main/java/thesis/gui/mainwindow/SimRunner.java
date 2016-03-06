package thesis.gui.mainwindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.utilities.LoggerIDs;

public class SimRunner implements Runnable
{
   /**
    * If the time between now and the last GUI rendering is greater than
    * this value (milliseconds) then re-render the GUI
    */
   private final long GUI_INTERVAL_MS = 25;

   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

   private SimModel simModel;
   private volatile boolean terminateApp;
   private boolean pause;
   private boolean stepOneFrame;

   private long lastGUITime;
   private long frameCnt;

   /**
    * User specified delay between frames in milliseconds.
    */
   private int interFrameDelayMS;

   private ISimGUIUpdater renderer;

   public SimRunner()
   {
      terminateApp = false;
      pause = true;
      interFrameDelayMS = 250;
      stepOneFrame = false;

      lastGUITime = 0;
      frameCnt = -1;

   }

   @Override
   public void run()
   {
      long wallTime = 0;

      while (!terminateApp)
      {
         // logger.trace("---Frame {}---", frameCnt);
         wallTime = System.currentTimeMillis();

         if ((wallTime - lastGUITime) > GUI_INTERVAL_MS)
         {
            lastGUITime = wallTime;
            renderer.updateGUI();
         }

         if (!pause || stepOneFrame)
         {
            if (stepOneFrame)
            {
               stepOneFrame = false;
               logger.info("Stepping one frame.");
            }

            frameCnt++;

            synchronized(simModel)
            {
               simModel.stepSimulation();
            }
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
   }

   public boolean init(SimModel simModel, ISimGUIUpdater renderer)
   {
      boolean success = true;

      this.simModel = simModel;
      this.renderer = renderer;

      return success;
   }

   public void setInterframeDelay(int interFrameDelayMS)
   {
      if (interFrameDelayMS == -1)
      {
         pause = true;
         logger.info("Simulation paused.");
      }

      if (interFrameDelayMS == -2)
      {
         stepOneFrame = true;
         if (!pause)
         {
            pause = true;
            logger.info("Simulation paused.");
         }
      }

      if (interFrameDelayMS == -3)
      {
         pause = false;
         logger.info("Simulation unpaused.  Running with {}ms inter-frame delay.", interFrameDelayMS);
      }

      if (interFrameDelayMS >= 0 && interFrameDelayMS != this.interFrameDelayMS)
      {
         logger.info("Inter-frame delay changed from {}ms to {}ms.", this.interFrameDelayMS, interFrameDelayMS);
         this.interFrameDelayMS = interFrameDelayMS;
      }
   }

   public void terminateSim()
   {
      logger.debug("Terminating simulation thread.");
      terminateApp = true;
   }

}
