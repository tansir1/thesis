package thesis.gui.simpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import thesis.core.SimModel;
import thesis.core.world.CellCoordinate;
import thesis.core.world.WorldCoordinate;

@SuppressWarnings("serial")
public class RenderableSimWorldPanel extends JPanel
{
   private SimModel simModel;
   private MouseMoveListenerProxy mouseState;

   public RenderableSimWorldPanel()
   {
      Dimension minSz = new Dimension(640, 480);
      setMinimumSize(minSz);
      setPreferredSize(minSz);

      mouseState = new MouseMoveListenerProxy();
   }

   public void connectSimModel(SimModel simModel)
   {
      this.simModel = simModel;
      this.addMouseListener(mouseState);
      this.addMouseMotionListener(mouseState);
   }

   /**
    * Converts the given pixel coordinate into simulation world coordinates.
    * 
    * @param x
    *           The horizontal pixel location.
    * @param y
    *           The vertical pixel location.
    * @return The pixel location converted into a world coordinate.
    */
   private WorldCoordinate pixelsToWorldCoordinate(int x, int y)
   {
      double xPercent = (x * 1.0) / (1.0 * getWidth());
      double yPercent = (y * 1.0) / (1.0 * getHeight());

      double worldH = simModel.getWorld().getHeight();
      double worldW = simModel.getWorld().getWidth();

      // TODO Probably need to invert the y axis depending on where origin is
      // placed in sim model
      return new WorldCoordinate(yPercent * worldH, xPercent * worldW);
   }

   /**
    * Converts the given pixel coordinate into simulation cell coordinates.
    * 
    * @param x
    *           The horizontal pixel location.
    * @param y
    *           The vertical pixel location.
    * @return The pixel location converted into a cell coordinate.
    */
   private CellCoordinate pixelsToCellCoordinate(int x, int y)
   {
      final int numCols = simModel.getWorld().getColumnCount();
      final int numRows = simModel.getWorld().getRowCount();

      final int colW = (int) Math.round((getWidth() * 1.0) / (numCols * 1.0));
      final int rowH = (int) Math.round((getHeight() * 1.0) / (numRows * 1.0));      
      
      // TODO Probably need to invert the y axis depending on where origin is
      // placed in sim model
      return new CellCoordinate(y / rowH, x / colW);
   }

   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      // Wipe out previous rendering and draw the background
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0, 0, getWidth(), getHeight());

      // Draw nothing if the world isn't ready yet
      if (simModel != null)
      {
         drawGridLines(g2d);
         drawCoordinates(g2d);
      }
      else
      {
         g2d.setColor(Color.WHITE);
         g2d.drawString("Simulation initializing", getWidth() / 2, getHeight() / 2);
      }

   }

   private void drawGridLines(Graphics2D g2d)
   {
      final int pixH = getHeight() - 1;
      final int pixW = getWidth() - 1;

      g2d.drawRect(0, 0, pixW, pixH);
      final int numCols = simModel.getWorld().getColumnCount();
      final int numRows = simModel.getWorld().getRowCount();

      final int colW = (int) Math.round((pixW * 1.0) / (numCols * 1.0));
      final int rowH = (int) Math.round((pixH * 1.0) / (numRows * 1.0));

      g2d.setColor(Color.white);

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numCols; ++i)
      {
         g2d.drawLine(i * colW, 0, i * colW, pixH);
      }

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numRows; ++i)
      {
         g2d.drawLine(0, i * rowH, pixW, i * rowH);
      }
   }

   private void drawCoordinates(Graphics2D g2d)
   {
      if (mouseState.isMouseOverPanel())
      {
         int x = mouseState.getMouseX();
         int y = mouseState.getMouseY();

         WorldCoordinate wc = pixelsToWorldCoordinate(x, y);
         CellCoordinate cc = pixelsToCellCoordinate(x, y);
         
         String locationTxt = wc.toString() + " - " + cc.toString();
         g2d.setColor(Color.YELLOW);
         g2d.drawString(locationTxt, 5, getHeight() - 10);
      }
   }

   private class MouseMoveListenerProxy implements MouseMotionListener, MouseListener
   {
      private int curX;
      private int curY;
      private boolean mouseOver;

      public MouseMoveListenerProxy()
      {
         curX = -1;
         curY = -1;
         mouseOver = false;
      }

      public boolean isMouseOverPanel()
      {
         return mouseOver;
      }

      public int getMouseX()
      {
         return curX;
      }

      public int getMouseY()
      {
         return curY;
      }

      @Override
      public void mouseDragged(MouseEvent evt)
      {
         // Do nothing for now
      }

      @Override
      public void mouseMoved(MouseEvent evt)
      {
         curX = evt.getX();
         curY = evt.getY();
         RenderableSimWorldPanel.this.repaint();
      }

      @Override
      public void mouseClicked(MouseEvent evt)
      {
         // Do nothing for now
      }

      @Override
      public void mouseEntered(MouseEvent evt)
      {
         mouseOver = true;
      }

      @Override
      public void mouseExited(MouseEvent evt)
      {
         mouseOver = false;
      }

      @Override
      public void mousePressed(MouseEvent evt)
      {
         // Do nothing for now
      }

      @Override
      public void mouseReleased(MouseEvent evt)
      {
         // Do nothing for now
      }

   }
}
