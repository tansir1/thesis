package thesis.core.uav.auction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import thesis.core.uav.comms.IMsgTransmitter;
import thesis.core.uav.comms.Message;
import thesis.core.uav.comms.Message.MsgType;
import thesis.core.uav.logic.TaskType;

public class AuctionTests
{

   @Test
   public void auctionTest()
   {
      TestTransmitter testTx = new TestTransmitter();
      final int targetID = 1;
      AuctionMgr testMe = new AuctionMgr(42);
      assertTrue("Failed to start confirm task auction.", testMe.startAuction(TaskType.Confirm, targetID));
      assertFalse("Invalid auction restart of confirm task.", testMe.startAuction(TaskType.Confirm, targetID));
      testMe.stepSimulation(testTx);

      assertEquals("Did not send announcement message.", MsgType.AuctionAnnounce, testTx.message.getType());
      assertEquals("Did not broadcast the confirm announcement task.", Message.BROADCAST_ID, testTx.destination);
      AuctionAnnounceMessage auctionAnncMsg = (AuctionAnnounceMessage)testTx.message;
      assertEquals("Did not announce the confirmation task.", TaskType.Confirm, auctionAnncMsg.getTaskType());
      assertEquals("Did not announce the confirmation task for correct target.", targetID, auctionAnncMsg.getTargetID());
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
}
