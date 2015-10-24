package thesis.gui.simpanel;

/**
 * Callback interface for receiving mouse events related to the
 * {@link RenderableSimWorldPanel}.
 */
public interface IMapMouseListener
{
	/**
	 * Callback when new mouse data is generated from the simulation panel.
	 *
	 * @param event
	 *            Data describing the mouse and map interaction event.
	 */
	public void onMapMouseUpdate(MapMouseData event);
}
