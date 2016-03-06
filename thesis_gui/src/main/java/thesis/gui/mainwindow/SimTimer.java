package thesis.gui.mainwindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;

public class SimTimer
{
   private static final int PAUSE = -1;
   private static final int STEP = -2;
   private static final int PLAY = -3;

   private Logger logger;
   private SimRunner simRunner;

   public SimTimer()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
   }

   public void connectSimRunner(SimRunner simRunner)
   {
      this.simRunner = simRunner;
   }

   public void step()
   {
      logger.info("Stepping simulation");
      run(STEP);
   }

   public void pause()
   {
      logger.info("Pausing simulation");
      run(PAUSE);
   }

   public void play()
   {
      logger.info("Resuming simulation.");
      run(PLAY);
   }

   public void run(int interFrameDelayMS)
   {
      if(interFrameDelayMS > 0)
      {
         int hertz = 1000 / interFrameDelayMS;
         logger.info("Free running simulation at {}Hz", hertz);
      }
      else
      {
         logger.info("Free running simulation at CPU speed");
      }

      simRunner.setInterframeDelay(interFrameDelayMS);
   }
}
