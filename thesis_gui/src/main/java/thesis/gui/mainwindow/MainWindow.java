package thesis.gui.mainwindow;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

      MenuBar menuBar = new MenuBar(this);
      frame.setJMenuBar(menuBar.getMenuBar());

      buildGUI();
      frame.pack();
      frame.setVisible(true);
   }

   private void buildGUI()
   {
      simPanel = new RenderableSimWorldPanel();
      frame.setLayout(new BorderLayout());
      frame.add(simPanel, BorderLayout.CENTER);
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
