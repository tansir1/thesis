package thesis.core.uav.auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.TargetBelief;
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

   public boolean startAuction(TaskType task, TargetBelief tgtBelief)
   {
      boolean addedAuction = false;

      AuctionKey key = new AuctionKey(task, tgtBelief.getTrueTargetID());

      if (!auctions.containsKey(key))
      {
         addedAuction = true;

         AuctionAnnounceMessage msg = new AuctionAnnounceMessage(task, tgtBelief);
         msg.setReceiverUAV(Message.BROADCAST_ID);

         msgsToTx.add(msg);

         auctions.put(key, new Auction(key));
         logger.info("UAV {} started auction type {} for target {}", hostUavId, task, tgtBelief.getTrueTargetID());
      }

      if (!addedAuction)
      {
         logger.warn("UAV {} attempted to re-start auction type {} for target {}", hostUavId, task,
               tgtBelief.getTrueTargetID());
      }

      return addedAuction;
   }

   public void onAuctionAnnouncementReceived(Message rawMsg)
   {
      AuctionAnnounceMessage msg = (AuctionAnnounceMessage) rawMsg;

      AuctionKey key = new AuctionKey(msg.getTaskType(), msg.getTargetID());

      if (!auctions.containsKey(key))
      {
         // New auction, place a bid
         double bid = computeBid(msg.getTaskType(), msg.getBelief());

         logger.info("UAV {} bidding on task {} for target {} with a bid of {}", hostUavId, msg.getTaskType(),
               msg.getTargetID(), bid);

         msgsToTx.add(new BidMessage(msg.getTaskType(), msg.getTargetID(), bid));
      }
   }

   public void onAuctionBidReceived(Message rawMsg)
   {
      BidMessage msg = (BidMessage)rawMsg;
      AuctionKey key = new AuctionKey(msg.getTaskType(), msg.getTrueTgtID());

      if (auctions.containsKey(key))
      {
         Auction auction = auctions.get(key);
         auction.onBid(msg.getOriginatingUAV(), msg.getTaskType(), msg.getBid());
      }
   }

   public void onAuctionLossReceived()
   {

   }

   public void onAuctionWinReceived()
   {

   }

   public void stepSimulation(IMsgTransmitter msgTransmitter)
   {
      for (Message msg : msgsToTx)
      {
         msgTransmitter.transmit(msg, msg.getReceiverUAV());
      }
   }

   private double computeBid(TaskType task, TargetBelief tgtBelief)
   {
      return 0;
   }
}
