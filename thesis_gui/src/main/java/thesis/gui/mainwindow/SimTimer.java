package thesis.gui.mainwindow;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.utilities.LoggerIDs;
import thesis.gui.simpanel.RenderableSimWorldPanel;

public class SimTimer
{
   private static final long UPDATE_GUI_RATE_MS = 250;

   private ScheduledExecutorService execSvc;

   private SimModel model;
   private ScheduledFuture<?> future;

   private Logger logger;

   private RenderableSimWorldPanel simPanel;

   private long guiRefreshAccumulator;

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
      if (simPanel == null)
      {
         throw new NullPointerException("SimPanel cannot be null.");
      }
      this.simPanel = simPanel;

      logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      execSvc = Executors.newSingleThreadScheduledExecutor();

      guiRefreshAccumulator = 0;
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
         model.stepSimulation();

         guiRefreshAccumulator = 0;
         try
         {
            SwingUtilities.invokeAndWait(new Runnable()
            {

               @Override
               public void run()
               {
                  simPanel.repaint();
               }
            });
         }
         catch (InvocationTargetException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
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

   public void run(int fastMultiplier)
   {
      if (future != null)
      {
         future.cancel(false);
      }

      if (model != null)
      {
         double stepRate = SimTime.SIM_STEP_RATE_MS / (fastMultiplier * 1.0) * 1000;
         logger.info("Free running simulation at {}x", fastMultiplier);
         future = execSvc.scheduleAtFixedRate(new Runnable()
         {

            @Override
            public void run()
            {
               try
               {
                  if (model != null)
                  {
                     model.stepSimulation();
                     guiRefreshAccumulator += SimTime.SIM_STEP_RATE_MS;
                     if(guiRefreshAccumulator > UPDATE_GUI_RATE_MS)
                     {
                        guiRefreshAccumulator = 0;
                        SwingUtilities.invokeLater(new Runnable()
                        {
                           @Override
                           public void run()
                           {
                              simPanel.repaint();
                           }
                        });
                     }
                  }
               }
               catch (Exception e)
               {
                  logger.error("{}", e);
               }
            }
         }, 0, (long) stepRate, TimeUnit.MICROSECONDS);
      }
   }

}
