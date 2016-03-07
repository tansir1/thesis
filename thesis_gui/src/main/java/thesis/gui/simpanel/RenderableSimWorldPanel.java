package thesis.gui.simpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import thesis.core.SimModel;
import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldCoordinate;
import thesis.core.world.RenderSimState;
import thesis.gui.mainwindow.actions.Actions;
import thesis.gui.mainwindow.actions.renderopts.RenderOptAction;
import thesis.gui.utilities.ListenerSupport;

@SuppressWarnings("serial")
public class RenderableSimWorldPanel extends JPanel
{
	private MouseMoveListenerProxy mouseState;

	private RenderSimState renderWorld;

	private ListenerSupport<IMapMouseListener> listeners;

	//private SimModel simModel;

	public RenderableSimWorldPanel()
	{
		Dimension minSz = new Dimension(640, 480);
		setMinimumSize(minSz);
		setPreferredSize(minSz);

		mouseState = this.new MouseMoveListenerProxy();
		renderWorld = null;

		listeners = new ListenerSupport<IMapMouseListener>();

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (renderWorld != null)
				{
					renderWorld.setBounds(0, 0, getWidth(), getHeight());
					repaint();
				}
			}
		});

	}

	public ListenerSupport<IMapMouseListener> getListenerSupport()
	{
		return listeners;
	}

	public void connectSimModel(final SimModel simModel, final Actions actions)
	{
	   //this.simModel = simModel;

		renderWorld = new RenderSimState(simModel);
		this.addMouseListener(mouseState);
		this.addMouseMotionListener(mouseState);
		renderWorld.setBounds(0, 0, getWidth(), getHeight());

		for(RenderOptAction action : actions.getRenderOptions())
		{
		   action.connectToModel(renderWorld.getRenderOptions());
		}

		//repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Wipe out previous rendering and draw the background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if (renderWorld != null)
		{
			renderWorld.render(g2d);
			// drawCoordinates(g2d);
		}
		else
		{
			g2d.setColor(Color.BLACK);
			g2d.drawString("Simulation initializing", getWidth() / 2, getHeight() / 2);
		}

	}

	public RenderSimState getWorldRenderer()
	{
		return renderWorld;
	}

	private void updateMapMouseListeners()
	{
		if (mouseState.isMouseOverPanel())
		{
			MapMouseData data = mouseState.getData();
			for (IMapMouseListener l : listeners.getListeners())
			{
				l.onMapMouseUpdate(data);
			}
		}
	}

	private class MouseMoveListenerProxy implements MouseMotionListener, MouseListener
	{
		private int curX;
		private int curY;
		private boolean mouseOver;

		private boolean clicked;

		public MouseMoveListenerProxy()
		{
			curX = -1;
			curY = -1;
			mouseOver = false;
			clicked = false;
		}

		public MapMouseData getData()
		{
			WorldCoordinate wc = renderWorld.pixelsToWorldCoordinate(curX, curY);
			CellCoordinate cc = renderWorld.pixelsToCellCoordinate(curX, curY);

			MapMouseData data = new MapMouseData(wc, cc, curX, curY, clicked);
			return data;
		}

		public boolean isMouseOverPanel()
		{
			return mouseOver;
		}

		// public int getMouseX()
		// {
		// return curX;
		// }
		//
		// public int getMouseY()
		// {
		// return curY;
		// }

		@Override
		public void mouseDragged(MouseEvent evt)
		{
			// Do nothing for now
		}

		@Override
		public void mouseMoved(MouseEvent evt)
		{
			curX = evt.getX();
			curY = evt.getY();
			clicked = false;

			updateMapMouseListeners();
		}

		@Override
		public void mouseClicked(MouseEvent evt)
		{
	       curX = evt.getX();
	       curY = evt.getY();
	       clicked = true;

//	       Circle region = new Circle();
//	       renderWorld.pixelsToWorldCoordinate(curX, curY, region.getCenter());
//
//
//	       synchronized(simModel)
//	       {
//	          double radius = simModel.getWorldGIS().getMaxWorldDistance() * 0.01;
//	          region.setRadius(radius);
//	          List<UAV> uavs = simModel.getUAVManager().getAllUAVsInRegion(region);
//
//	          for(UAV uav : uavs)
//	          {
//	             System.out.println(Integer.toString(uav.getID()));
//	          }
//	       }

	       updateMapMouseListeners();
		}

		@Override
		public void mouseEntered(MouseEvent evt)
		{
			mouseOver = true;
		}

		@Override
		public void mouseExited(MouseEvent evt)
		{
			mouseOver = false;
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			// Do nothing for now
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			// Do nothing for now
		}

	}
}
