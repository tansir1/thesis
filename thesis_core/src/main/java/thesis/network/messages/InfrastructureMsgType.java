package thesis.network.messages;

public enum InfrastructureMsgType
{

   Test(0);

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
      }
      return type;
   }
}
