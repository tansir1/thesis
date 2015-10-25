package thesis.gui.mainwindow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.utilities.LoggerIDs;
import thesis.gui.simpanel.RenderableSimWorldPanel;

public class SimTimer
{
   /**
    * Length in time in milliseconds between subsequent steps of the simulation.
    */
   private final long timeStepMS;

   private ScheduledExecutorService execSvc;

   private SimModel model;
   private ScheduledFuture<?> future;

   private Logger logger;

   private RenderableSimWorldPanel simPanel;

   /**
    * Initialize a timer to drive the simulation. Does nothing without a
    * simulation model being set.
    *
    * @param simPanel
    *           After stepping the simulation the a request to repaint this
    *           panel will be queued in the application's main EDT.
    *
    * @see #reset(SimModel)
    */
   public SimTimer(RenderableSimWorldPanel simPanel)
   {
      if(simPanel == null)
      {
         throw new NullPointerException("SimPanel cannot be null.");
      }
      this.simPanel = simPanel;

      logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      execSvc = Executors.newSingleThreadScheduledExecutor();

      timeStepMS = 16;// 60hz update rate
   }

   public void reset(SimModel model)
   {
      if (model == null)
      {
         throw new NullPointerException("Model cannot be null.");
      }

      this.model = model;
   }

   public void step()
   {
      if (model != null)
      {
         if (future != null)
         {
            future.cancel(false);
         }

         logger.info("Stepping simulation.");
         model.stepSimulation(timeStepMS);
         SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run()
            {
               simPanel.repaint();
            }
         });
      }
   }

   public void pause()
   {
      if (model != null && future != null)
      {
         logger.info("Simulation paused.");
         future.cancel(false);
      }
   }

   public void run()
   {
      if (future != null)
      {
         future.cancel(false);
      }

      if (model != null)
      {
         logger.info("Free running simulation");
         future = execSvc.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run()
            {
               if (model != null)
               {
                  model.stepSimulation(timeStepMS);
                  SwingUtilities.invokeLater(new Runnable() {

                     @Override
                     public void run()
                     {
                        simPanel.repaint();
                     }
                  });
               }
            }
         }, 0, timeStepMS, TimeUnit.MILLISECONDS);
      }
   }

}