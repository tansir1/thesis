package thesis.gui.mainwindow;

import javax.swing.JFrame;

import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 * Container of all actions in the main GUI.
 */
public class Actions
{
	private ScreenShotAction screenShotAction;

	// Sim playback controls
	private PauseAction pauseAction;
	private PlayAction playAction;
	private StepSimAction stepSimAction;

	public Actions(JFrame parentFrame, RenderableSimWorldPanel simPanel, SimTimer simTimer)
	{
		screenShotAction = new ScreenShotAction(parentFrame, simPanel);
		pauseAction = new PauseAction(simTimer);
		playAction = new PlayAction(simTimer);
		stepSimAction = new StepSimAction(simTimer);
	}

	public ScreenShotAction getScreenShotAction()
	{
		return screenShotAction;
	}

	public PauseAction getPauseAction()
	{
		return pauseAction;
	}

	public PlayAction getPlayAction()
	{
		return playAction;
	}

	public StepSimAction getStepSimAction()
	{
		return stepSimAction;
	}

}
