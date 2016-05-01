package thesis.gui.mainwindow.uavstatpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.uav.UAV;
import thesis.core.uav.logic.TaskAllocator;
import thesis.core.uav.logic.TaskType;
import thesis.core.weapons.Weapon;
import thesis.gui.simpanel.RenderableSimWorldPanel;

public class UAVViewPanel
{
   private SimModel simModel;
   private JComboBox<Integer> uavSelCB;
   private JPanel renderable;

   private JLabel northLbl, eastLbl, hdgLbl;

   private RenderableSimWorldPanel renderSim;

   private String teleportOnTxt;
   private String teleportOffTxt;
   private JButton teleportBtn;
   private boolean teleportInProgress;
   private JLabel logicStateLbl;
   private JLabel tgtIDLbl;

   private JList<String> wpnsList;
   private DefaultListModel<String> wpnsListModel;

   private JLabel bestAttackTgtLbl, bestMonitorTgtLbl;
   private JLabel bestAttackTgtBidLbl, bestMonitorTgtBidLbl;

   public UAVViewPanel()
   {
      uavSelCB = new JComboBox<Integer>();

      renderable = new JPanel();
      renderable.setBorder(BorderFactory.createTitledBorder("UAVs"));

      /*
      Dimension size = new Dimension(200, 100);
      renderable.setMinimumSize(size);
      renderable.setPreferredSize(size);*/

      uavSelCB.addItemListener(new ItemListener()
      {

         @Override
         public void itemStateChanged(ItemEvent e)
         {
            update();
            if (renderSim != null)
            {
               renderSim.repaint();
            }
         }
      });

      teleportInProgress = false;
      teleportOnTxt = "Teleport to Click";
      teleportOffTxt = "Teleport - cancel";
      teleportBtn = new JButton(teleportOnTxt);
      teleportBtn.addActionListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            toggleTeleportStatus();
         }
      });

      wpnsListModel = new DefaultListModel<String>();

      buildGUI();
   }

   private void buildGUI()
   {
      northLbl = new JLabel();
      eastLbl = new JLabel();
      hdgLbl = new JLabel();
      logicStateLbl = new JLabel();
      tgtIDLbl = new JLabel();
      wpnsList = new JList<String>(wpnsListModel);

      bestAttackTgtLbl = new JLabel();
      bestMonitorTgtLbl = new JLabel();
      bestAttackTgtBidLbl = new JLabel();
      bestMonitorTgtBidLbl = new JLabel();

      renderable.setLayout(new MigLayout(new LC().fill()));

      addGridFormRow("Selected UAV:", uavSelCB);
      addGridFormRow("North:", northLbl);
      addGridFormRow("East:", eastLbl);
      addGridFormRow("Heading:", hdgLbl);
      addGridFormRow("Logic State:", logicStateLbl);
      addGridFormRow("Target ID:", tgtIDLbl);
      addGridFormRow("Best Attack Tgt:", bestAttackTgtLbl);
      addGridFormRow("Best Attack Bid:", bestAttackTgtBidLbl);
      addGridFormRow("Best Monitor Tgt:", bestMonitorTgtLbl);
      addGridFormRow("Best Monitor Bid:", bestMonitorTgtBidLbl);

      renderable.add(teleportBtn, "spanx 2, wrap");

      JScrollPane wpnScroll = new JScrollPane(wpnsList);
      renderable.add(wpnScroll, "spany 2, wrap");
   }

   private void addGridFormRow(String lblText, JComponent view)
   {
      renderable.add(new JLabel(lblText));
      renderable.add(view, "wrap");
   }

   public void connectSimModel(final SimModel simModel, RenderableSimWorldPanel renderSim)
   {
      this.simModel = simModel;
      this.renderSim = renderSim;

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

   public void onMapClick(WorldCoordinate wc)
   {
      if (teleportInProgress)
      {
         toggleTeleportStatus();

         int selUAVID = (Integer) uavSelCB.getSelectedItem();

         synchronized (simModel)
         {
            for (UAV uav : simModel.getUAVManager().getAllUAVs())
            {
               if (uav.getID() == selUAVID)
               {
                  uav.getPathing().teleportTo(wc);
                  break;
               }
            }
         }

      }
   }

   private void toggleTeleportStatus()
   {
      if (teleportInProgress)
      {
         teleportInProgress = false;
         teleportBtn.setText(teleportOnTxt);
      }
      else
      {
         teleportInProgress = true;
         teleportBtn.setText(teleportOffTxt);
      }
   }

   public JComponent getRenderable()
   {
      return renderable;
   }

   public void update()
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
      // This function runs on the EDT due to SwingUtilitiesInvokeLater() in
      // update()

      renderSim.getWorldRenderer().setSelectedUAV(selectedUAV.getID());

      synchronized (simModel)
      {
         wpnsListModel.clear();
         wpnsListModel.addElement("WPN_TYPE, QTY");
         for(Weapon wpn : selectedUAV.getWeapons().getWeapons())
         {
            wpnsListModel.addElement(wpn.toString());
         }

         TaskAllocator allocator = selectedUAV.getLogic().getTaskAllocator();
         if(allocator.getBestAttackTarget() != null)
         {
            int id = allocator.getBestAttackTarget().getTrueTargetID();
            int bid = allocator.getBestAttackTargetBid();

            bestAttackTgtLbl.setText(Integer.toString(id));
            bestAttackTgtBidLbl.setText(Integer.toString(bid));
         }
         else
         {
            bestAttackTgtLbl.setText("NULL");
            bestAttackTgtBidLbl.setText("NULL");
         }

         if(allocator.getBestMonitorTarget() != null)
         {
            int id = allocator.getBestMonitorTarget().getTrueTargetID();
            int bid = allocator.getBestMonitorTargetBid();

            bestMonitorTgtLbl.setText(Integer.toString(id));
            bestMonitorTgtBidLbl.setText(Integer.toString(bid));
         }
         else
         {
            bestMonitorTgtLbl.setText("NULL");
            bestMonitorTgtBidLbl.setText("NULL");
         }

         WorldPose pose = selectedUAV.getPathing().getPose();
         northLbl.setText(String.format("%5.2fm", pose.getCoordinate().getNorth()));
         eastLbl.setText(String.format("%5.2fm", pose.getCoordinate().getEast()));
         hdgLbl.setText(String.format("%.2f\u00B0", pose.getHeading()));
         // \u00B0 is unicode for degree symbol

         TaskType taskType = selectedUAV.getLogic().getCurrentTaskType();
         if(taskType != null)
         {
            logicStateLbl.setText(taskType.toString());
         }
         else
         {
            logicStateLbl.setText("----");
         }

         if(selectedUAV.getLogic().getCurrentTarget() != null)
         {
            tgtIDLbl.setText(Integer.toString(selectedUAV.getLogic().getCurrentTarget().getTrueTargetID()));
         }
         else
         {
            tgtIDLbl.setText("---");
         }
      }
   }
}
