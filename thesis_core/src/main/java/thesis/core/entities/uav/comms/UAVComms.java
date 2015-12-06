package thesis.core.entities.uav.comms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import thesis.core.common.Circle;
import thesis.core.entities.uav.UAV;
import thesis.core.entities.uav.UAVMgr;

public class UAVComms
{
   private Queue<Message> incomingQ;
   private Queue<Message> outgoingQ;
   private UAVMgr uavMgr;
   private Random randGen;
   /**
    * Geographic range of where the UAV can communicate.
    */
   private Circle commsCoverage;

   /**
    * The maximum number of times that a message can be relayed between UAVs.
    */
   private final int maxRelayHops;

   /**
    * UAV containing this communications system.
    */
   private UAV hostUAV;

   /**
    * The probability that the UAV will relay a message.
    */
   private float commsRelayProb;

   public UAVComms(UAV hostUAV, UAVMgr uavMgr, int maxRelayHops, double maxCommsRng, Random randGen,
         float commsRelayProb)
   {
      if (hostUAV == null)
      {
         throw new NullPointerException("Host UAV cannot be null.");
      }

      if (uavMgr == null)
      {
         throw new NullPointerException("UAVMgr cannot be null.");
      }

      if (randGen == null)
      {
         throw new NullPointerException("Random generator cannot be null.");
      }

      this.hostUAV = hostUAV;
      this.uavMgr = uavMgr;
      this.maxRelayHops = maxRelayHops;
      this.randGen = randGen;
      this.commsRelayProb = commsRelayProb;

      commsCoverage = new Circle();
      commsCoverage.setRadius(maxCommsRng);

      incomingQ = new LinkedList<Message>();
      outgoingQ = new LinkedList<Message>();
   }

   public void receive(final Message msg)
   {
      incomingQ.offer(msg);
   }

   /**
    * Schedule a message to be transmitted to nearby UAVs.
    *
    * @param msg
    *           The message to send.
    * @param destinationID
    *           Who should receive the message. Can be
    *           {@link Message#BROADCAST_ID} to send to all UAVs in range.
    */
   public void transmit(final Message msg, int destinationID)
   {
      msg.setNumHops(0);
      msg.setOriginatingUAV(hostUAV.getID());
      msg.setReceiverUAV(destinationID);
      msg.resetTime();
      outgoingQ.offer(msg);
   }

   public void stepSimulation()
   {
      commsCoverage.getCenter().setCoordinate(hostUAV.getCoordinate());

      List<UAV> uavs = uavMgr.getAllUAVsInRegion(commsCoverage);
      relayMessages(uavs);

      Iterator<Message> itr = outgoingQ.iterator();
      while(itr.hasNext())
      {
         Message msg = itr.next();
         for (UAV uav : uavs)
         {
            uav.getComms().receive(msg);
         }
         itr.remove();
      }
   }

   /**
    * Scan through the incoming queue and process everything not destined for
    * this UAV.
    *
    * Processing messages entails relaying (based on a probability) or dropping
    * the message.
    *
    * @param uavs
    *           The list of all UAVs within communications range.
    */
   private void relayMessages(List<UAV> uavs)
   {
      Iterator<Message> itr = incomingQ.iterator();
      while (itr.hasNext())
      {
         Message msg = itr.next();
         if (msg.getReceiverUAV() != hostUAV.getID() && msg.getReceiverUAV() != Message.BROADCAST_ID
               && msg.getNumHops() < maxRelayHops)
         {

            Message toSend = msg.copy();
            toSend.setNumHops(msg.getNumHops() + 1);

            if (randGen.nextFloat() < commsRelayProb)
            {
               for (UAV uav : uavs)
               {
                  uav.getComms().receive(toSend);
               }
            }
            // Message has been processed (dropped or relayed) so remove it from
            // the queue
            itr.remove();
         }
      }
   }
}
