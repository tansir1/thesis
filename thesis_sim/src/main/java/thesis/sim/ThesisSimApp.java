package thesis.sim;

import java.util.ArrayList;
import java.util.List;

import thesis.network.ClientComms;
import thesis.network.messages.InfrastructureMsg;
import thesis.network.messages.TestMsg;

public class ThesisSimApp
{

   public static void main(String[] args) throws InterruptedException
   {
      // TODO Auto-generated method stub
      // load configs
      // Init sim
      // listen for gui to connect

      ClientComms comms = new ClientComms();
      if (comms.connect("127.0.0.1", 10555))
      {
         int data[] = new int[3];
         data[0] = 0;
         data[1] = 1;
         data[2] = 2;

         TestMsg sendMsg1 = new TestMsg();
         sendMsg1.setData(data);

         data[0] = 3;
         data[1] = 4;
         data[2] = 5;
         TestMsg sendMsg2 = new TestMsg();
         sendMsg2.setData(data);

         List<InfrastructureMsg> msgs = new ArrayList<InfrastructureMsg>();
         msgs.add(sendMsg1);
         msgs.add(sendMsg2);
         comms.sendData(msgs);

         do
         {
            msgs = comms.getData();
            Thread.sleep(500);
         } while (msgs.isEmpty());

         TestMsg recvMsg = (TestMsg)msgs.get(0);
         data = recvMsg.getData();
         for(int i=0; i<data.length; ++i)
         {
            System.out.println(data[i]);
         }
      }
      else
      {
         System.err.println("Could not connect.");
      }
      comms.disconnect();
   }

}
