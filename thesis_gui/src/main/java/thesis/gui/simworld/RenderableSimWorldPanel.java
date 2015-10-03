package thesis.gui.simworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import thesis.core.world.World;

@SuppressWarnings("serial")
class RenderableSimWorldPanel extends JPanel
{
   private World world;
   
   public RenderableSimWorldPanel()
   {
      Dimension minSz = new Dimension(640, 480);
      setMinimumSize(minSz);
      setPreferredSize(minSz);
   }
   
   public void setWorld(World world)
   {
      this.world = world;
   }
   
   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;

      //Wipe out previous rendering and draw the background
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0, 0, getWidth(), getHeight());

      //Draw nothing if the world isn't ready yet
      if(world == null)
      {
         g2d.setColor(Color.WHITE);
         g2d.drawString("World initializing", getWidth()/2, getHeight()/2);
      }
      else
      {
         drawGridLines(g2d);
      }

   }
   
   private void drawGridLines(Graphics2D g2d)
   {
      final int pixH = getHeight() - 1;
      final int pixW = getWidth() - 1;
      
      g2d.drawRect(0, 0, pixW, pixH);
      final int numCols = world.getColumnCount();
      final int numRows = world.getRowCount();
      
      final int colW = (int)Math.round((pixW * 1.0) / (numCols * 1.0));
      final int rowH = (int)Math.round((pixH * 1.0) / (numRows * 1.0));
      
      //0th border line is handled by the border rectangle
      for(int i=1; i<numCols; ++i)
      {
         g2d.drawLine(i*colW, 0, i*colW, pixH);
      }
      
      //0th border line is handled by the border rectangle
      for(int i=1; i<numRows; ++i)
      {
         g2d.drawLine(0, i*rowH, pixW, i*rowH);
      }
   }
}
