package thesis.gui.utilities;

public class GUIAppConfig
{
   private String serverIP;
   private int serverPort;

   public GUIAppConfig()
   {
      serverIP = "";
      serverPort = -1;
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

}
