package thesis.gui.mainwindow;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import thesis.core.common.SimTimeState;

public class SimStatusPanel
{
   private JLabel totalSimTimeLbl;
   private JLabel totalWallTimeLbl;
   private JLabel simFrameCntLbl;
   private JPanel renderable;

   public SimStatusPanel()
   {
      totalSimTimeLbl = new JLabel();
      totalWallTimeLbl = new JLabel();
      simFrameCntLbl = new JLabel();
      renderable = new JPanel();

      renderable.setBorder(BorderFactory.createTitledBorder("Sim Status"));

      buildGUI();
   }

   private void buildGUI()
   {
      renderable.setLayout(new MigLayout());
      addGridFormRow("Sim Time:", totalSimTimeLbl);
      addGridFormRow("Wall Time:", totalWallTimeLbl);
      addGridFormRow("Sim Frame Count:", simFrameCntLbl);
   }

   private void addGridFormRow(String lblText, JComponent view)
   {
      renderable.add(new JLabel(lblText));
      renderable.add(view, "wrap");
   }

   public JComponent getRenderable()
   {
      return renderable;
   }

   public void update(final SimTimeState timeState)
   {

      long totalSimTime = timeState.getSimTime();
      int seconds = (int) (totalSimTime / 1000) % 60;
      int minutes = (int) ((totalSimTime / (1000 * 60)) % 60);
      int hours = (int) ((totalSimTime / (1000 * 60 * 60)) % 24);
      totalSimTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

      long totalWallTime = timeState.getWallTime();
      seconds = (int) (totalWallTime / 1000) % 60;
      minutes = (int) ((totalWallTime / (1000 * 60)) % 60);
      hours = (int) ((totalWallTime / (1000 * 60 * 60)) % 24);
      totalWallTimeLbl.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

      simFrameCntLbl.setText(Long.toString(timeState.getFrameCount()));

   }
}
