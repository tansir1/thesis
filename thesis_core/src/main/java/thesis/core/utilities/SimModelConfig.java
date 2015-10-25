package thesis.core.utilities;

import java.io.File;

/**
 * Contains all the configuration parameters for the simulation model.
 */
public class SimModelConfig
{
   private int randomSeed;
   private File worldFile;
   private File entityTypesFile;


   public SimModelConfig()
   {
      randomSeed = 0;
      worldFile = null;
      entityTypesFile = null;
   }

   public int getRandomSeed()
   {
      return randomSeed;
   }

   public void setRandomSeed(int randomSeed)
   {
      this.randomSeed = randomSeed;
   }

   public File getWorldFile()
   {
      return worldFile;
   }

   public void setWorldFile(File file)
   {
      if(file == null)
      {
         throw new NullPointerException("World file cannot be null.");
      }
      this.worldFile = file;
   }

   public File getEntityTypeFile()
   {
      return entityTypesFile;
   }

   public void setEntityTypeFile(File file)
   {
      if(file == null)
      {
         throw new NullPointerException("Entity Type file cannot be null.");
      }
      this.entityTypesFile = file;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      // Generic sim parameters
      sb.append("Random seed: ");
      sb.append(randomSeed);
      sb.append("\nWorld: ");
      sb.append(worldFile.getAbsolutePath());
      sb.append("\nEntity Types: ");
      sb.append(entityTypesFile.getAbsolutePath());

      return sb.toString();
   }
}
