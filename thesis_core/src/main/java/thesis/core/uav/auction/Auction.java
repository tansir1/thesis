package thesis.core.uav.auction;

public class Auction
{
   private AuctionKey auctionKey;

   public Auction(AuctionKey auctionKey)
   {
      this.auctionKey = auctionKey;
   }

   public AuctionKey getKey()
   {
      return auctionKey;
   }
}
