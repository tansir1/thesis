package thesis.network.messages;

public enum InfrastructureMsgType
{

   Test(0),
   SimTime(1),
   SetSimStepRate(2),
   RequestFullStateDump(3),
   WorldCfg(4),
   TargetsInit(5),
   FullInitReponse(6),
   SimStateUpdate(7),
   Shutdown(8);

   private byte id;
   private InfrastructureMsgType(int id)
   {
      this.id = (byte)id;
   }

   public byte getMsgID()
   {
      return id;
   }

   public static InfrastructureMsgType fromMsgID(byte id)
   {
      InfrastructureMsgType type = null;
      switch(id)
      {
      case 0:
         type = Test;
         break;
      case 1:
         type = SimTime;
         break;
      case 2:
         type = SetSimStepRate;
         break;
      case 3:
         type = RequestFullStateDump;
         break;
      case 4:
         type = WorldCfg;
         break;
      case 5:
         type = TargetsInit;
         break;
      case 6:
         type = FullInitReponse;
         break;
      case 7:
         type = SimStateUpdate;
         break;
      case 8:
         type = Shutdown;
         break;
      }
      return type;
   }
}
