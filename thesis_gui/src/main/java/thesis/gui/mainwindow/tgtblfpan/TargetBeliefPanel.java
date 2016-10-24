package thesis.gui.mainwindow.tgtblfpan;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import thesis.core.SimModel;
import thesis.core.belief.TargetBelief;
import thesis.core.belief.TargetTaskStatus;
import thesis.core.uav.UAV;

public class TargetBeliefPanel
{

   private SimModel simModel;
   private JComboBox<Integer> uavSelCB;
   private JPanel renderable;
   
   private JList<String> tgtList;
   private DefaultListModel<String> tgtListModel;
   
   public TargetBeliefPanel()
   {
      uavSelCB = new JComboBox<Integer>();

      renderable = new JPanel();
      renderable.setBorder(BorderFactory.createTitledBorder("Target Task States"));


      uavSelCB.addItemListener(new ItemListener()
      {

         @Override
         public void itemStateChanged(ItemEvent e)
         {
            update();
         }
      });
      
      tgtListModel = new DefaultListModel<String>();
      
      buildGUI();
   }
   
   public JComponent getRenderable()
   {
      return renderable;
   }
   
   private void buildGUI()
   {
      tgtList = new JList<String>(tgtListModel);
      renderable.setLayout(new MigLayout(new LC().fill()));

      renderable.add(new JLabel("Selected UAV:"));
      renderable.add(uavSelCB, "wrap");
      
      JScrollPane tgtScroll = new JScrollPane(tgtList);
      renderable.add(tgtScroll, "grow, spanx 2, spany 5, wrap");
      //renderable.add(tgtScroll, "grow, wrap");
   }
     
   public void connectSimModel(final SimModel simModel)
   {
      this.simModel = simModel;

      synchronized (simModel)
      {
         for (UAV uav : simModel.getUAVManager().getAllUAVs())
         {
            uavSelCB.addItem(uav.getID());
         }
      }

      if (uavSelCB.getModel().getSize() > 0)
      {
         uavSelCB.setSelectedIndex(0);
         update();
      }
   }
   
   private void update()
   {
      int selUAVID = (Integer) uavSelCB.getSelectedItem();

      synchronized (simModel)
      {
         for (UAV dump : simModel.getUAVManager().getAllUAVs())
         {
            if (dump.getID() == selUAVID)
            {
               SwingUtilities.invokeLater(new Runnable()
               {

                  @Override
                  public void run()
                  {
                     updateSelectedUAVData(dump);
                  }
               });
               break;
            }
         }
      }
   }
   
   private void updateSelectedUAVData(final UAV selectedUAV)
   {
      synchronized (simModel)
      {
         tgtListModel.clear();
         tgtListModel.addElement("Key: Value");
         for(TargetBelief tb : selectedUAV.getBelief().getTargetBeliefs())
         {
            tgtListModel.addElement("---------");
            tgtListModel.addElement("Tgt: " + Integer.toString(tb.getTrueTargetID()));
            
            TargetTaskStatus tts = tb.getTaskStatus();
            tgtListModel.addElement("Attack State: " + tts.getAttackState());
            tgtListModel.addElement("Attack UAV: " + tts.getAttackUAV());
            tgtListModel.addElement("Attack score: " + tts.getAttackUAVScore());
            tgtListModel.addElement("Attack timestamp: " + tts.getAttackUpdateTimestamp());
            tgtListModel.addElement("Monitor state: " + tts.getMonitorState());
            tgtListModel.addElement("Monitor UAV: " + tts.getMonitorUAV());
            tgtListModel.addElement("Monitor score: " + tts.getMonitorUAVScore());
            tgtListModel.addElement("Monitor timestamp: " + tts.getMonitorUpdateTimestamp());
         }
      }
   }
   
}
