package thesis.gui.simpanel;

import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldCoordinate;

public class MapMouseData
{
	private WorldCoordinate world;
	private CellCoordinate cell;

	private int x, y;

	public MapMouseData(WorldCoordinate world, CellCoordinate cell, int x, int y)
	{
		this.world = world;
		this.cell = cell;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return The cell coordinate where the mouse cursor is located over the
	 *         map.
	 */
	public CellCoordinate getCellCoordinate()
	{
		return cell;
	}

	/**
	 * @return The world coordinate where the mouse cursor is located over the
	 *         map.
	 */
	public WorldCoordinate getWorldCoordinate()
	{
		return world;
	}

	/**
	 * @return The mouse X pixel coordinate.
	 */
	public int getMouseX()
	{
		return x;
	}

	/**
	 * @return The mouse Y pixel coordinate.
	 */
	public int getMouseY()
	{
		return y;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(world.toString());
		sb.append(" - ");
		sb.append(cell.toString());
		sb.append(" - ");
		sb.append("[");
		sb.append(Integer.toString(x));
		sb.append(",");
		sb.append(Integer.toString(y));
		sb.append("]");
		return sb.toString();
	}
}
