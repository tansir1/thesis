package thesis.gui.network;

import thesis.network.ServerComms;

public class ClientListenerTask implements Runnable
{
   private ServerComms comms;
   private String listenIP;
   private int listenPort;
   private NetworkHndlr netHndlr;

   public ClientListenerTask(ServerComms comms, String ip, int port, NetworkHndlr netHndlr)
   {
      this.comms = comms;
      this.listenIP = ip;
      this.listenPort = port;
      this.netHndlr = netHndlr;
   }

   @Override
   public void run()
   {
      //Blocks until the client connects
      comms.listenForClient(listenIP, listenPort);
      netHndlr.enqueueSelf();
   }
}
