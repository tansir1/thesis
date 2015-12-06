package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class UAVHistoryOptAction extends RenderOptAction
{
   public UAVHistoryOptAction()
   {
      super(RenderOption.UavHistoryTrail, "Flight Trail", true);
   }
}
