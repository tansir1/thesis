package thesis.gui.simpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import thesis.core.SimModel;
import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldCoordinate;
import thesis.core.world.RenderWorld;

@SuppressWarnings("serial")
public class RenderableSimWorldPanel extends JPanel
{
   private MouseMoveListenerProxy mouseState;

   private RenderWorld renderWorld;

   public RenderableSimWorldPanel()
   {
      Dimension minSz = new Dimension(640, 480);
      setMinimumSize(minSz);
      setPreferredSize(minSz);

      mouseState = new MouseMoveListenerProxy();
      renderWorld = null;

      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e)
         {
            if (renderWorld != null)
            {
               renderWorld.setBounds(0, 0, getWidth(), getHeight());
            }
         }
      });
   }

   public void connectSimModel(SimModel simModel)
   {
      renderWorld = new RenderWorld(simModel.getWorld());
      this.addMouseListener(mouseState);
      this.addMouseMotionListener(mouseState);
      renderWorld.setBounds(0, 0, getWidth(), getHeight());
      repaint();
   }

   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      // Wipe out previous rendering and draw the background
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, getWidth(), getHeight());

      if (renderWorld != null)
      {
         renderWorld.render(g2d);
         drawCoordinates(g2d);
      }
      else
      {
         g2d.setColor(Color.BLACK);
         g2d.drawString("Simulation initializing", getWidth() / 2, getHeight() / 2);
      }

   }

   private void drawCoordinates(Graphics2D g2d)
   {
      if (mouseState.isMouseOverPanel())
      {
         int x = mouseState.getMouseX();
         int y = mouseState.getMouseY();

         WorldCoordinate wc = renderWorld.pixelsToWorldCoordinate(x, y);
         CellCoordinate cc = renderWorld.pixelsToCellCoordinate(x, y);

         String locationTxt = wc.toString() + " - " + cc.toString() + " - " + Integer.toString(x) + "," + Integer.toString(y);
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
