package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class BeliefOptAction extends RenderOptAction
{
   public BeliefOptAction()
   {
      super(RenderOption.Belief, "Selected Belief", false);
   }
}
