package thesis.core.utilities;

import java.io.File;

/**
 * Contains all the configuration parameters for the simulation model.
 */
public class SimModelConfig
{
   private int randomSeed;
   private File worldDir;
   private File entityTypesDir;

   /**
    * Speed in % / second in which cell belief certainty decays.
    */
   private double beliefDecayRate;

   /**
    * Maximum range of UAV communication systems expressed as a percentage of
    * the maximum distance across the world.
    */
   private double commsRngPercent;

   /**
    * The probability [0,1] that a UAV will relay a message.
    */
   private double probMsgFwd;

   /**
    * The average uncertainty across world belief models must be below this
    * value for the model to believe the world is "known."
    */
   private double minWorldClearUncertThreshold;

   public SimModelConfig()
   {
      randomSeed = 0;
      worldDir = null;
      entityTypesDir = null;
      commsRngPercent = 0;
      beliefDecayRate = 0;
      minWorldClearUncertThreshold = 0;
   }

   public int getRandomSeed()
   {
      return randomSeed;
   }

   public void setRandomSeed(int randomSeed)
   {
      this.randomSeed = randomSeed;
   }

   public File getWorldDir()
   {
      return worldDir;
   }

   public void setWorldDir(File file)
   {
      if (file == null)
      {
         throw new NullPointerException("World directory cannot be null.");
      }
      this.worldDir = file;
   }

   public File getEntityTypeDir()
   {
      return entityTypesDir;
   }

   public void setEntityTypeDir(File file)
   {
      if (file == null)
      {
         throw new NullPointerException("Entity directory file cannot be null.");
      }
      this.entityTypesDir = file;
   }

   public void setCommsRngPercent(double percent)
   {
      this.commsRngPercent = percent;
   }

   public double getCommsRngPercent()
   {
      return commsRngPercent;
   }

   public void setCommsRelayProbability(double prob)
   {
      this.probMsgFwd = prob;
   }

   public double getCommsRelayProbability()
   {
      return probMsgFwd;
   }

   public void setBeliefDecayRate(double rate)
   {
      this.beliefDecayRate = rate;
   }

   public double getBeliefDecayRate()
   {
      return beliefDecayRate;
   }
   
   public void setMinWorldClearUncert(double min)
   {
      minWorldClearUncertThreshold = min;
   }
   
   public double getMinWorldClearUncert()
   {
      return minWorldClearUncertThreshold;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      // Generic sim parameters
      sb.append("Random seed: ");
      sb.append(randomSeed);
      sb.append("\nWorld: ");
      sb.append(worldDir.getAbsolutePath());
      sb.append("\nEntity Types: ");
      sb.append(entityTypesDir.getAbsolutePath());
      sb.append("\nCommsRng: ");
      sb.append(String.format("%.2f", commsRngPercent));
      sb.append("\nBeliefDecayRate: ");
      sb.append(String.format("%.2f", beliefDecayRate));
      sb.append("\nMinWorldClearUncert: ");
      sb.append(String.format("%.2f", minWorldClearUncertThreshold));
      

      return sb.toString();
   }
}
