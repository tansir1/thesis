package thesis.core.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import thesis.core.common.CellCoordinate;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.DirectedEdge;

public class RenderWorld
{
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

	private World world;

	private Rectangle bounds;

	/**
	 * Initialize a renderer with a bounds size of zero.
	 *
	 * This world cannot be correctly rendered until the rendering size is set
	 * via {@link #setBounds(int, int, int, int)}.
	 *
	 * @param render
	 *            The world to render.
	 */
	public RenderWorld(World render)
	{
		if (render == null)
		{
			throw new NullPointerException("World to render cannot be null.");
		}

		this.world = render;
		bounds = new Rectangle();
	}

	/**
	 * Initialize a renderer with the specified bounds size.
	 *
	 * @param render
	 *            The world to render.
	 * @param minX
	 *            The minimum X pixel coordinate for the world will start here.
	 *            This allows the user to offset the rendering.
	 * @param minY
	 *            The minimum Y pixel coordinate for the world will start here.
	 *            This allows the user to offset the rendering.
	 * @param width
	 *            The width of the rendering space in pixels.
	 * @param height
	 *            The height of the rendering space in pixels.
	 */
	public RenderWorld(World render, int minX, int minY, int width, int height)
	{
		if (render == null)
		{
			throw new NullPointerException("World to render cannot be null.");
		}

		this.world = render;
		bounds = new Rectangle();
	}

	/**
	 * Set the pixel space for rendering the world.
	 *
	 * @param minX
	 *            The minimum X pixel coordinate for the world will start here.
	 *            This allows the user to offset the rendering.
	 * @param minY
	 *            The minimum Y pixel coordinate for the world will start here.
	 *            This allows the user to offset the rendering.
	 * @param width
	 *            The width of the rendering space in pixels.
	 * @param height
	 *            The height of the rendering space in pixels.
	 */
	public void setBounds(int minX, int minY, int width, int height)
	{
		bounds.x = minX;
		bounds.y = minY;
		bounds.width = width;
		bounds.height = height;
		recomputeCellPixelSize();
	}

	/**
	 * Render the world to the given graphics device.
	 *
	 * @param gfx
	 *            The world will be rendered into this graphics device.
	 */
	public void render(Graphics2D gfx)
	{
		if (gfx == null)
		{
			throw new NullPointerException("Graphics to paint with cannot be null.");
		}

		// Clear the image canvas
		gfx.setColor(Color.BLACK);
		gfx.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		drawGridLines(gfx);
		drawRoads(gfx);
		drawHavens(gfx);
	}

	/**
	 * Converts the given pixel coordinate into simulation world coordinates.
	 *
	 * @param x
	 *            The horizontal pixel location.
	 * @param y
	 *            The vertical pixel location.
	 * @return The pixel location converted into a world coordinate.
	 */
	public WorldCoordinate pixelsToWorldCoordinate(int x, int y)
	{
		double xPercent = (x * 1.0) / (1.0 * bounds.width);
		double yPercent = (y * 1.0) / (1.0 * bounds.height);

		Distance worldH = new Distance(world.getHeight());
		Distance worldW = new Distance(world.getWidth());

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
	 *            The horizontal pixel location.
	 * @param y
	 *            The vertical pixel location.
	 * @return The pixel location converted into a cell coordinate.
	 */
	public CellCoordinate pixelsToCellCoordinate(int x, int y)
	{
		// TODO Probably need to invert the y axis depending on where origin is
		// placed in sim model
		return new CellCoordinate(y / gridCellH, x / gridCellW);
	}

	private void recomputeCellPixelSize()
	{
		int pixW = bounds.width - 1;
		int pixH = bounds.height - 1;

		int numCols = world.getColumnCount();
		int numRows = world.getRowCount();

		gridCellW = (int) Math.round((pixW * 1.0) / (numCols * 1.0));
		gridCellH = (int) Math.round((pixH * 1.0) / (numRows * 1.0));

		pixelsPerMeterH = (pixH * 1.0) / world.getHeight().asMeters();
		pixelsPerMeterW = (pixW * 1.0) / world.getWidth().asMeters();
	}

	/**
	 * Draws a rectangle that fills half the grid cell to represent a safe haven
	 * location.
	 *
	 * @param g2d
	 *            Draw on this graphics object.
	 */
	private void drawHavens(Graphics2D g2d)
	{
		final int quarterGridW = (int) (gridCellW * 0.25);
		final int quarterGridH = (int) (gridCellH * 0.25);
		final int thirdGridW = (int) (gridCellW * 0.33);
		final int thirdGridH = (int) (gridCellH * 0.33);
		final int halfGridW = (int) (gridCellW * 0.5);
		final int halfGridH = (int) (gridCellH * 0.5);

		for (WorldCoordinate wc : world.getHavenLocations())
		{
			CellCoordinate cell = world.convertWorldToCell(wc);

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

		for (DirectedEdge<WorldCoordinate> edge : world.getRoadNetwork().getEdges())
		{
			WorldCoordinate start = edge.getStartVertex().getUserData();
			WorldCoordinate end = edge.getEndVertex().getUserData();

			int xStart = (int) (pixelsPerMeterW * start.getEast().asMeters());
			int yStart = (int) (pixelsPerMeterH * start.getNorth().asMeters());

			int xEnd = (int) (pixelsPerMeterW * end.getEast().asMeters());
			int yEnd = (int) (pixelsPerMeterH * end.getNorth().asMeters());

			g2d.drawLine(xStart, yStart, xEnd, yEnd);
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
		final int numCols = world.getColumnCount();
		final int numRows = world.getRowCount();

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

	/**
	 * Convenience wrapper function to render the given world into an image of
	 * the specified size. This provides a screenshot of the world.
	 *
	 * @param world
	 *            The world to render.
	 * @param imgW
	 *            The width of the final image in pixels.
	 * @param imgH
	 *            The height of the final image in pixels.
	 * @return The world rendered into the image.
	 */
	public static BufferedImage renderToImage(World world, int imgW, int imgH)
	{
		if (world == null)
		{
			throw new NullPointerException("World cannot be null.");
		}

		BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_3BYTE_BGR);
		RenderWorld render = new RenderWorld(world);
		render.setBounds(0, 0, imgW, imgH);
		render.render(img.createGraphics());
		return img;
	}
}
