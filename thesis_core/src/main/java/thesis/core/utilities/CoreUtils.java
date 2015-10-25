package thesis.core.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of static utility methods.
 */
public class CoreUtils
{
	/**
	 * Export a resource embedded in the jar file to a local path on the hard
	 * disk.
	 *
	 * @param rscPath
	 *            Fully qualified path to the resource.
	 * @return True if the file exported successfully.
	 * @throws Exception
	 */
	public static boolean exportResource(String rscPath, File outputFile)
	{
		boolean success = true;
		Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);

		InputStream rscStr = null;
		OutputStream outStr = null;
		try
		{
			rscStr = ClassLoader.getSystemResourceAsStream(rscPath);
			if (rscStr == null)
			{
				logger.error("Could not find resource {} in the class loader.", rscPath);
			}
			else
			{
				byte[] buffer = new byte[4096];
				outStr = new FileOutputStream(outputFile);

				int numBytes = rscStr.read(buffer);
				while (numBytes > 0)
				{
					outStr.write(buffer, 0, numBytes);
					numBytes = rscStr.read(buffer);
				}
			}
		}
		catch (Exception e)
		{
			success = false;
			logger.warn("Failed to export resource {} to {}. Details: {}", rscPath, outputFile,
					e.getLocalizedMessage());
		}
		finally
		{
			if (rscStr != null)
			{
				try
				{
					rscStr.close();
				}
				catch (IOException e)
				{
					logger.warn("Failed to close stream to resource {}. Details: {}", rscPath, e.getLocalizedMessage());
				}
			}

			if (outStr != null)
			{
				try
				{
					outStr.close();
				}
				catch (IOException e)
				{
					logger.warn("Failed to close stream to output file {}. Details: {}", outputFile,
							e.getLocalizedMessage());
				}
			}
		}

		return success;
	}

	/**
	 * Parses the internal version.properties resource for the simulation
	 * version number.
	 *
	 * @return The simulation version number of -1.-1.-1 if the version data
	 *         failed to load.
	 */
	public static VersionID loadVersionID()
	{
		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
		VersionID versionID = new VersionID(-1, -1, -1);

		InputStream versProps = ClassLoader.getSystemResourceAsStream(CoreRsrcPaths.VERSION_PATH);
		if (versProps == null)
		{
			logger.warn("Failed to load simulation version information resource.");
		}
		else
		{
			PropertiesLoader propsLdr = new PropertiesLoader();
			if (propsLdr.loadFile(versProps))
			{
				try
				{
					int major = propsLdr.getInt("version.major");
					int minor = propsLdr.getInt("version.minor");
					int patch = propsLdr.getInt("version.patch");
					versionID = new VersionID(major, minor, patch);
				}
				catch (Exception e)
				{
					logger.warn("Failed to read simulation version data. Details: {}", e.getLocalizedMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				logger.warn("Failed to open simulation version data.");
			}
		}
		return versionID;
	}

	/**
	 * Get a stream to read an embedded resource.
	 *
	 * @param rscPath
	 *            Fully qualified path to the resource.
	 * @return The requested resource or null if no such resource was found.
	 *
	 * @see {@link CoreRsrcPaths}
	 */
	public static InputStream getResource(String rscPath)
	{
		InputStream stream = ClassLoader.getSystemResourceAsStream(rscPath);
		if (stream == null)
		{
			Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
			logger.debug("Failed to load requested resource: {}", rscPath);
		}
		return stream;
	}

	/**
	 * Load an embedded image resource.
	 *
	 * @param rscPath
	 *            The fully qualified path to the resource.
	 * @return The requested image or null if loading the resource failed.
	 */
	public static BufferedImage getResourceAsImage(String rscPath)
	{
		InputStream imgStream = getResource(rscPath);
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(imgStream);
		}
		catch (IOException e)
		{
			Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
			logger.debug("Failed to load image. Details: {}", e.getMessage());
		}
		return img;
	}
}
