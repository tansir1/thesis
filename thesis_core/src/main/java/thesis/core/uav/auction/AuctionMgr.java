package thesis.core.uav.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.uav.comms.IMsgTransmitter;
import thesis.core.uav.comms.Message;
import thesis.core.uav.logic.TaskType;
import thesis.core.utilities.LoggerIDs;

public class AuctionMgr
{
   private Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_AUCTION);

   private List<Message> msgsToTx;
   private Map<AuctionKey, Auction> auctions;
   private int hostUavId;

   public AuctionMgr(int hostUavID)
   {
      this.hostUavId = hostUavID;

      msgsToTx = new ArrayList<Message>();
      auctions = new HashMap<AuctionKey, Auction>();
   }

   public boolean startAuction(TaskType task, int trueTgtID)
   {
      boolean addedAuction = false;

      AuctionKey key = new AuctionKey(task, trueTgtID);

      if(!auctions.containsKey(key))
      {
         addedAuction = true;

         AuctionAnnounceMessage msg = new AuctionAnnounceMessage(task, trueTgtID);
         msg.setReceiverUAV(Message.BROADCAST_ID);

         msgsToTx.add(msg);

         auctions.put(key, new Auction(key));
         logger.info("UAV {} started auction type {} for target {}", hostUavId, task, trueTgtID);
      }

      if(!addedAuction)
      {
         logger.warn("UAV {} attempted to re-start auction type {} for target {}", hostUavId, task, trueTgtID);
      }

      return addedAuction;
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
