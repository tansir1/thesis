package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class CommsRangeOptAction extends RenderOptAction
{
   public CommsRangeOptAction()
   {
      super(RenderOption.CommsRange, "UAV Comms Range", false);
   }
}
