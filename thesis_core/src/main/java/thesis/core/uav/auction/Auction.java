package thesis.core.uav.auction;

import java.util.HashMap;
import java.util.Map;

import thesis.core.uav.logic.TaskType;

@Deprecated
public class Auction
{
   /*
    * After this amount of time (milliseconds) the auction will close as long as
    * a single bid is present.
    */
   //private static final long BIDDING_WINDOW = 5000;
   private AuctionKey auctionKey;
   //private final long auctionStartTime;
   private Map<Integer, Double> bids;

   public Auction(AuctionKey auctionKey)
   {
      this.auctionKey = auctionKey;
      //auctionStartTime = SimTime.getCurrentSimTimeMS();

      bids = new HashMap<Integer, Double>();
   }

   public AuctionKey getKey()
   {
      return auctionKey;
   }

   public void onBid(int biddingUAV, TaskType type, double bid)
   {
      bids.put(biddingUAV, bid);
   }

   public void stepSimulation()
   {

   }
}
