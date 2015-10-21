package thesis.worldgen;

import java.io.File;

public class GenConfig
{
	private int numWorlds;
	private File outputDir;
	private int randSeed;

	public GenConfig()
	{

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

}
