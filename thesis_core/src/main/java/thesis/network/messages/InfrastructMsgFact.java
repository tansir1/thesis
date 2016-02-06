package thesis.network.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;

public class InfrastructMsgFact
{
   // TODO This should be a memory pool to avoid allocating new memory at
   // runtime

   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN_NET);

   public static InfrastructureMsg createMessage(byte msgType)
   {
      return createMessage(InfrastructureMsgType.fromMsgID(msgType));
   }

   public static InfrastructureMsg createMessage(InfrastructureMsgType msgType)
   {
      InfrastructureMsg msg = null;
      switch (msgType)
      {
      case SetSimStepRate:
         msg = new SetSimStepRateMsg();
         break;
      case SimTime:
         msg = new SimTimeMsg();
         break;
      case Test:
         msg = new TestMsg();
         break;
      default:
         logger.warn("Could not convert unknown message type {} to a message object.", msgType);
         break;
      }
      return msg;
   }

}
