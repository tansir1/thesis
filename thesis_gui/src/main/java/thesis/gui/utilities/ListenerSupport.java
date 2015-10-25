package thesis.gui.utilities;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe wrapper to maintain a list of callback listeners.
 *
 * @param <T>
 */
public class ListenerSupport<T>
{
	private List<T> listeners;

	public ListenerSupport()
	{
		listeners = new CopyOnWriteArrayList<T>();
	}

	public void addListener(T listener)
	{
		listeners.add(listener);
	}

	public void removeListener(T listener)
	{
		listeners.remove(listener);
	}

	/**
	 * @return An unmodifiable view of the list of callback listeners.
	 */
	public List<T> getListeners()
	{
		return Collections.unmodifiableList(listeners);
	}
}
