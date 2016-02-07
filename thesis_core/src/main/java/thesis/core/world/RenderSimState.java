package thesis.core.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.common.RoadNetwork;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;
import thesis.core.statedump.SensorDump;
import thesis.core.statedump.SimStateDump;
import thesis.core.statedump.TargetDump;
import thesis.core.statedump.UAVDump;
import thesis.core.utilities.CoreRsrcPaths;
import thesis.core.utilities.CoreUtils;
import thesis.core.world.RenderOptions.RenderOption;

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

   /**
    * Stroke for drawing lines connecting sample points along a UAV flight path.
    */
   private BasicStroke historyStroke;

   /**
    * Stroke used for drawing sensor FOV lines.
    */
   private BasicStroke sensorFOVStroke;

   private SimStateDump simStateDump;
   private WorldGIS gis;

   private Rectangle bounds;

   /**
    * Pixel size of the square representing road intersections (graph vertices).
    */
   private int roadInterSectionSz;

   private int selectedUAV;

   private BufferedImage rawHavenImg;
   private BufferedImage scaledHavenImg;

   private BufferedImage rawRedMobileImg;
   private BufferedImage scaledRedMobileImg;

   private BufferedImage rawGreenMobileImg;
   private BufferedImage scaledGreenMobileImg;

   private BufferedImage rawRedStaticImg;
   private BufferedImage scaledRedStaticImg;

   private BufferedImage rawBlueMobileImg;
   private BufferedImage scaledBlueMobileImg;

   private RenderOptions renderOpts;

   /**
    * Initialize a renderer with a bounds size of zero.
    *
    * This world cannot be correctly rendered until the rendering size is set
    * via {@link #setBounds(int, int, int, int)}.
    *
    * @param model
    *           The model to render.
    */
   public RenderSimState(SimStateDump simStateDump)
   {
      if (simStateDump == null)
      {
         throw new NullPointerException("SimStateDump to render cannot be null.");
      }

      this.simStateDump = simStateDump;
      renderOpts = new RenderOptions();
      bounds = new Rectangle();
      roadStroke = new BasicStroke(1f);
      historyStroke = new BasicStroke(3f);
      sensorFOVStroke = new BasicStroke(1f);
      rawHavenImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.HAVEN_IMG_PATH);
      rawRedMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_MOBILE_IMG_PATH);
      rawGreenMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.GREEN_MOBILE_IMG_PATH);
      rawRedStaticImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.RED_STATIC_IMG_PATH);
      rawBlueMobileImg = CoreUtils.getResourceAsImage(CoreRsrcPaths.BLUE_MOBILE_IMG_PATH);

      selectedUAV = -1;
      gis = simStateDump.getWorldGIS();
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

   public void setSelectedUAV(int id)
   {
      this.selectedUAV = id;
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

      synchronized (simStateDump)
      {
         if (renderOpts.isOptionEnabled(RenderOption.Graticule))
         {
            drawGridLines(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.Roads))
         {
            drawRoads(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.Havens))
         {
            drawHavens(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.Targets))
         {
            drawTargets(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.UavHistoryTrail))
         {
            drawUAVHistoryTrails(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.UAVs))
         {
            drawUAVs(gfx);
         }

         if (renderOpts.isOptionEnabled(RenderOption.SensorFOV))
         {
            drawSensorFOVs(gfx);
         }
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
   public WorldCoordinate pixelsToWorldCoordinate(int x, int y)
   {
      // Invert the y axis so that "north" is at the top of the screen.
      y = bounds.height - y;

      final double xPercent = (x * 1.0) / (1.0 * bounds.width);
      final double yPercent = (y * 1.0) / (1.0 * bounds.height);

      final double worldH = gis.getHeight() * yPercent;
      final double worldW = gis.getWidth() * xPercent;

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
      final int rows = gis.getRowCount() - 1;
      final int rawRow = y / gridCellH;
      final int invertedRows = rows - rawRow;
      return new CellCoordinate(invertedRows, x / gridCellW);
   }

   public Point worldCoordinateToPixels(final WorldCoordinate wc)
   {
      final double x = pixelsPerMeterW * wc.getEast();
      // Invert the y axis so that "north" is at the top of the screen.
      final double y = bounds.height - (int) (pixelsPerMeterH * wc.getNorth());

      final Point p = new Point(0, 0);
      p.setLocation(x, y);
      return p;
   }

   public void worldCoordinateToPixels(final WorldCoordinate wc, final Point pixels)
   {
      final double x = pixelsPerMeterW * wc.getEast();
      // Invert the y axis so that "north" is at the top of the screen.
      final double y = bounds.height - (int) (pixelsPerMeterH * wc.getNorth());
      pixels.setLocation(x, y);
   }

   public void cellCoordinateToPixels(final CellCoordinate cc, final Point pixels)
   {
      int pixelX = gridCellW * cc.getRow() + gridCellW / 2;
      // Invert the y axis so that "north" is at the top of the screen.
      int pixelY = bounds.height - (gridCellH * cc.getColumn() + gridCellH / 2);

      pixels.setLocation(pixelX, pixelY);
   }

   /**
    * Retrieve the rendering options.
    *
    * Changes made to this option set will affect the next frame of rendering.
    *
    * @return A reference to the rendering options.
    */
   public RenderOptions getRenderOptions()
   {
      return renderOpts;
   }

   private void recomputeScalingSizes()
   {
      int pixW = bounds.width - 1;
      int pixH = bounds.height - 1;

      int numCols = gis.getColumnCount();
      int numRows = gis.getRowCount();

      gridCellW = (int) Math.round((pixW * 1.0) / (numCols * 1.0));
      gridCellH = (int) Math.round((pixH * 1.0) / (numRows * 1.0));

      pixelsPerMeterH = (pixH * 1.0) / gis.getHeight();
      pixelsPerMeterW = (pixW * 1.0) / gis.getWidth();

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

      if (rawGreenMobileImg != null)
      {
         // Does this need scaling?
         scaledGreenMobileImg = rawGreenMobileImg;
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
      Point pixels = new Point(0, 0);

      Havens havens = simStateDump.getWorld().getHavens();
      final int NUM_HAVENS = havens.getNumHavens();

      for (int i = 0; i < NUM_HAVENS; ++i)
      {
         cellCoordinateToPixels(havens.getHavenByIndx(i), pixels);
         g2d.drawImage(scaledHavenImg, pixels.x - halfRdSz, pixels.y - halfRdSz, null);
      }
   }

   private void drawRoads(Graphics2D g2d)
   {
      g2d.setColor(Color.pink);
      g2d.setStroke(roadStroke);

      final Point start = new Point(0, 0);
      final Point end = new Point(0, 0);
      final CellCoordinate startRow = new CellCoordinate();
      final CellCoordinate endRow = new CellCoordinate();

      final int numCols = gis.getColumnCount();
      final int numRows = gis.getRowCount();
      final RoadNetwork roads = simStateDump.getWorld().getRoadNetwork();

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            if (roads.isTraversable(i, j))
            {
               if ((i + 1) < numRows && roads.isTraversable(i + 1, j)) // Check
                                                                       // next
                                                                       // row
               {
                  // Draw road from current cell to cell below
                  startRow.setCoordinate(i, j);
                  endRow.setCoordinate(i + 1, j);
                  cellCoordinateToPixels(startRow, start);
                  cellCoordinateToPixels(endRow, end);
                  g2d.drawLine(start.x, start.y, end.x, end.y);
               }

               if ((j + 1) < numCols && roads.isTraversable(i, j + 1)) // Check
                                                                       // next
                                                                       // column
               {
                  // Draw road from current cell to cell to the right
                  startRow.setCoordinate(i, j);
                  endRow.setCoordinate(i, j + 1);
                  cellCoordinateToPixels(startRow, start);
                  cellCoordinateToPixels(endRow, end);
                  g2d.drawLine(start.x, start.y, end.x, end.y);
               }
            }
         }
      }
   }

   private void drawGridLines(Graphics2D g2d)
   {
      final int pixH = bounds.height - 1;
      final int pixW = bounds.width - 1;

      g2d.setColor(Color.blue);
      g2d.drawRect(bounds.x + 1, bounds.y + 1, pixW, pixH);

      // White, half alpha
      final Color lineColor = new Color(255, 255, 255, 127);
      final int numCols = gis.getColumnCount();
      final int numRows = gis.getRowCount();

      g2d.setColor(Color.yellow);
      g2d.drawString(Integer.toString(0), bounds.x + 1, bounds.height - 1);

      // ----Major grind lines----
      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numCols; ++i)
      {
         g2d.setColor(Color.yellow);
         g2d.drawString(Integer.toString(i), i * gridCellW, bounds.height - 1);
         g2d.setColor(lineColor);
         g2d.drawLine(i * gridCellW, bounds.y, i * gridCellW, pixH);
      }

      // 0th border line is handled by the border rectangle
      for (int i = 1; i < numRows; ++i)
      {
         g2d.setColor(Color.yellow);
         g2d.drawString(Integer.toString(numRows - i), bounds.x + 1, i * gridCellH);
         g2d.setColor(lineColor);
         g2d.drawLine(bounds.x, i * gridCellH, pixW, i * gridCellH);
      }

      // ----Minor grind lines----
      g2d.setColor(lineColor);
      float dashSpacing[] = { 15.0f };
      BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashSpacing,
            0.0f);
      g2d.setStroke(dashed);

      final int halfCellW = gridCellW / 2;
      final int halfCellH = gridCellH / 2;
      for (int i = 0; i < numCols; ++i)
      {
         g2d.drawLine(i * gridCellW + halfCellW, bounds.y, i * gridCellW + halfCellW, pixH);
      }

      for (int i = 0; i < numRows; ++i)
      {
         g2d.drawLine(bounds.x, i * gridCellH + halfCellH, pixW, i * gridCellH + halfCellH);
      }
   }

   private void drawTargets(Graphics2D g2d)
   {
      final WorldCoordinate wc = new WorldCoordinate();

      int halfImgW = -1;
      int halfImgH = -1;

      final AffineTransform trans = new AffineTransform();
      final Point pixels = new Point(0, 0);

      final List<TargetDump> targets = simStateDump.getTargets();
      final int NUM_TARGETS = simStateDump.getTargets().size();
      TargetDump tgt = null;
      for (int i = 0; i < NUM_TARGETS; ++i)
      {
         tgt = targets.get(i);
         trans.setToIdentity();

         wc.setCoordinate(tgt.getPose().getCoordinate());

         worldCoordinateToPixels(wc, pixels);

         if (tgt.isMobile())
         {
            if (scaledRedMobileImg != null)
            {
               halfImgW = scaledRedMobileImg.getWidth() / 2;
               halfImgH = scaledRedMobileImg.getHeight() / 2;

               trans.translate(pixels.x - halfImgW, pixels.y - halfImgH);
               // trans.rotate(-tgt.getHeading().asRadians());
               double deg = tgt.getPose().getHeading() - 90;
               trans.rotate(-Math.toRadians(deg));

               g2d.drawImage(scaledRedMobileImg, trans, null);
            }
         }
         else
         {
            if (scaledRedStaticImg != null)
            {
               halfImgW = scaledRedStaticImg.getWidth() / 2;
               halfImgH = scaledRedStaticImg.getHeight() / 2;

               trans.translate(pixels.x - halfImgW, pixels.y - halfImgH);
               trans.rotate(Math.toRadians(-tgt.getPose().getHeading()));

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

      final WorldCoordinate wc = new WorldCoordinate();
      final Point pixels = new Point(0, 0);
      final int halfImgW = scaledBlueMobileImg.getWidth() / 2;
      final int halfImgH = scaledBlueMobileImg.getHeight() / 2;

      final AffineTransform trans = new AffineTransform();

      List<UAVDump> uavs = simStateDump.getUAVs();
      int numUAVs = uavs.size();
      UAVDump uav = null;
      for (int i = 0; i < numUAVs; ++i)
      {
         uav = uavs.get(i);

         trans.setToIdentity();

         wc.setCoordinate(uav.getPose().getCoordinate());

         worldCoordinateToPixels(wc, pixels);

         trans.translate(pixels.x - halfImgW, pixels.y - halfImgH);
         // trans.rotate(-uav.getHeading().asRadians());
         double deg = uav.getPose().getHeading() - 90;
         trans.rotate(-Math.toRadians(deg));

         if (uav.getID() != selectedUAV)
         {
            g2d.drawImage(scaledBlueMobileImg, trans, null);
         }
         else
         {
            g2d.drawImage(scaledGreenMobileImg, trans, null);
         }
      }
   }

   /**
    * Draw a series of connected dots at each recorded location of all UAV's
    * history trails.
    *
    * @param g2d
    */
   private void drawUAVHistoryTrails(Graphics2D g2d)
   {
      g2d.setColor(Color.blue);
      final Point curPixels = new Point(0, 0);
      final Point prevPix = new Point(0, 0);
      // BasicStroke historyStroke = new BasicStroke(3f);
      g2d.setStroke(historyStroke);

      List<WorldPose> history = new ArrayList<WorldPose>();
/*
      List<UAVDump> uavs = simStateDump.getUAVs();
      int numUAVs = uavs.size();
      UAVDump uav = null;
      for (int i = 0; i < numUAVs; ++i)
      {
         uav = uavs.get(i);
         prevPix.setLocation(-1, -1);

         history.clear();
         uav.getPathing().getFlightHistoryTrail(history);
         for (WorldPose pose : history)
         {
            worldCoordinateToPixels(pose.getCoordinate(), curPixels);

            if (prevPix.x != -1 && prevPix.y != -1)
            {
               g2d.drawLine(prevPix.x, prevPix.y, curPixels.x, curPixels.y);
            }

            prevPix.setLocation(curPixels);
         }
      }*/
   }

   private void drawSensorFOVs(Graphics2D gfx)
   {
      final Point frustrumPix = new Point(0, 0);
      final Point uavPix = new Point(0, 0);

      gfx.setColor(Color.yellow);
      gfx.setStroke(sensorFOVStroke);

      final int frustrumX[] = new int[5];
      final int frustrumY[] = new int[5];

      final List<UAVDump> uavs = simStateDump.getUAVs();
      final int NUM_UAVS = uavs.size();
      UAVDump uav = null;
      List<SensorDump> sensors = null;
      SensorDump sensor = null;
      for (int i = 0; i < NUM_UAVS; ++i)
      {
         uav = uavs.get(i);
         worldCoordinateToPixels(uav.getPose().getCoordinate(), uavPix);

         sensors = uav.getSensors();
         final int NUM_SENSORS = sensors.size();

         for (int j = 0; j < NUM_SENSORS; ++j)
         {
            sensor = sensors.get(j);
            final thesis.core.common.Rectangle viewRect = sensor.getViewFootPrint();

            // Line from UAV to center of FOV
            worldCoordinateToPixels(sensor.getViewCenter(), frustrumPix);
            gfx.drawLine(uavPix.x, uavPix.y, frustrumPix.x, frustrumPix.y);

            // Coordinates for drawing region box
            worldCoordinateToPixels(viewRect.getTopLeft(), frustrumPix);
            frustrumX[0] = frustrumPix.x;
            frustrumY[0] = frustrumPix.y;

            worldCoordinateToPixels(viewRect.getTopRight(), frustrumPix);
            frustrumX[1] = frustrumPix.x;
            frustrumY[1] = frustrumPix.y;

            worldCoordinateToPixels(viewRect.getBottomRight(), frustrumPix);
            frustrumX[2] = frustrumPix.x;
            frustrumY[2] = frustrumPix.y;

            worldCoordinateToPixels(viewRect.getBottomLeft(), frustrumPix);
            frustrumX[3] = frustrumPix.x;
            frustrumY[3] = frustrumPix.y;

            // Connect back to start
            worldCoordinateToPixels(viewRect.getTopLeft(), frustrumPix);
            frustrumX[4] = frustrumPix.x;
            frustrumY[4] = frustrumPix.y;

            gfx.drawPolyline(frustrumX, frustrumY, 5);
         }
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
    * @param dump
    *           The model to render.
    * @param imgW
    *           The width of the final image in pixels.
    * @param imgH
    *           The height of the final image in pixels.
    * @param opts
    *           Custom rendering options to apply to the rendered image.
    * @return The world rendered into the image.
    */
   public static BufferedImage renderToImage(SimStateDump dump, int imgW, int imgH, RenderOptions opts)
   {
      if (dump == null)
      {
         throw new NullPointerException("SimModel cannot be null.");
      }

      BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
      RenderSimState render = new RenderSimState(dump);

      if (opts != null)
      {
         render.getRenderOptions().copy(opts);
      }

      render.setBounds(0, 0, imgW, imgH);
      render.render(img.createGraphics());
      return img;
   }

   /**
    * Convenience wrapper function to render the given world into an image of
    * the specified size. This provides a screenshot of the world using default
    * rendering options.
    *
    * @param dump
    *           The model to render.
    * @param imgW
    *           The width of the final image in pixels.
    * @param imgH
    *           The height of the final image in pixels.
    * @return The world rendered into the image.
    */
   public static BufferedImage renderToImage(SimStateDump dump, int imgW, int imgH)
   {
      return renderToImage(dump, imgW, imgH, null);
   }

}
