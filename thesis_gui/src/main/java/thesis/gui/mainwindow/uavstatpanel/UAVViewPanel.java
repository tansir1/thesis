package thesis.gui.mainwindow.uavstatpanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import thesis.core.common.WorldPose;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.UAVDump;
import thesis.gui.simpanel.RenderableSimWorldPanel;

public class UAVViewPanel
{
   private SimStateDump simModel;
   private JComboBox<Integer> uavSelCB;
   private JPanel renderable;

   private JLabel northLbl, eastLbl, hdgLbl;

   private RenderableSimWorldPanel renderSim;

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
      gbc.anchor = GridBagConstraints.LINE_START;

      addGridFormRow(gbc, "Selected UAV:", uavSelCB);
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

   public void connectSimModel(final SimStateDump simModel, RenderableSimWorldPanel renderSim)
   {
      this.simModel = simModel;
      this.renderSim = renderSim;

      synchronized (simModel)
      {
         for (UAVDump uav : simModel.getUAVs())
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

   public JComponent getRenderable()
   {
      return renderable;
   }

   public void update()
   {
      int selUAVID = (Integer) uavSelCB.getSelectedItem();

      synchronized (simModel)
      {
         for (UAVDump dump : simModel.getUAVs())
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

   private void updateSelectedUAVData(final UAVDump dump)
   {
      // This function runs on the EDT due to SwingUtilitiesInvokeLater() in
      // update()

      renderSim.getWorldRenderer().setSelectedUAV(dump.getID());

      synchronized (simModel)
      {
         WorldPose pose = dump.getPose();
         northLbl.setText(String.format("%5.2fm", pose.getCoordinate().getNorth()));
         eastLbl.setText(String.format("%5.2fm", pose.getCoordinate().getEast()));
         hdgLbl.setText(String.format("%.2f\u00B0", pose.getHeading()));
         // \u00B0 is unicode for degree symbol

      }
   }
}
