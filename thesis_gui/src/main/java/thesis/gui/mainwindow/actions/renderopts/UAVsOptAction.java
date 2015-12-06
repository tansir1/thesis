package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class UAVsOptAction extends RenderOptAction
{
   public UAVsOptAction()
   {
      super(RenderOption.UAVs, "UAV", true);
   }
}
