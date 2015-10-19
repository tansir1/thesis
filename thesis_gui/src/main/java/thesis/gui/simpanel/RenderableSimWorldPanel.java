package thesis.gui.simpanel;

import java.awt.BasicStroke;
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
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.DirectedEdge;

@SuppressWarnings("serial")
public class RenderableSimWorldPanel extends JPanel
{
   private SimModel simModel;
   private MouseMoveListenerProxy mouseState;

   /**
    * The width of grid cells in pixels.
    */
   private int gridCellW;

   /**
    * The height of grid cells in pixels.
    */
   private int gridCellH;

   private double pixelsPerMeterH;

   private double pixelsPerMeterW;

   public RenderableSimWorldPanel()
   {
      Dimension minSz = new Dimension(640, 480);
      setMinimumSize(minSz);
      setPreferredSize(minSz);

      mouseState = new MouseMoveListenerProxy();

      this.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            recomputeCellPixelSize();
         }
      });
      recomputeCellPixelSize();
   }

   public void connectSimModel(SimModel simModel)
   {
      this.simModel = simModel;
      this.addMouseListener(mouseState);
      this.addMouseMotionListener(mouseState);
      recomputeCellPixelSize();
      repaint();
   }

   private void recomputeCellPixelSize()
   {
      if (simModel != null)
      {
         int pixW = getWidth() - 1;
         int pixH = getHeight() - 1;

         int numCols = simModel.getWorld().getColumnCount();
         int numRows = simModel.getWorld().getRowCount();

         gridCellW = (int) Math.round((pixW * 1.0) / (numCols * 1.0));
         gridCellH = (int) Math.round((pixH * 1.0) / (numRows * 1.0));

         pixelsPerMeterH = (pixH * 1.0) / simModel.getWorld().getHeight().asMeters();
         pixelsPerMeterW = (pixW * 1.0) / simModel.getWorld().getWidth().asMeters();
      }
      else
      {
         gridCellW = 1;
         gridCellH = 1;
         pixelsPerMeterH = 1;
         pixelsPerMeterW = 1;
      }
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

      Distance worldH = new Distance(simModel.getWorld().getHeight());
      Distance worldW = new Distance(simModel.getWorld().getWidth());

      // TODO Probably need to invert the y axis depending on where origin is
      // placed in sim model
      worldH.scale(yPercent);
      worldW.scale(xPercent);
      return new WorldCoordinate(worldH, worldW);
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
      // TODO Probably need to invert the y axis depending on where origin is
      // placed in sim model
      return new CellCoordinate(y / gridCellH, x / gridCellW);
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
         drawRoads(g2d);
         drawHavens(g2d);
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

      //White, half alpha
      g2d.setColor(new Color(255, 255, 255, 127));
      g2d.drawRect(0, 0, pixW, pixH);

      final int numCols = simModel.getWorld().getColumnCount();
      final int numRows = simModel.getWorld().getRowCount();

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numCols; ++i)
      {
         g2d.drawLine(i * gridCellW, 0, i * gridCellW, pixH);
      }

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numRows; ++i)
      {
         g2d.drawLine(0, i * gridCellH, pixW, i * gridCellH);
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

   /**
    * Draws a rectangle that fills half the grid cell to represent a safe haven
    * location.
    *
    * @param g2d
    *           Draw on this graphics object.
    */
   private void drawHavens(Graphics2D g2d)
   {
      final int quarterGridW = (int) (gridCellW * 0.25);
      final int quarterGridH = (int) (gridCellH * 0.25);
      final int thirdGridW = (int) (gridCellW * 0.33);
      final int thirdGridH = (int) (gridCellH * 0.33);
      final int halfGridW = (int) (gridCellW * 0.5);
      final int halfGridH = (int) (gridCellH * 0.5);

      for (WorldCoordinate wc : simModel.getWorld().getHavenLocations())
      {
         CellCoordinate cell = simModel.getWorld().convertWorldToCell(wc);

         g2d.setColor(Color.ORANGE);
         int x = gridCellW * cell.getColumn();
         int y = gridCellH * cell.getRow();
         g2d.fillRect(x + quarterGridW, y + quarterGridH, halfGridW, halfGridH);

         g2d.setColor(Color.black);
         // Left vertical line of H
         g2d.drawLine(x + thirdGridW, y + thirdGridH, x + thirdGridW, y + thirdGridH + thirdGridH);
         // Right vertical line of H
         g2d.drawLine(x + thirdGridW + thirdGridW, y + thirdGridH, x + thirdGridW + thirdGridW,
               y + thirdGridH + thirdGridH);
         // Horizontal line of H
         g2d.drawLine(x + thirdGridW, y + halfGridH, x + thirdGridW + thirdGridW, y + halfGridH);
      }
   }

   private void drawRoads(Graphics2D g2d)
   {
      g2d.setColor(Color.pink);
      g2d.setStroke(new BasicStroke(3f));

      for(DirectedEdge<WorldCoordinate> edge : simModel.getWorld().getRoadNetwork().getEdges())
      {
         WorldCoordinate start = edge.getStartVertex().getUserData();
         WorldCoordinate end = edge.getEndVertex().getUserData();

         int xStart = (int)(pixelsPerMeterW * start.getEast().asMeters());
         int yStart = (int)(pixelsPerMeterH * start.getNorth().asMeters());

         int xEnd = (int)(pixelsPerMeterW * end.getEast().asMeters());
         int yEnd = (int)(pixelsPerMeterH * end.getNorth().asMeters());

         g2d.drawLine(xStart, yStart, xEnd, yEnd);
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
