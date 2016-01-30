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
    * Maximum range of UAV communication systems expressed as a percentage of
    * the maximum distance across the world.
    */
   private float commsRngPercent;

   /**
    * The probability [0,1] that a UAV will relay a message.
    */
   private float probMsgFwd;

   public SimModelConfig()
   {
      randomSeed = 0;
      worldDir = null;
      entityTypesDir = null;
      commsRngPercent = 0;
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

   public void setCommsRngPercent(float percent)
   {
      this.commsRngPercent = percent;
   }

   public float getCommsRngPercent()
   {
      return commsRngPercent;
   }

   public void setCommsRelayProbability(float prob)
   {
      this.probMsgFwd = prob;
   }

   public float getCommsRelayProbability()
   {
      return probMsgFwd;
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

      return sb.toString();
   }
}
