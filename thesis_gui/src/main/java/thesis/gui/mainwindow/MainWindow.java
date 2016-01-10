package thesis.gui.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import thesis.core.SimModel;
import thesis.gui.mainwindow.actions.Actions;
import thesis.gui.mainwindow.uavstatpanel.UAVViewPanel;
import thesis.gui.simpanel.IMapMouseListener;
import thesis.gui.simpanel.MapMouseData;
import thesis.gui.simpanel.RenderableSimWorldPanel;

/**
 * Contains the GUI application's main window frame.
 *
 */
public class MainWindow implements IMapMouseListener
{
	private JFrame frame;
	private RenderableSimWorldPanel simPanel;
	private UAVViewPanel uavViewPan;
	private SimStatusPanel simStatPan;

	private Actions actions;

	private JLabel statusLbl;

	private SimTimer simTimer;

	public MainWindow()
	{
		frame = new JFrame("Thesis Simulator");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				onClose();
			}
		});

		simPanel = new RenderableSimWorldPanel();
		uavViewPan = new UAVViewPanel();
		simStatPan = new SimStatusPanel();
		simTimer = new SimTimer(simPanel);
		actions = new Actions(frame, simPanel, simTimer);

		MenuBar menuBar = new MenuBar(this, actions);
		frame.setJMenuBar(menuBar.getMenuBar());

		statusLbl = new JLabel("");

		buildGUI();

		frame.pack();
		frame.setVisible(true);

		simPanel.getListenerSupport().addListener(this);
	}

	private void buildGUI()
	{
		frame.setLayout(new BorderLayout());
		frame.add(simPanel, BorderLayout.CENTER);

		JPanel westPan = new JPanel();
		westPan.setLayout(new BoxLayout(westPan, BoxLayout.Y_AXIS));
		westPan.add(uavViewPan.getRenderable());
		westPan.add(simStatPan.getRenderable());


		frame.add(buildToolbar(), BorderLayout.NORTH);
		frame.add(westPan, BorderLayout.WEST);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.add(statusLbl);

		frame.add(statusPanel, BorderLayout.SOUTH);
	}

	protected void onClose()
	{
		// TODO Check if there is data to save
		// TODO Confirm exit
		int userChoice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit Thesis Simulator?",
				"Confirm exit", JOptionPane.YES_NO_OPTION);
		if (userChoice == JOptionPane.YES_OPTION)
		{
			// TODO Close external resources
			frame.dispose();
			System.exit(0);
		}
	}

	/**
	 * Get a reference to the main window's frame for passing into dialogs.
	 *
	 * @return The main window's frame.
	 */
	protected JFrame getParentFrame()
	{
		return frame;
	}

	private JToolBar buildToolbar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(actions.getPlayAction());
		toolbar.add(actions.getPauseAction());
		toolbar.add(actions.getStepSimAction());
		toolbar.addSeparator();
		toolbar.add(actions.getPlay5XAction());
		toolbar.add(actions.getPlay10XAction());
		toolbar.add(actions.getPlay20XAction());
		toolbar.add(actions.getPlay50XAction());
		toolbar.add(actions.getPlay100XAction());
		toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
		return toolbar;
	}

	/**
	 * Connect the event listeners of the GUI to the event triggers in the
	 * model.
	 *
	 * @param simModel
	 *            The model to listen to and to render.
	 */
	public void connectSimModel(SimModel simModel)
	{
		simPanel.connectSimModel(simModel, actions);
		uavViewPan.connectSimModel(simModel, simPanel);
		simStatPan.connectSimModel(simModel);
		simTimer.reset(simModel);
	}

	/**
	 * Request that the sim panel repaint itself.
	 */
	public void repaintSimPanel()
	{
	   SwingUtilities.invokeLater(new Runnable()
      {

         @Override
         public void run()
         {
            simPanel.repaint();
         }
      });
	}

	@Override
	public void onMapMouseUpdate(MapMouseData event)
	{
		statusLbl.setText(event.toString());
	}
}
