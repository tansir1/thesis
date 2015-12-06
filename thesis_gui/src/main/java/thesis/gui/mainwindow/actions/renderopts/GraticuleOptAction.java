package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class GraticuleOptAction extends RenderOptAction
{
   public GraticuleOptAction()
   {
      super(RenderOption.Graticule, "Graticule", true);
   }
}
