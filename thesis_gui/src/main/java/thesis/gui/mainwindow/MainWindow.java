package thesis.gui.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.SimStateUpdateDump;
import thesis.core.utilities.LoggerIDs;
import thesis.gui.mainwindow.actions.Actions;
import thesis.gui.mainwindow.uavstatpanel.UAVViewPanel;
import thesis.gui.simpanel.IMapMouseListener;
import thesis.gui.simpanel.MapMouseData;
import thesis.gui.simpanel.RenderableSimWorldPanel;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.ShutdownMsg;

/**
 * Contains the GUI application's main window frame.
 *
 */
public class MainWindow implements IMapMouseListener
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
   private final int MSG_QUEUE_PROCESS_RATE_MS = 100;

   private JFrame frame;
   private SimStateDump simStateDump;
   private RenderableSimWorldPanel simPanel;
   private UAVViewPanel uavViewPan;
   private SimStatusPanel simStatPan;

   private Actions actions;

   private JLabel statusLbl;

   private SimTimer simTimer;

   private LinkedBlockingQueue<InfrastructureMsg> sendQ;
   private ScheduledExecutorService execSvc;

   public MainWindow()
   {
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

      JPanel westPan = new JPanel();
      westPan.setLayout(new BoxLayout(westPan, BoxLayout.Y_AXIS));
      westPan.add(uavViewPan.getRenderable());
      westPan.add(simStatPan.getRenderable());

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
      // TODO Confirm exit
      int userChoice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit Thesis Simulator?",
            "Confirm exit", JOptionPane.YES_NO_OPTION);
      if (userChoice == JOptionPane.YES_OPTION)
      {
         logger.info("Sending shutdown message to simulation.");
         sendQ.add(new ShutdownMsg());

         try
         {
            Thread.sleep(250);
         }
         catch (InterruptedException e)
         {
            logger.error("Interrupted while waiting for shutdown message to be sent.");
         }
         execSvc.shutdown();
         frame.dispose();
         System.exit(0);
      }
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
      toolbar.add(actions.getPlayCPUAction());
      toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
      return toolbar;
   }

   public void connectQueues(LinkedBlockingQueue<InfrastructureMsg> sendQ, LinkedBlockingQueue<InfrastructureMsg> recvQ)
   {
      this.sendQ = sendQ;
      simTimer.connectQueue(sendQ);
      execSvc.scheduleAtFixedRate(new RecvQConsumer(this, recvQ), 100, MSG_QUEUE_PROCESS_RATE_MS,
            TimeUnit.MILLISECONDS);
   }

   /**
    * Connect the event listeners of the GUI to the event triggers in the model.
    *
    * @param simModel
    *           The model to listen to and to render.
    */
   private void connectSimModel(SimStateDump simModel)
   {
      simPanel.connectSimModel(simModel, actions);
      uavViewPan.connectSimModel(simModel, simPanel);
      // simStatPan.connectSimModel(simModel);
      // simTimer.reset(simModel);
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
   }

   protected void onFullInitResponseMsg(SimStateDump simState, EntityTypeCfgs entTypeCfgs)
   {
      this.simStateDump = simState;
      //TODO EntityTypeConfigs?
      connectSimModel(simState);
   }

   protected void onSimStateUpdate(SimStateUpdateDump simUpdate)
   {
      if(simStateDump != null)
      {
         synchronized(simStateDump)
         {
            simStateDump.update(simUpdate);
         }
         logger.info("Render state update");
         simPanel.repaint();
         uavViewPan.update();
      }
   }

}
