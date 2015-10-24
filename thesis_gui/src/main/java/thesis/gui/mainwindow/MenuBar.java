package thesis.gui.mainwindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar
{
	private JMenuBar menuBar;

	public MenuBar(MainWindow mainWin, Actions actions)
	{
		menuBar = new JMenuBar();

		menuBar.add(buildFileMenu(mainWin, actions));
		menuBar.add(buildHelpMenu(mainWin, actions));
	}

	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	private JMenu buildFileMenu(final MainWindow mainWin, Actions actions)
	{
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				mainWin.onClose();
			}
		});

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(actions.getScreenShotAction());
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		return fileMenu;
	}

	private JMenu buildHelpMenu(final MainWindow mainWin, Actions actions)
	{
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				AboutDialog abt = new AboutDialog(mainWin.getParentFrame());
				abt.setVisible(true);
			}
		});

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(aboutItem);
		return helpMenu;
	}
}
