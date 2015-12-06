package thesis.gui.mainwindow.actions.renderopts;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import thesis.core.world.RenderOptions;
import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class RenderOptAction extends AbstractAction
{
   private RenderOption actionOpt;
   private RenderOptions optsMgr;
   private boolean enabled;

   public RenderOptAction(final RenderOption option, String desc, boolean enabled)
   {
      this.actionOpt = option;
      putValue(NAME, desc);

      this.enabled = enabled;
   }

   public boolean isRenderOptEnabled()
   {
      return enabled;
   }

   public void connectToModel(final RenderOptions renderOpts)
   {
      if (renderOpts == null)
      {
         throw new NullPointerException("Render options cannot be null.");
      }

      this.optsMgr = renderOpts;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if (optsMgr != null)
      {
         enabled = !enabled;
         if (enabled)
         {
            optsMgr.setOption(actionOpt);
         }
         else
         {
            optsMgr.clearOption(actionOpt);
         }
      }
   }
}
