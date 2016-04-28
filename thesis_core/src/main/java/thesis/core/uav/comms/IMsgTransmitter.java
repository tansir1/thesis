package thesis.core.uav.comms;

public interface IMsgTransmitter
{
   /**
    * Schedule a message to be transmitted to nearby UAVs.
    *
    * @param msg
    *           The message to send.
    * @param destinationID
    *           Who should receive the message. Can be
    *           {@link Message#BROADCAST_ID} to send to all UAVs in range.
    */
   public void transmit(final Message msg, int destinationID);
}
