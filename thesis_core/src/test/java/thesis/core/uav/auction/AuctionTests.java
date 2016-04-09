package thesis.core.uav.auction;

public class AuctionTests
{
/*
   @Test
   public void hostAuctionTest()
   {
      TargetBelief tgtBelief = new TargetBelief(2, 12);
      tgtBelief.setCoordinate(new WorldCoordinate(100, 200));
      tgtBelief.setHeadingEstimate(45);
      tgtBelief.setTimestamp(123456);
      tgtBelief.setTypeProbability(0, 0.33);
      tgtBelief.setTypeProbability(1, 0.66);

      TestTransmitter testTx = new TestTransmitter();
      AuctionMgr testMe = new AuctionMgr(42);
      assertTrue("Failed to start confirm task auction.", testMe.startAuction(TaskType.Confirm, tgtBelief));
      assertFalse("Invalid auction restart of confirm task.", testMe.startAuction(TaskType.Confirm, tgtBelief));
      testMe.stepSimulation(testTx);

      assertEquals("Did not send announcement message.", MsgType.AuctionAnnounce, testTx.message.getType());
      assertEquals("Did not broadcast the confirm announcement task.", Message.BROADCAST_ID, testTx.destination);
      AuctionAnnounceMessage auctionAnncMsg = (AuctionAnnounceMessage)testTx.message;
      assertEquals("Did not announce the confirmation task.", TaskType.Confirm, auctionAnncMsg.getTaskType());
      assertEquals("Did not announce the confirmation task for correct target.", tgtBelief.getTrueTargetID(), auctionAnncMsg.getTargetID());

      BidMessage bid1 = new BidMessage(TaskType.Confirm, tgtBelief.getTrueTargetID(), 42.123);
      bid1.setOriginatingUAV(1);
      BidMessage bid2 = new BidMessage(TaskType.Confirm, tgtBelief.getTrueTargetID(), 456.123);
      bid2.setOriginatingUAV(2);

      testMe.onAuctionBidReceived(bid1);
      testMe.onAuctionBidReceived(bid2);
   }

   @Test
   public void respondToAuctionTest()
   {

   }

   private static class TestTransmitter implements IMsgTransmitter
   {
      public Message message;
      public int destination;

      public TestTransmitter()
      {

      }

      @Override
      public void transmit(Message msg, int destinationID)
      {
         this.message = msg;
         this.destination = destinationID;
      }

   }
   */
}
