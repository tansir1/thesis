package thesis.core.utilities;

import thesis.core.common.Distance;

/**
 * Contains all the configuration parameters for the simulation model.
 */
public class SimModelConfig
{
   int randomSeed;

   // World map model values
   private Distance worldWidth;
   private Distance worldHeight;
   private int numWorldRows;
   private int numWorldCols;

   public SimModelConfig()
   {
      randomSeed = 0;

      worldWidth = new Distance();
      worldHeight = new Distance();
      numWorldCols = 0;
      numWorldRows = 0;
   }

   public Distance getWorldWidth()
   {
      return worldWidth;
   }

   public Distance getWorldHeight()
   {
      return worldHeight;
   }

   public int getNumWorldRows()
   {
      return numWorldRows;
   }

   public void setNumWorldRows(int numWorldRows)
   {
      this.numWorldRows = numWorldRows;
   }

   public int getNumWorldCols()
   {
      return numWorldCols;
   }

   public void setNumWorldCols(int numWorldCols)
   {
      this.numWorldCols = numWorldCols;
   }

   public int getRandomSeed()
   {
      return randomSeed;
   }

   public void setRandomSeed(int randomSeed)
   {
      this.randomSeed = randomSeed;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();

      // Generic sim parameters
      sb.append("-Sim-");
      sb.append("\n\tRandom seed: ");
      sb.append(randomSeed);

      // World parameters
      sb.append("\n-World-");
      sb.append("\n\tWidth:");
      sb.append(worldWidth);
      sb.append("\n\tHeight:");
      sb.append(worldHeight);
      sb.append("\n\tRows:");
      sb.append(numWorldRows);
      sb.append("\n\tCols:");
      sb.append(numWorldCols);

      return sb.toString();
   }
}
