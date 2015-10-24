package thesis.gui.mainwindow;

import javax.swing.JFrame;

import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 * Container of all actions in the main GUI.
 */
public class Actions
{
	private ScreenShotAction screenShotAction;

	public Actions(JFrame parentFrame, RenderableSimWorldPanel simPanel)
	{
		screenShotAction = new ScreenShotAction(parentFrame, simPanel);
	}

	public ScreenShotAction getScreenShotAction()
	{
		return screenShotAction;
	}

}
