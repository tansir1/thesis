package thesis.core.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import thesis.core.SimModel;
import thesis.core.common.CellCoordinate;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.DirectedEdge;
import thesis.core.entities.Target;
import thesis.core.entities.UAV;
import thesis.core.utilities.CoreRsrcPaths;
import thesis.core.utilities.CoreUtils;

public class RenderSimState
{
   /**
    * Pixel size of the square that represents road intersections (road graph
    * vertices) is proportional to the percentage of the min(gridCellW,
    * gridCellH).
    */
   private static final float INTERSECTION_SZ_VS_GRID_PERCENT = 0.2f;

   /**
    * The pixel width of roads is proportional to the percentage of the
    * min(gridCellW, gridCellH).
    */
   private static final float ROAD_WIDTH_VS_GRID_PERCENT = 0.1f;

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

   /**
    * Stroke for drawing roads. Proportional to the size of grid cells.
    *
    * @see #ROAD_WIDTH_VS_GRID_PERCENT
    */
   private BasicStroke roadStroke;

   private SimModel model;

   private Rectangle bounds;

   /**
    * Pixel size of the square representing road intersections (graph vertices).
    */
   private int roadInterSectionSz;

   private BufferedImage rawHavenImg;
   private BufferedImage scaledHavenImg;

   private BufferedImage rawRedMobileImg;
   private BufferedImage scaledRedMobileImg;

   private BufferedImage rawRedStaticImg;
   private BufferedImage scaledRedStaticImg;

   private BufferedImage rawBlueMobileImg;
   private BufferedImage scaledBlueMobileImg;

   /**
    * Initialize a renderer with a bounds size of zero.
    *
    * This world cannot be correctly rendered until the rendering size is set
    * via {@link #setBounds(int, int, int, int)}.
    *
    * @param model
    *           The model to render.
    */
   public RenderSimState(SimModel model)
   {
      if (model == null)
      {
         throw new NullPointerException("SimModel to render cannot be null.");
      }

      this.model = model;
      bounds = new Rectangle();
      roadStroke = new BasicStroke(1f);
      rawHavenImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.HAVEN_IMG_PATH);
      rawRedMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_MOBILE_IMG_PATH);
      rawRedStaticImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_STATIC_IMG_PATH);
      rawBlueMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.BLUE_MOBILE_IMG_PATH);
   }

   /**
    * Initialize a renderer with the specified bounds size.
    *
    * @param render
    *           The model to render.
    * @param minX
    *           The minimum X pixel coordinate for the world will start here.
    *           This allows the user to offset the rendering.
    * @param minY
    *           The minimum Y pixel coordinate for the world will start here.
    *           This allows the user to offset the rendering.
    * @param width
    *           The width of the rendering space in pixels.
    * @param height
    *           The height of the rendering space in pixels.
    */
   public RenderSimState(SimModel model, int minX, int minY, int width, int height)
   {
      if (model == null)
      {
         throw new NullPointerException("SimModel to render cannot be null.");
      }

      this.model = model;
      bounds = new Rectangle();
      roadStroke = new BasicStroke(1f);
      rawHavenImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.HAVEN_IMG_PATH);
      rawRedMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_MOBILE_IMG_PATH);
      rawRedStaticImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_STATIC_IMG_PATH);
      rawBlueMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.BLUE_MOBILE_IMG_PATH);
   }

   /**
    * Set the pixel space for rendering the world.
    *
    * @param minX
    *           The minimum X pixel coordinate for the world will start here.
    *           This allows the user to offset the rendering.
    * @param minY
    *           The minimum Y pixel coordinate for the world will start here.
    *           This allows the user to offset the rendering.
    * @param width
    *           The width of the rendering space in pixels.
    * @param height
    *           The height of the rendering space in pixels.
    */
   public void setBounds(int minX, int minY, int width, int height)
   {
      bounds.x = minX;
      bounds.y = minY;
      bounds.width = width;
      bounds.height = height;
      recomputeScalingSizes();
   }

   /**
    *
    * @return The current width of the rendering space in pixels.
    */
   public int getRenderWidth()
   {
      return bounds.width;
   }

   /**
    * @return The current height of the rendering space in pixels.
    */
   public int getRenderHeight()
   {
      return bounds.height;
   }

   /**
    * Render the world to the given graphics device.
    *
    * @param gfx
    *           The world will be rendered into this graphics device.
    */
   public void render(Graphics2D gfx)
   {
      if (gfx == null)
      {
         throw new NullPointerException("Graphics to paint with cannot be null.");
      }

      // gfx.translate(0.0, -bounds.height);// Move the origin to the lower left
      // gfx.scale(1.0, 1.0);//Make moving up the screen positive

      // Clear the image canvas
      gfx.setColor(Color.BLACK);
      gfx.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

      // Increase the default font size
      Font currentFont = gfx.getFont();
      Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4f);
      gfx.setFont(newFont);

      drawGridLines(gfx);
      drawRoads(gfx);
      drawHavens(gfx);
      drawTargets(gfx);
      drawUAVs(gfx);
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
   public WorldCoordinate pixelsToWorldCoordinate(int x, int y)
   {
      // Invert the y axis so that "north" is at the top of the screen.
      y = bounds.height - y;

      final double xPercent = (x * 1.0) / (1.0 * bounds.width);
      final double yPercent = (y * 1.0) / (1.0 * bounds.height);

      final Distance worldH = new Distance(model.getWorld().getHeight());
      final Distance worldW = new Distance(model.getWorld().getWidth());

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
   public CellCoordinate pixelsToCellCoordinate(int x, int y)
   {
      // Invert the y axis so that "north" is at the top of the screen.
      final int rows = model.getWorld().getRowCount() - 1;
      final int rawRow = y / gridCellH;
      final int invertedRows = rows - rawRow;
      return new CellCoordinate(invertedRows, x / gridCellW);
   }

   public int WorldCoordinateToYPixel(final WorldCoordinate wc)
   {
      final double meters = wc.getNorth().asMeters();
      int y = (int) (pixelsPerMeterH * meters);
      y = bounds.height - y;
      return y;
   }

   private void recomputeScalingSizes()
   {
      int pixW = bounds.width - 1;
      int pixH = bounds.height - 1;

      int numCols = model.getWorld().getColumnCount();
      int numRows = model.getWorld().getRowCount();

      gridCellW = (int) Math.round((pixW * 1.0) / (numCols * 1.0));
      gridCellH = (int) Math.round((pixH * 1.0) / (numRows * 1.0));

      pixelsPerMeterH = (pixH * 1.0) / model.getWorld().getHeight().asMeters();
      pixelsPerMeterW = (pixW * 1.0) / model.getWorld().getWidth().asMeters();

      roadStroke = new BasicStroke(Math.min(gridCellH, gridCellW) * ROAD_WIDTH_VS_GRID_PERCENT);
      roadInterSectionSz = (int) (Math.min(gridCellH, gridCellW) * INTERSECTION_SZ_VS_GRID_PERCENT);

      if (rawHavenImg != null)
      {
         scaledHavenImg = new BufferedImage(roadInterSectionSz, roadInterSectionSz, BufferedImage.TYPE_INT_ARGB);

         Graphics g = scaledHavenImg.createGraphics();
         g.drawImage(rawHavenImg, 0, 0, roadInterSectionSz, roadInterSectionSz, null);
         g.dispose();
      }

      if (rawRedMobileImg != null)
      {
         scaledRedMobileImg = rawRedMobileImg;
         // scaledRedMobileImg = new BufferedImage(rawRedMobileImg.getWidth()*2,
         // rawRedMobileImg.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
         //
         // Graphics g = scaledRedMobileImg.createGraphics();
         // g.drawImage(rawRedMobileImg, 0, 0, rawRedMobileImg.getWidth()*2,
         // rawRedMobileImg.getHeight()*2, null);
         // g.dispose();
      }

      if (rawRedStaticImg != null)
      {
         scaledRedStaticImg = rawRedStaticImg;
         // scaledRedStaticImg = new BufferedImage(rawRedStaticImg.getWidth()*2,
         // rawRedStaticImg.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
         //
         // Graphics g = scaledRedStaticImg.createGraphics();
         // g.drawImage(rawRedStaticImg, 0, 0, rawRedStaticImg.getWidth()*2,
         // rawRedStaticImg.getHeight()*2, null);
         // g.dispose();
      }

      if (rawBlueMobileImg != null)
      {
         // Does this need scaling?
         scaledBlueMobileImg = rawBlueMobileImg;
      }
   }

   /**
    * Draws the haven graphic over all vertices (intersections) that are havens.
    *
    * @param g2d
    *           Draw on this graphics object.
    */
   private void drawHavens(Graphics2D g2d)
   {
      if (scaledHavenImg == null)
      {
         // Failed to load the resource, do not render
         return;
      }

      // Overlay the haven graphic over all road intersections that are also
      // havens
      final int halfRdSz = roadInterSectionSz / 2;
      for (WorldCoordinate wc : model.getWorld().getHavenLocations())
      {
         final int x = (int) (pixelsPerMeterW * wc.getEast().asMeters());
         // Invert the y axis so that "north" is at the top of the screen.
         final int y = bounds.height - (int) (pixelsPerMeterH * wc.getNorth().asMeters());
         g2d.drawImage(scaledHavenImg, x - halfRdSz, y - halfRdSz, null);
      }
   }

   private void drawRoads(Graphics2D g2d)
   {
      g2d.setColor(Color.pink);
      g2d.setStroke(roadStroke);

      final int halfRdSz = roadInterSectionSz / 2;

      for (DirectedEdge<WorldCoordinate> edge : model.getWorld().getRoadNetwork().getEdges())
      {
         WorldCoordinate start = edge.getStartVertex().getUserData();
         WorldCoordinate end = edge.getEndVertex().getUserData();

         final int xStart = (int) (pixelsPerMeterW * start.getEast().asMeters());
         // Invert the y axis so that "north" is at the top of the screen.
         final int yStart = bounds.height - (int) (pixelsPerMeterH * start.getNorth().asMeters());

         final int xEnd = (int) (pixelsPerMeterW * end.getEast().asMeters());
         // Invert the y axis so that "north" is at the top of the screen.
         final int yEnd = bounds.height - (int) (pixelsPerMeterH * end.getNorth().asMeters());

         // Draw a line representing the road
         g2d.setColor(Color.pink);
         g2d.drawLine(xStart, yStart, xEnd, yEnd);

         // Draw squares over the road intersections (road graph vertices)
         g2d.fillRect(xStart - halfRdSz, yStart - halfRdSz, roadInterSectionSz, roadInterSectionSz);
         g2d.fillRect(xEnd - halfRdSz, yEnd - halfRdSz, roadInterSectionSz, roadInterSectionSz);

         // Draw the road intersection/vertex IDs above and to the left of the
         // intersection
         g2d.setColor(Color.GREEN);
         g2d.drawString(Integer.toString(edge.getStartVertex().getID()), xStart - roadInterSectionSz,
               yStart - roadInterSectionSz);
         g2d.drawString(Integer.toString(edge.getEndVertex().getID()), xEnd - roadInterSectionSz,
               yEnd - roadInterSectionSz);

      }
   }

   private void drawGridLines(Graphics2D g2d)
   {
      final int pixH = bounds.height - 1;
      final int pixW = bounds.width - 1;

      // White, half alpha
      g2d.setColor(Color.blue);
      g2d.drawRect(bounds.x + 1, bounds.y + 1, pixW, pixH);
      g2d.setColor(new Color(255, 255, 255, 127));
      final int numCols = model.getWorld().getColumnCount();
      final int numRows = model.getWorld().getRowCount();

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numCols; ++i)
      {
         g2d.drawLine(i * gridCellW, bounds.y, i * gridCellW, pixH);
      }

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numRows; ++i)
      {
         g2d.drawLine(bounds.x, i * gridCellH, pixW, i * gridCellH);
      }
   }

   private void drawTargets(Graphics2D g2d)
   {
      WorldCoordinate wc = new WorldCoordinate();

      int x = -1;
      int y = -1;
      int halfImgW = -1;
      int halfImgH = -1;

      AffineTransform trans = new AffineTransform();

      for (Target tgt : model.getTargetManager().getAllTargets())
      {
         trans.setToIdentity();

         wc.setCoordinate(tgt.getCoordinate());

         x = (int) (pixelsPerMeterW * wc.getEast().asMeters());
         y = (int) (pixelsPerMeterH * wc.getNorth().asMeters());
         // Invert the y axis so that "north" is at the top of the screen.
         y = bounds.height - y;

         if (tgt.getType().isMobile())
         {
            if (scaledRedMobileImg != null)
            {
               halfImgW = scaledRedMobileImg.getWidth() / 2;
               halfImgH = scaledRedMobileImg.getHeight() / 2;

               trans.translate(x - halfImgW, y - halfImgH);
               trans.rotate(-tgt.getOrientation().asRadians());

               g2d.drawImage(scaledRedMobileImg, trans, null);
            }
         }
         else
         {
            if (scaledRedStaticImg != null)
            {
               halfImgW = scaledRedStaticImg.getWidth() / 2;
               halfImgH = scaledRedStaticImg.getHeight() / 2;

               trans.translate(x - halfImgW, y - halfImgH);
               trans.rotate(-tgt.getOrientation().asRadians());

               g2d.drawImage(scaledRedStaticImg, trans, null);
            }
         }
      }
   }

   private void drawUAVs(Graphics2D g2d)
   {
      if (scaledBlueMobileImg == null)
      {
         return;
      }

      WorldCoordinate wc = new WorldCoordinate();

      int x = -1;
      int y = -1;
      final int halfImgW = scaledBlueMobileImg.getWidth() / 2;
      final int halfImgH = scaledBlueMobileImg.getHeight() / 2;

      AffineTransform trans = new AffineTransform();

      for (UAV uav : model.getUAVManager().getAllUAVs())
      {
         trans.setToIdentity();

         wc.setCoordinate(uav.getCoordinate());

         x = (int) (pixelsPerMeterW * wc.getEast().asMeters());
         y = (int) (pixelsPerMeterH * wc.getNorth().asMeters());
         // Invert the y axis so that "north" is at the top of the screen.
         y = bounds.height - y;

         trans.translate(x - halfImgW, y - halfImgH);
         trans.rotate(-uav.getOrientation().asRadians());

         g2d.drawImage(scaledBlueMobileImg, trans, null);
      }
   }

   /**
    * Convenience function to render the world into an image.
    *
    * @return An image with the world rendered into it.
    */
   public BufferedImage renderToImage()
   {
      BufferedImage img = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
      render(img.createGraphics());
      return img;
   }

   /**
    * Convenience wrapper function to render the given world into an image of
    * the specified size. This provides a screenshot of the world.
    *
    * @param model
    *           The model to render.
    * @param imgW
    *           The width of the final image in pixels.
    * @param imgH
    *           The height of the final image in pixels.
    * @return The world rendered into the image.
    */
   public static BufferedImage renderToImage(SimModel model, int imgW, int imgH)
   {
      if (model == null)
      {
         throw new NullPointerException("SimModel cannot be null.");
      }

      BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
      RenderSimState render = new RenderSimState(model);
      render.setBounds(0, 0, imgW, imgH);
      render.render(img.createGraphics());
      return img;
   }
}
