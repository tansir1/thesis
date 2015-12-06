package thesis.gui.mainwindow.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.world.RenderSimState;
import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 *An action that saves a screenshot of the world.
 */
@SuppressWarnings("serial")
public class ScreenShotAction extends AbstractAction
{
	private JFrame parentFrame;
	private RenderableSimWorldPanel simPanel;

	public ScreenShotAction(JFrame parentFrame, RenderableSimWorldPanel simPanel)
	{
		super("Save Screenshot");

		if (parentFrame == null)
		{
			throw new NullPointerException("Parent frame cannot be null.");
		}

		if (simPanel == null)
		{
			throw new NullPointerException("Sim panel cannot be null.");
		}

		this.parentFrame = parentFrame;
		this.simPanel = simPanel;

		putValue(SHORT_DESCRIPTION, "Save a screenshot of the current state of the world.");
		putValue(MNEMONIC_KEY, KeyEvent.VK_PRINTSCREEN);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		//Initialize to the application directory.
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png");
		fileChooser.setFileFilter(filter);

		// fileChooser.setDialogTitle(dialogTitle);
		int choice = fileChooser.showSaveDialog(parentFrame);
		if (JFileChooser.APPROVE_OPTION == choice)
		{
			Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);

			File saveFile = fileChooser.getSelectedFile();
			RenderSimState render = simPanel.getWorldRenderer();
			if (render == null)
			{
				logger.error("World renderer is not yet initialized.  Cannot save screenshot.");
			}
			else
			{
				try
				{
					if(!saveFile.getName().endsWith(".png"))
					{
						saveFile = new File(fileChooser.getSelectedFile() + ".png");
					}

					logger.info("Saving screenshot of world into {}", saveFile.getAbsolutePath());
					BufferedImage image = render.renderToImage();
					ImageIO.write(image, "png", saveFile);
				}
				catch (IOException e)
				{
					logger.error("Failed to save screenshot.  Details: {}", e.getMessage());
				}
			}
		}
	}

}
