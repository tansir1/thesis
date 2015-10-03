package thesis.core.utilities;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A static class to load the version ID information.
 */
public class VersionIDLoader
{
   /**
    * Parses the internal version.properties resource for the simulation version
    * number.
    * 
    * @return The simulation version number of -1.-1.-1 if the version data
    *         failed to load.
    */
   public static VersionID loadVersionID()
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      VersionID versionID = new VersionID(-1, -1, -1);

      InputStream versProps = ClassLoader.getSystemResourceAsStream("thesis/core/version.properties");
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
}
