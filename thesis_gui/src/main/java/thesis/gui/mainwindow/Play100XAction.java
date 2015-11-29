package thesis.gui.mainwindow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import thesis.core.utilities.CoreUtils;
import thesis.gui.utilities.GuiRsrcPaths;

@SuppressWarnings("serial")
public class Play100XAction extends AbstractAction
{
   private SimTimer simTimer;

   public Play100XAction(SimTimer simTimer)
   {
      if(simTimer == null)
      {
         throw new NullPointerException("Sim timer cannot be null.");
      }
      this.simTimer = simTimer;

      putValue(SHORT_DESCRIPTION, "Run the simulation at 100X.");

      Icon icon = new ImageIcon(CoreUtils.getResourceAsImage(GuiRsrcPaths.RUN_100X_IMG_PATH));
      putValue(LARGE_ICON_KEY, icon);
   }

   @Override
   public void actionPerformed(ActionEvent arg0)
   {
      simTimer.run(100);
   }

}
