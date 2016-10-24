package thesis.gui.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;
import thesis.core.SimModel;
import thesis.core.utilities.LoggerIDs;
import thesis.gui.mainwindow.actions.Actions;
import thesis.gui.mainwindow.tgtblfpan.TargetBeliefPanel;
import thesis.gui.mainwindow.uavstatpanel.UAVViewPanel;
import thesis.gui.simpanel.IMapMouseListener;
import thesis.gui.simpanel.MapMouseData;
import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 * Contains the GUI application's main window frame.
 *
 */
public class MainWindow implements IMapMouseListener, ISimGUIUpdater
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

   private JFrame frame;
   private RenderableSimWorldPanel simPanel;
   private UAVViewPanel uavViewPan;
   private SimStatusPanel simStatPan;
   private TargetBeliefPanel tgtBlfPan;
   
   private Actions actions;

   private JLabel statusLbl;

   private SimTimer simTimer;

   private ScheduledExecutorService execSvc;

   private SimRunner simRunner;

   public MainWindow()
   {
      simRunner = new SimRunner();

      frame = new JFrame("Thesis Simulator");
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClose();
         }
      });

      simPanel = new RenderableSimWorldPanel();
      uavViewPan = new UAVViewPanel();
      simStatPan = new SimStatusPanel();
      tgtBlfPan = new TargetBeliefPanel();
      simTimer = new SimTimer();
      actions = new Actions(frame, simPanel, simTimer);

      MenuBar menuBar = new MenuBar(this, actions);
      frame.setJMenuBar(menuBar.getMenuBar());

      statusLbl = new JLabel("");

      buildGUI();

      frame.pack();
      frame.setVisible(true);

      simPanel.getListenerSupport().addListener(this);
      execSvc = Executors.newSingleThreadScheduledExecutor();
   }

   private void buildGUI()
   {
      frame.setLayout(new BorderLayout());
      frame.add(simPanel, BorderLayout.CENTER);

      JPanel tab1 = new JPanel();
      tab1.setLayout(new MigLayout());
      //westPan.add(uavViewPan.getRenderable(), "spanx 3, wrap");
      tab1.add(uavViewPan.getRenderable(), "growy, wrap");
      tab1.add(simStatPan.getRenderable());
    
      JTabbedPane westPan = new JTabbedPane();
      westPan.addTab("Tab1", tab1);
      westPan.addTab("Tab2", tgtBlfPan.getRenderable());

      frame.add(buildToolbar(), BorderLayout.NORTH);
      frame.add(westPan, BorderLayout.WEST);

      JPanel statusPanel = new JPanel();
      statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
      statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
      statusPanel.add(statusLbl);

      frame.add(statusPanel, BorderLayout.SOUTH);
   }

   protected void onClose()
   {
      // TODO Check if there is data to save

      int userChoice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit Thesis Simulator?",
            "Confirm exit", JOptionPane.YES_NO_OPTION);
      if (userChoice == JOptionPane.YES_OPTION)
      {
         logger.info("Shutting down application.");

         execSvc.shutdown();
         frame.dispose();
         System.exit(0);
      }
   }

   /**
    * @param simCfg
    * @return True on successful initialization.
    */
   public boolean init(SimModel simModel)
   {
      boolean success = true;

      connectSimModel(simModel);

      success = simRunner.init(simModel, this);

      if(success)
      {
         //execSvc.submit(simRunner);
         execSvc.submit(new CatchableRunnerException(simRunner));
      }

      return success;
   }

   /**
    * Get a reference to the main window's frame for passing into dialogs.
    *
    * @return The main window's frame.
    */
   protected JFrame getParentFrame()
   {
      return frame;
   }

   private JToolBar buildToolbar()
   {
      JToolBar toolbar = new JToolBar();
      toolbar.setFloatable(false);
      toolbar.add(actions.getPlayAction());
      toolbar.add(actions.getPauseAction());
      toolbar.add(actions.getStepSimAction());
      toolbar.addSeparator();
      toolbar.add(actions.getPlay2HzAction());
      toolbar.add(actions.getPlay4HzAction());
      toolbar.add(actions.getPlay15HzAction());
      toolbar.add(actions.getPlay30HzAction());
      toolbar.add(actions.getPlay1000HzAction());
      toolbar.add(actions.getPlayCPUAction());
      toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
      return toolbar;
   }

   /**
    * Connect the event listeners of the GUI to the event triggers in the model.
    *
    * @param simModel
    *           The model to listen to and to render.
    */
   private void connectSimModel(SimModel simModel)
   {
      uavViewPan.connectSimModel(simModel, simPanel);
      simTimer.connectSimRunner(simRunner);
      simPanel.connectSimModel(simModel, actions);
      tgtBlfPan.connectSimModel(simModel);
   }

   protected SimStatusPanel getSimStatusPanel()
   {
      return simStatPan;
   }

   /**
    * Request that the sim panel repaint itself.
    */
   public void repaintSimPanel()
   {
      SwingUtilities.invokeLater(new Runnable()
      {

         @Override
         public void run()
         {
            simPanel.repaint();
         }
      });
   }

   @Override
   public void onMapMouseUpdate(MapMouseData event)
   {
      statusLbl.setText(event.toString());
      if(event.isClicked())
      {
         uavViewPan.onMapClick(event.getWorldCoordinate());
      }
   }

   @Override
   public void updateGUI(SimModel simModel)
   {
      try
      {
         //Block the sim thread until the render is complete
         SwingUtilities.invokeAndWait(new Runnable()
         {

            @Override
            public void run()
            {
               //logger.info("Render state update");
               simPanel.repaint();
               uavViewPan.update();
               simStatPan.update(simModel.getSimTimeState());
            }
         });
      }
      catch (Exception e)
      {
         logger.error("Failed to render sim GUI. Details: {}", e.getMessage());
      }
   }

   /**
    *This is a quick hack to debug exceptions thrown in the executor service.
    */
   private static class CatchableRunnerException implements Runnable
   {
      private Runnable runMe;

      public CatchableRunnerException(Runnable runMe)
      {
         this.runMe = runMe;
      }

      @Override
      public void run()
      {
         try
         {
            runMe.run();
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }
      }

   }

}
