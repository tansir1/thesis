package thesis.gui.mainwindow.actions.runspeed;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import thesis.core.utilities.CoreUtils;
import thesis.gui.mainwindow.SimTimer;
import thesis.gui.utilities.GuiRsrcPaths;

@SuppressWarnings("serial")
public class PauseAction extends AbstractAction
{
	private SimTimer simTimer;

	public PauseAction(SimTimer simTimer)
	{
		if(simTimer == null)
		{
			throw new NullPointerException("Sim timer cannot be null.");
		}
		this.simTimer = simTimer;

		putValue(SHORT_DESCRIPTION, "Pause the simulation.");
		putValue(MNEMONIC_KEY, KeyEvent.VK_P);

		Icon icon = new ImageIcon(CoreUtils.getResourceAsImage(GuiRsrcPaths.PAUSE_IMG_PATH));
		putValue(LARGE_ICON_KEY, icon);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		simTimer.pause();
	}

}
