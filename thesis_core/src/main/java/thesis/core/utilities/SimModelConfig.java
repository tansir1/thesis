package thesis.core.utilities;

import java.text.DecimalFormat;

/**
 * Contains all the configuration parameters for the simulation model.
 */
public class SimModelConfig
{
   int randomSeed;

   // World map model values
   private double worldWidth;
   private double worldHeight;
   private int numWorldRows;
   private int numWorldCols;

   public SimModelConfig()
   {
      randomSeed = 0;

      worldWidth = 0;
      worldHeight = 0;
      numWorldCols = 0;
      numWorldRows = 0;
   }

   public double getWorldWidth()
   {
      return worldWidth;
   }

   public void setWorldWidth(double worldWidth)
   {
      this.worldWidth = worldWidth;
   }

   public double getWorldHeight()
   {
      return worldHeight;
   }

   public void setWorldHeight(double worldHeight)
   {
      this.worldHeight = worldHeight;
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
      DecimalFormat df = new DecimalFormat("0.000");
      
      StringBuilder sb = new StringBuilder();
      
      //Generic sim parameters
      sb.append("-Sim-");
      sb.append("\n\tRandom seed: ");
      sb.append(randomSeed);
      
      //World parameters
      sb.append("\n-World-");
      sb.append("\n\tWidth (km):");
      sb.append(df.format(worldWidth));
      sb.append("\n\tHeight (km):");
      sb.append(df.format(worldHeight));
      sb.append("\n\tRows:");
      sb.append(numWorldRows);
      sb.append("\n\tCols:");
      sb.append(numWorldCols);
      
      return sb.toString();
   }
}
