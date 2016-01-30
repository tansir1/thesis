package thesis.core.uav.comms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import thesis.core.common.Circle;
import thesis.core.common.WorldCoordinate;
import thesis.core.uav.UAV;
import thesis.core.uav.UAVMgr;

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
    * ID number of the UAV containing this communications system.
    */
   private int hostUavId;

   /**
    * The probability that the UAV will relay a message.
    */
   private float commsRelayProb;

   public UAVComms(int hostUavId, UAVMgr uavMgr, Random randGen, CommsConfig commsCfg)
   {
      if (uavMgr == null)
      {
         throw new NullPointerException("UAVMgr cannot be null.");
      }

      if (randGen == null)
      {
         throw new NullPointerException("Random generator cannot be null.");
      }

      this.hostUavId = hostUavId;
      this.uavMgr = uavMgr;
      this.maxRelayHops = commsCfg.getMaxRelayHops();
      this.randGen = randGen;
      this.commsRelayProb = commsCfg.getCommsRelayProb();

      commsCoverage = new Circle();
      commsCoverage.setRadius(commsCfg.getMaxCommsRng());

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
      msg.setNumHops(maxRelayHops);
      msg.setOriginatingUAV(hostUavId);
      msg.setReceiverUAV(destinationID);
      msg.resetTime();
      outgoingQ.offer(msg);
   }

   public void stepSimulation(WorldCoordinate commsLocation)
   {
      commsCoverage.getCenter().setCoordinate(commsLocation);

      List<UAV> uavs = uavMgr.getAllUAVsInRegion(commsCoverage, hostUavId);
      relayMessages(uavs);

      Iterator<Message> itr = outgoingQ.iterator();
      while (itr.hasNext())
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
    * NOTE: Broadcast messages are deleted from the queue by this method! Be sure
    * to invoke {@link #getAllIncoming()} otherwise the UAV will miss broadcast
    * messages.
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
         if (msg.getReceiverUAV() != hostUavId && msg.getNumHops() < maxRelayHops)
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

   public List<Message> getAllIncoming()
   {
      List<Message> msgs = new ArrayList<Message>();

      Iterator<Message> itr = incomingQ.iterator();
      while (itr.hasNext())
      {
         Message msg = itr.next();
         if (msg.getReceiverUAV() == hostUavId || msg.getReceiverUAV() == Message.BROADCAST_ID)
         {
            msgs.add(msg);

            // Only remove the message from the queue if it was explicitly
            // destined
            // for this UAV. Broadcast messages are removed during {@link
            // #relayMessages(List<UAV>)}.
            if (msg.getReceiverUAV() == hostUavId)
            {
               itr.remove();
            }
         }
      }
      return msgs;
   }
}
