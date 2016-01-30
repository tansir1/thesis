package thesis.core.uav.comms;

public class CommsConfig
{
   /**
    * The maximum number of times that a message can be relayed between UAVs.
    */
   private int maxRelayHops;

   /**
    * The maximum communication range in meters.
    */
   private double maxCommsRng;

   /**
    * The probability that the UAV will relay a message [0,1].
    */
   private float commsRelayProb;

   public CommsConfig()
   {

   }

   public int getMaxRelayHops()
   {
      return maxRelayHops;
   }

   public void setMaxRelayHops(int maxRelayHops)
   {
      this.maxRelayHops = maxRelayHops;
   }

   public double getMaxCommsRng()
   {
      return maxCommsRng;
   }

   public void setMaxCommsRng(double maxCommsRng)
   {
      this.maxCommsRng = maxCommsRng;
   }

   public float getCommsRelayProb()
   {
      return commsRelayProb;
   }

   public void setCommsRelayProb(float commsRelayProb)
   {
      this.commsRelayProb = commsRelayProb;
   }

}
