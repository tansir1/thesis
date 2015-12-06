package thesis.gui.mainwindow.actions.renderopts;

import thesis.core.world.RenderOptions.RenderOption;

@SuppressWarnings("serial")
public class SensorFOVOptAction extends RenderOptAction
{
   public SensorFOVOptAction()
   {
      super(RenderOption.SensorFOV, "Sensor FOV", false);
   }
}
