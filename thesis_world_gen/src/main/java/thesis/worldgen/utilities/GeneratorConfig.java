package thesis.worldgen.utilities;

import java.io.File;

public class GeneratorConfig
{
   private int numWorlds;
   private File outputDir;
   private File entityTypesFile;
   private int randSeed;

   private double worldWidth;
   private double worldHeight;
   private int numRows;
   private int numColumns;

   private int numMobileTargets;
   private int numStaticTargets;

   private int numUAVs;

   public GeneratorConfig()
   {

   }

   public void copy(GeneratorConfig copy)
   {
      numWorlds = copy.numWorlds;
      outputDir = copy.outputDir;
      entityTypesFile = copy.entityTypesFile;
      randSeed = copy.randSeed;
      worldWidth = copy.worldWidth;
      worldHeight = copy.worldHeight;
      numRows = copy.numRows;
      numColumns = copy.numColumns;
      numMobileTargets = copy.numMobileTargets;
      numStaticTargets = copy.numStaticTargets;
      numUAVs = copy.numUAVs;
   }

   public int getNumWorlds()
   {
      return numWorlds;
   }

   public void setNumWorlds(int numWorlds)
   {
      this.numWorlds = numWorlds;
   }

   public File getOutputDir()
   {
      return outputDir;
   }

   public void setOutputDir(File outputDir)
   {
      this.outputDir = outputDir;
   }

   public int getRandSeed()
   {
      return randSeed;
   }

   public void setRandSeed(int randSeed)
   {
      this.randSeed = randSeed;
   }

   public File getEntityTypesFile()
   {
      return entityTypesFile;
   }

   public void setEntityTypesFile(File entityTypesFile)
   {
      this.entityTypesFile = entityTypesFile;
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

   public int getNumRows()
   {
      return numRows;
   }

   public void setNumRows(int numRows)
   {
      this.numRows = numRows;
   }

   public int getNumColumns()
   {
      return numColumns;
   }

   public void setNumColumns(int numColumns)
   {
      this.numColumns = numColumns;
   }

   public int getNumMobileTargets()
   {
      return numMobileTargets;
   }

   public void setNumMobileTargets(int numMobileTargets)
   {
      this.numMobileTargets = numMobileTargets;
   }

   public int getNumStaticTargets()
   {
      return numStaticTargets;
   }

   public void setNumStaticTargets(int numStaticTargets)
   {
      this.numStaticTargets = numStaticTargets;
   }

   public int getNumUAVs()
   {
      return numUAVs;
   }

   public void setNumUAVs(int numUAVs)
   {
      this.numUAVs = numUAVs;
   }

}
