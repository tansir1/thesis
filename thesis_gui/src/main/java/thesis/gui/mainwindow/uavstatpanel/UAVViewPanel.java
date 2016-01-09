package thesis.gui.mainwindow.uavstatpanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.common.WorldPose;
import thesis.core.entities.uav.UAV;
import thesis.core.utilities.ISimStepListener;

public class UAVViewPanel implements ISimStepListener
{
   private static final long UPDATE_GUI_RATE_MS = 500;

   private SimModel simModel;
   private JComboBox<Integer> uavSelCB;
   private JPanel renderable;

   private JLabel northLbl, eastLbl, hdgLbl;

   private long updateTimeAccumulator;

   public UAVViewPanel()
   {
      uavSelCB = new JComboBox<Integer>();

      renderable = new JPanel();
      renderable.setBorder(BorderFactory.createTitledBorder("UAVs"));

      Dimension size = new Dimension(200, 100);
      renderable.setMinimumSize(size);
      renderable.setPreferredSize(size);

      updateTimeAccumulator = 0;

      buildGUI();
   }

   private void buildGUI()
   {
      northLbl = new JLabel();
      eastLbl = new JLabel();
      hdgLbl = new JLabel();

      renderable.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridwidth = 2;

      renderable.add(uavSelCB, gbc);
      gbc.gridy++;
      gbc.gridwidth = 1;
      gbc.anchor = GridBagConstraints.LINE_START;

      addGridFormRow(gbc, "North:", northLbl);
      addGridFormRow(gbc, "East:", eastLbl);
      addGridFormRow(gbc, "Heading:", hdgLbl);
   }


   private void addGridFormRow(GridBagConstraints gbc, String lblText, JComponent view)
   {
      renderable.add(new JLabel(lblText), gbc);
      gbc.gridx++;
      renderable.add(view, gbc);
      gbc.gridx = 0;
      gbc.gridy++;
   }

   public void connectSimModel(final SimModel simModel)
   {
      this.simModel = simModel;
      for(UAV uav : simModel.getUAVManager().getAllUAVs())
      {
         uavSelCB.addItem(uav.getID());
      }

      if(uavSelCB.getModel().getSize() > 0)
      {
         uavSelCB.setSelectedIndex(0);
         update();
      }
      simModel.addStepListener(this);
   }

   public JComponent getRenderable()
   {
      return renderable;
   }

   public void update()
   {
      int selUAV = (Integer)uavSelCB.getSelectedItem();
      final UAV uav = simModel.getUAVManager().getUAV(selUAV);

      SwingUtilities.invokeLater(new Runnable()
      {

         @Override
         public void run()
         {
            WorldPose pose = uav.getPathing().getPose();
            northLbl.setText(String.format("%5.2fm", pose.getCoordinate().getNorth()));
            eastLbl.setText(String.format("%5.2fm", pose.getCoordinate().getEast()));
            // \u00B0 is unicode for degree symbol
            hdgLbl.setText(String.format("%.2f\u00B0", pose.getHeading()));

         }
      });
   }

   @Override
   public void onSimulationStep()
   {
      updateTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
      if(updateTimeAccumulator > UPDATE_GUI_RATE_MS)
      {
         update();
         updateTimeAccumulator = 0;
      }
   }
}
