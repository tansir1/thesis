package thesis.core.uav.comms;

import thesis.core.common.SimTime;

/**
 * Abstract base class for all messages sent between UAVs.
 */
public abstract class Message
{
   /**
    * Special receiver ID indicating that all UAVs should listen to this
    * message.
    */
   public static final int BROADCAST_ID = -1;

   public enum MsgType
   {
      WorldBelief,

      @Deprecated
      AuctionAnnounce,
      @Deprecated
      AuctionBid,
      @Deprecated
      AuctionWin,
      @Deprecated
      AuctionLose,
   }

   /**
    * Unique identifier specifying the type of data contained in the message.
    */
   private final MsgType type;

   /**
    * The simulation time when the data in the message was created.
    */
   private long simTime;

   /**
    * ID of the UAV that created this message.
    */
   private int originatingUAV;

   /**
    * ID of the intended message recipient.
    */
   private int receiverUAV;

   /**
    * The number of times this message has been relayed in attempt to reach the
    * specified recipient.
    */
   private int numHops;

   public Message(MsgType type)
   {
      if (type == null)
      {
         throw new NullPointerException("Message type cannot be null.");
      }

      this.type = type;
   }

   public MsgType getType()
   {
      return type;
   }

   public Message copy()
   {
      Message msg = cloneMsgSpecificData();
      msg.simTime = simTime;
      msg.originatingUAV = originatingUAV;
      msg.receiverUAV = receiverUAV;
      msg.numHops = numHops;

      return msg;
   }

   /**
    * Subclasses must clone their local data into a new subclass object.
    *
    * @return A new message containing the same information as the invoker.
    */
   protected abstract Message cloneMsgSpecificData();

   /**
    * Reset the simulation time in which the message's data was considered valid
    * and updated to now.
    */
   public void resetTime()
   {
      simTime = SimTime.getCurrentSimTimeMS();
   }

   /**
    * @return The simulation time in milliseconds when the message data was
    *         updated.
    */
   public long getTime()
   {
      return simTime;
   }

   public int getOriginatingUAV()
   {
      return originatingUAV;
   }

   public void setOriginatingUAV(int senderUAV)
   {
      this.originatingUAV = senderUAV;
   }

   public int getReceiverUAV()
   {
      return receiverUAV;
   }

   public void setReceiverUAV(int receiverUAV)
   {
      this.receiverUAV = receiverUAV;
   }

   public int getNumHops()
   {
      return numHops;
   }

   public void setNumHops(int numHops)
   {
      this.numHops = numHops;
   }

}
