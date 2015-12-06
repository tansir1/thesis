package thesis.gui.mainwindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import thesis.gui.mainwindow.actions.Actions;
import thesis.gui.mainwindow.actions.renderopts.RenderOptAction;

public class MenuBar
{
	private JMenuBar menuBar;

	public MenuBar(final MainWindow mainWin, final Actions actions)
	{
		menuBar = new JMenuBar();

		menuBar.add(buildFileMenu(mainWin, actions));
		menuBar.add(buildRenderOptionsMenu(mainWin, actions));
		menuBar.add(buildHelpMenu(mainWin, actions));
	}

	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	private JMenu buildFileMenu(final MainWindow mainWin, final Actions actions)
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

	private JMenu buildHelpMenu(final MainWindow mainWin, final Actions actions)
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

	private JMenu buildRenderOptionsMenu(final MainWindow mainWin, final Actions actions)
	{
      JMenu renderOptsMenu = new JMenu("Render Options");
      for(final RenderOptAction opt : actions.getRenderOptions())
      {
         final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(opt);
         menuItem.setSelected(opt.isRenderOptEnabled());
         menuItem.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               mainWin.repaintSimPanel();
            }
         });

         renderOptsMenu.add(menuItem);
      }
      return renderOptsMenu;
	}
}
