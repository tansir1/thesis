package thesis.gui.mainwindow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import thesis.network.messages.SimTimeMsg;

public class SimStatusPanel
{
   private JLabel totalSimTimeLbl;
   private JLabel totalWallTimeLbl;
   private JLabel simFPSLbl;
   private JPanel renderable;

   public SimStatusPanel()
   {
      totalSimTimeLbl = new JLabel();
      totalWallTimeLbl = new JLabel();
      simFPSLbl = new JLabel();
      renderable = new JPanel();

      renderable.setBorder(BorderFactory.createTitledBorder("Sim Status"));

      Dimension size = new Dimension(200, 100);
      renderable.setMinimumSize(size);
      renderable.setPreferredSize(size);

      buildGUI();
   }

   private void buildGUI()
   {
      renderable.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.LINE_START;

      addGridFormRow(gbc, "Sim Time:", totalSimTimeLbl);
      addGridFormRow(gbc, "Wall Time:", totalWallTimeLbl);
      addGridFormRow(gbc, "Sim FPS:", simFPSLbl);
   }

   private void addGridFormRow(GridBagConstraints gbc, String lblText, JComponent view)
   {
      renderable.add(new JLabel(lblText), gbc);
      gbc.gridx++;
      renderable.add(view, gbc);
      gbc.gridx = 0;
      gbc.gridy++;
   }

   public JComponent getRenderable()
   {
      return renderable;
   }

   public void update(final SimTimeMsg msg)
   {
      SwingUtilities.invokeLater(new Runnable()
      {

         @Override
         public void run()
         {
            long totalSimTime = msg.getSimTime();
            int seconds = (int) (totalSimTime / 1000) % 60;
            int minutes = (int) ((totalSimTime / (1000 * 60)) % 60);
            int hours = (int) ((totalSimTime / (1000 * 60 * 60)) % 24);
            totalSimTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            long totalWallTime = msg.getSimWallTime();
            seconds = (int) (totalWallTime / 1000) % 60;
            minutes = (int) ((totalWallTime / (1000 * 60)) % 60);
            hours = (int) ((totalWallTime / (1000 * 60 * 60)) % 24);
            totalWallTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            simFPSLbl.setText(Long.toString(msg.getFrameCount()));
         }
      });
   }
}
