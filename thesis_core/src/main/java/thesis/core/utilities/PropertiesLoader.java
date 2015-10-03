package thesis.core.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses a properties file and retrieves typecasted values from it.
 */
public class PropertiesLoader
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);

   private Properties props;

   public PropertiesLoader()
   {
      props = new Properties();
   }

   /**
    * Load a properties file.
    * 
    * @param propStream
    *           The properties file input stream to load.
    * @return True if the stream was loaded successfully, false otherwise.
    */
   public boolean loadFile(InputStream propStream)
   {
      boolean success = false;
      try
      {
         props.load(propStream);
         success = true;
      }
      catch (IOException e)
      {
         logger.error("Failed to read property file stream. Details: {}", e);
      }
      return success;
   }

   /**
    * Load a properties file.
    * 
    * @param propFile
    *           The properties file on disk to load.
    * @return True if the file was loaded successfully, false otherwise.
    */
   public boolean loadFile(File propFile)
   {
      boolean success = false;
      try
      {
         props.load(new FileInputStream(propFile));
         success = true;
      }
      catch (FileNotFoundException e)
      {
         logger.error("Property file {} does not exist.", propFile.getAbsolutePath());
      }
      catch (IOException e)
      {
         logger.error("Failed to read property file at {}. Details: {}", propFile.getAbsolutePath(), e);
      }
      return success;
   }

   /**
    * Read an integer value from the properties file.
    * 
    * @param key
    *           The key to the property to read.
    * @param dfltVal
    *           The default value to assign to the property if the key is not
    *           present.
    * @return The parsed value from the file or the default value if no parsed
    *         value exists.
    */
   public int getInt(String key, int dfltVal)
   {
      return Integer.parseInt(props.getProperty(key, Integer.toString(dfltVal)));
   }

   /**
    * Read an integer value from the properties file and throw an exception if
    * the value is not found.
    * 
    * @param key
    *           The key to the property to read.
    * @return The parsed value from the file.
    * @throws RuntimeException
    *            Thrown if the specified key is not found in the properties
    *            file.
    */
   public int getInt(String key) throws Exception
   {
      String propsVal = props.getProperty(key);
      if (propsVal == null)
      {
         throw new RuntimeException("No value found for key: " + key);
      }

      return Integer.parseInt(propsVal);
   }

   /**
    * Read a double value from the properties file.
    * 
    * @param key
    *           The key to the property to read.
    * @param dfltVal
    *           The default value to assign to the property if the key is not
    *           present.
    * @return The parsed value from the file or the default value if no parsed
    *         value exists.
    */
   public double getDouble(String key, double dfltVal)
   {
      return Double.parseDouble(props.getProperty(key, Double.toString(dfltVal)));
   }

   /**
    * Read a string value from the properties file.
    * 
    * @param key
    *           The key to the property to read.
    * @param dfltVal
    *           The default value to assign to the property if the key is not
    *           present.
    * @return The parsed value from the file or the default value if no parsed
    *         value exists.
    */
   public String getString(String key, String dfltVal)
   {
      return props.getProperty(key, dfltVal);
   }
}
