package thesis.gui.mainwindow.uavstatpanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import thesis.core.SimModel;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.uav.UAV;
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

   public UAVViewPanel()
   {
      uavSelCB = new JComboBox<Integer>();

      renderable = new JPanel();
      renderable.setBorder(BorderFactory.createTitledBorder("UAVs"));

      Dimension size = new Dimension(200, 100);
      renderable.setMinimumSize(size);
      renderable.setPreferredSize(size);

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

      renderable.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.LINE_START;

      addGridFormRow(gbc, "Selected UAV:", uavSelCB);
      addGridFormRow(gbc, "North:", northLbl);
      addGridFormRow(gbc, "East:", eastLbl);
      addGridFormRow(gbc, "Heading:", hdgLbl);
      addGridFormRow(gbc, "Logic State:", logicStateLbl);
      addGridFormRow(gbc, "Target ID:", tgtIDLbl);

      gbc.gridwidth = 2;
      gbc.fill = GridBagConstraints.HORIZONTAL;

      JScrollPane wpnScroll = new JScrollPane(wpnsList);
      renderable.add(wpnScroll, gbc);
      gbc.gridy++;
      renderable.add(teleportBtn, gbc);

      gbc.gridwidth = 1;
   }

   private void addGridFormRow(GridBagConstraints gbc, String lblText, JComponent view)
   {
      renderable.add(new JLabel(lblText), gbc);
      gbc.gridx++;
      renderable.add(view, gbc);
      gbc.gridx = 0;
      gbc.gridy++;
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
