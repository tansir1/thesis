package thesis.core.uav.auction;

import java.util.ArrayList;
import java.util.List;

import thesis.core.uav.comms.IMsgTransmitter;
import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.TaskType;

public class AuctionMgr
{
   private List<Message> msgsToTx;

   public AuctionMgr()
   {
      msgsToTx = new ArrayList<Message>();
   }

   public void startAuction(TaskType task, int trueTgtID)
   {
      AuctionAnnounceMessage msg = new AuctionAnnounceMessage(task, trueTgtID);
      msg.setReceiverUAV(Message.BROADCAST_ID);

      msgsToTx.add(msg);
   }

   public void onAuctionAnnouncementReceived()
   {

   }

   public void onAuctionBidReceived()
   {

   }

   public void onAuctionLossReceived()
   {

   }

   public void onAuctionWinReceived()
   {

   }


   public void stepSimulation(IMsgTransmitter msgTransmitter)
   {
      for(Message msg : msgsToTx)
      {
         msgTransmitter.transmit(msg, msg.getReceiverUAV());
      }
   }

}
