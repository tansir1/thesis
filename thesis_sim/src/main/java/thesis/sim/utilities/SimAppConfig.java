package thesis.sim.utilities;

public class SimAppConfig
{
   private String serverIP;
   private int serverPort;

   private boolean enableNetwork;

   public SimAppConfig()
   {
      serverIP = "";
      serverPort = -1;
      enableNetwork = false;
   }

   public String getServerIP()
   {
      return serverIP;
   }

   public void setServerIP(String serverIP)
   {
      this.serverIP = serverIP;
   }

   public int getServerPort()
   {
      return serverPort;
   }

   public void setServerPort(int serverPort)
   {
      this.serverPort = serverPort;
   }

   public boolean isEnableNetwork()
   {
      return enableNetwork;
   }

   public void setEnableNetwork(boolean enableNetwork)
   {
      this.enableNetwork = enableNetwork;
   }

}
