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
   private Play5XAction play5xAction;
   private Play10XAction play10xAction;
   private Play20XAction play20xAction;
   private Play50XAction play50xAction;
   private Play100XAction play100xAction;

   public Actions(JFrame parentFrame, RenderableSimWorldPanel simPanel, SimTimer simTimer)
   {
      screenShotAction = new ScreenShotAction(parentFrame, simPanel);
      pauseAction = new PauseAction(simTimer);
      playAction = new PlayAction(simTimer);
      stepSimAction = new StepSimAction(simTimer);
      play5xAction = new Play5XAction(simTimer);
      play10xAction = new Play10XAction(simTimer);
      play20xAction = new Play20XAction(simTimer);
      play50xAction = new Play50XAction(simTimer);
      play100xAction = new Play100XAction(simTimer);
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

   public Play5XAction getPlay5XAction()
   {
      return play5xAction;
   }

   public Play10XAction getPlay10XAction()
   {
      return play10xAction;
   }

   public Play20XAction getPlay20XAction()
   {
      return play20xAction;
   }

   public Play50XAction getPlay50XAction()
   {
      return play50xAction;
   }

   public Play100XAction getPlay100XAction()
   {
      return play100xAction;
   }
}
