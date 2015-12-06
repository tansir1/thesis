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
public class StepSimAction extends AbstractAction
{
	private SimTimer simTimer;

	public StepSimAction(SimTimer simTimer)
	{
		if(simTimer == null)
		{
			throw new NullPointerException("Sim timer cannot be null.");
		}
		this.simTimer = simTimer;

		putValue(SHORT_DESCRIPTION, "Step the simulation one update frame.");
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);

		Icon icon = new ImageIcon(CoreUtils.getResourceAsImage(GuiRsrcPaths.STEP_IMG_PATH));
		putValue(LARGE_ICON_KEY, icon);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		simTimer.step();
	}

}
