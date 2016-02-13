package thesis.gui.mainwindow.uavstatpanel;

import java.util.concurrent.LinkedBlockingQueue;

import thesis.core.world.RenderSimState;
import thesis.network.messages.BeliefGUIRequestMsg;
import thesis.network.messages.InfrastructureMsg;

public class PeriodicMsgAction implements Runnable
{
   private LinkedBlockingQueue<InfrastructureMsg> sendQ;

   private int selectedUAVID;

   private BeliefGUIRequestMsg beliefGUIReq;
   private RenderSimState renderer;

   public PeriodicMsgAction(LinkedBlockingQueue<InfrastructureMsg> sendQ, RenderSimState renderer)
   {
      this.sendQ = sendQ;
      this.renderer = renderer;
      selectedUAVID = -1;

      beliefGUIReq = new BeliefGUIRequestMsg();
   }

   @Override
   public void run()
   {
      selectedUAVID = renderer.getSelectedUAV();
      if(selectedUAVID != -1)
      {
         beliefGUIReq.setUAVID(selectedUAVID);
         try
         {
            sendQ.put(beliefGUIReq);
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

   }

}
