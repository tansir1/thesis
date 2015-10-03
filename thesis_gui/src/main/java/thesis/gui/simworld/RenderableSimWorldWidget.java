package thesis.gui.simworld;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class RenderableSimWorldWidget
{
   //private JComponent renderable;
   private RenderableSimWorldPanel simPanel;
   
   public RenderableSimWorldWidget()
   {
      simPanel = new RenderableSimWorldPanel();
      simPanel.setBorder(BorderFactory.createTitledBorder("Sim World"));
   }
   
   public JComponent getRenderable()
   {
      return simPanel;
   }

}
