package thesis.gui.mainwindow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import thesis.core.SimModel;
import thesis.core.common.SimTime;
import thesis.core.utilities.ISimStepListener;

public class SimStatusPanel implements ISimStepListener
{
   private static final long UPDATE_GUI_RATE_MS = 500;
   private long updateTimeAccumulator;

   private JLabel totalSimTimeLbl;
   private JLabel totalWallTimeLbl;
   private JLabel simFPSLbl;
   private JPanel renderable;

   private long frameCnt;

   public SimStatusPanel()
   {
      totalSimTimeLbl = new JLabel();
      totalWallTimeLbl = new JLabel();
      simFPSLbl = new JLabel();
      renderable = new JPanel();
      updateTimeAccumulator = 0;

      renderable.setBorder(BorderFactory.createTitledBorder("Sim Status"));

      Dimension size = new Dimension(200, 100);
      renderable.setMinimumSize(size);
      renderable.setPreferredSize(size);

      buildGUI();

      // frameCnt is incremented by onSimulationStep() every frame. Every
      // second this ActionListener will fire, reset the counter, and update the
      // GUI.
      ActionListener fpsUpdater = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent evt)
         {
            simFPSLbl.setText(Long.toString(frameCnt));
            frameCnt = 0;
         }
      };
      new Timer(1000, fpsUpdater).start();
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

   public void connectSimModel(final SimModel simModel)
   {
      simModel.addStepListener(this);
   }

   @Override
   public void onSimulationStep()
   {
      frameCnt++;

      updateTimeAccumulator += SimTime.SIM_STEP_RATE_MS;
      if (updateTimeAccumulator > UPDATE_GUI_RATE_MS)
      {
         updateTimeAccumulator = 0;
         SwingUtilities.invokeLater(new Runnable()
         {

            @Override
            public void run()
            {
               long totalSimTime = SimTime.CURRENT_SIM_TIME_MS;
               int seconds = (int) (totalSimTime / 1000) % 60;
               int minutes = (int) ((totalSimTime / (1000 * 60)) % 60);
               int hours = (int) ((totalSimTime / (1000 * 60 * 60)) % 24);
               totalSimTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

               long totalWallTime = SimTime.getWallTime();
               seconds = (int) (totalWallTime / 1000) % 60;
               minutes = (int) ((totalWallTime / (1000 * 60)) % 60);
               hours = (int) ((totalWallTime / (1000 * 60 * 60)) % 24);
               totalWallTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
         });
      }
   }
}
