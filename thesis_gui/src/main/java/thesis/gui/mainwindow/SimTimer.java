package thesis.gui.mainwindow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import thesis.core.SimModel;

public class SimTimer
{
	/**
	 * Length in time in milliseconds between subsequent steps of the
	 * simulation.
	 */
	private final long timeStepMS;

	private ScheduledExecutorService execSvc;

	private SimModel model;
	private ScheduledFuture<?> future;

	/**
	 * Initialize a timer to drive the simulation. Does nothing without a
	 * simulation model being set.
	 *
	 * @see #reset(SimModel)
	 */
	public SimTimer()
	{
		execSvc = Executors.newSingleThreadScheduledExecutor();

		timeStepMS = 16;// 60hz update rate
	}

	public void reset(SimModel model)
	{
		if (model == null)
		{
			throw new NullPointerException("Model cannot be null.");
		}

		this.model = model;
	}

	public void step()
	{
		pause();
		if (model != null)
		{
			model.stepSimulation(timeStepMS);
		}
	}

	public void pause()
	{
		if (future != null)
		{
			future.cancel(false);
		}
	}

	public void run()
	{
		if (future != null)
		{
			future.cancel(false);
		}

		future = execSvc.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run()
			{
				if (model != null)
				{
					model.stepSimulation(timeStepMS);
				}
			}
		}, 0, timeStepMS, TimeUnit.MILLISECONDS);
	}

}
