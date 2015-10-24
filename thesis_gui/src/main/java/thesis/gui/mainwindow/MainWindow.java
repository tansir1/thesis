package thesis.gui.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import thesis.core.SimModel;
import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 * Contains the GUI application's main window frame.
 *
 */
public class MainWindow
{
   private JFrame frame;
   private RenderableSimWorldPanel simPanel;

   private Actions actions;

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
      actions = new Actions(frame, simPanel);

      MenuBar menuBar = new MenuBar(this, actions);
      frame.setJMenuBar(menuBar.getMenuBar());

      buildGUI();

      frame.pack();
      frame.setVisible(true);
   }

   private void buildGUI()
   {
      frame.setLayout(new BorderLayout());
      frame.add(simPanel, BorderLayout.CENTER);

      JPanel temp = new JPanel();
      temp.add(new JLabel("temp"));
      frame.add(temp, BorderLayout.LINE_START);

      JPanel statusPanel = new JPanel();
      statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
      statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

      statusPanel.add(new JLabel("Status"));

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
         // TODO Close external resources
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

   /**
    * Connect the event listeners of the GUI to the event triggers in the model.
    *
    * @param simModel
    *           The model to listen to and to render.
    */
   public void connectSimModel(SimModel simModel)
   {
      simPanel.connectSimModel(simModel);
   }
}
