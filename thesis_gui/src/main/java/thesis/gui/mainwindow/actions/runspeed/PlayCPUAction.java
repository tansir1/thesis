package thesis.gui.mainwindow.actions.runspeed;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import thesis.core.utilities.CoreUtils;
import thesis.gui.mainwindow.SimTimer;
import thesis.gui.utilities.GuiRsrcPaths;

@SuppressWarnings("serial")
public class PlayCPUAction extends AbstractAction
{
   private SimTimer simTimer;

   public PlayCPUAction(SimTimer simTimer)
   {
      if(simTimer == null)
      {
         throw new NullPointerException("Sim timer cannot be null.");
      }
      this.simTimer = simTimer;

      putValue(SHORT_DESCRIPTION, "Run the simulation at CPU speed.");

      Icon icon = new ImageIcon(CoreUtils.getResourceAsImage(GuiRsrcPaths.RUN_CPU_IMG_PATH));
      putValue(LARGE_ICON_KEY, icon);
   }

   @Override
   public void actionPerformed(ActionEvent arg0)
   {
      simTimer.run(0);
   }

}
