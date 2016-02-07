package thesis.gui.mainwindow.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import thesis.gui.mainwindow.SimTimer;
import thesis.gui.mainwindow.actions.renderopts.GraticuleOptAction;
import thesis.gui.mainwindow.actions.renderopts.HavensOptAction;
import thesis.gui.mainwindow.actions.renderopts.RenderOptAction;
import thesis.gui.mainwindow.actions.renderopts.RoadsOptAction;
import thesis.gui.mainwindow.actions.renderopts.SensorFOVOptAction;
import thesis.gui.mainwindow.actions.renderopts.TargetsOptAction;
import thesis.gui.mainwindow.actions.renderopts.UAVHistoryOptAction;
import thesis.gui.mainwindow.actions.renderopts.UAVsOptAction;
import thesis.gui.mainwindow.actions.runspeed.PauseAction;
import thesis.gui.mainwindow.actions.runspeed.PlayCPUAction;
import thesis.gui.mainwindow.actions.runspeed.Play4HzAction;
import thesis.gui.mainwindow.actions.runspeed.Play15HzAction;
import thesis.gui.mainwindow.actions.runspeed.Play30HzAction;
import thesis.gui.mainwindow.actions.runspeed.Play2HzAction;
import thesis.gui.mainwindow.actions.runspeed.PlayAction;
import thesis.gui.mainwindow.actions.runspeed.StepSimAction;
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
   private Play2HzAction play2HzAction;
   private Play4HzAction play4HzAction;
   private Play15HzAction play15HzAction;
   private Play30HzAction play30HzAction;
   private PlayCPUAction playCPUAction;

   private List<RenderOptAction> renderOptActions;

   public Actions(JFrame parentFrame, RenderableSimWorldPanel simPanel, SimTimer simTimer)
   {
      screenShotAction = new ScreenShotAction(parentFrame, simPanel);
      pauseAction = new PauseAction(simTimer);
      playAction = new PlayAction(simTimer);
      stepSimAction = new StepSimAction(simTimer);
      play2HzAction = new Play2HzAction(simTimer);
      play4HzAction = new Play4HzAction(simTimer);
      play15HzAction = new Play15HzAction(simTimer);
      play30HzAction = new Play30HzAction(simTimer);
      playCPUAction = new PlayCPUAction(simTimer);

      renderOptActions = new ArrayList<RenderOptAction>();
      renderOptActions.add(new UAVHistoryOptAction());
      renderOptActions.add(new GraticuleOptAction());
      renderOptActions.add(new HavensOptAction());
      renderOptActions.add(new RoadsOptAction());
      renderOptActions.add(new SensorFOVOptAction());
      renderOptActions.add(new TargetsOptAction());
      renderOptActions.add(new UAVsOptAction());
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

   public Play2HzAction getPlay2HzAction()
   {
      return play2HzAction;
   }

   public Play4HzAction getPlay4HzAction()
   {
      return play4HzAction;
   }

   public Play15HzAction getPlay15HzAction()
   {
      return play15HzAction;
   }

   public Play30HzAction getPlay30HzAction()
   {
      return play30HzAction;
   }

   public PlayCPUAction getPlayCPUAction()
   {
      return playCPUAction;
   }

   public List<RenderOptAction> getRenderOptions()
   {
      return renderOptActions;
   }
}
