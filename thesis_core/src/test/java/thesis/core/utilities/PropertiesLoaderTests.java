package thesis.core.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

public class PropertiesLoaderTests
{
   @Test
   public void loadFile()
   {
      InputStream testFile = this.getClass().getResourceAsStream("propsLoaderTest.properties");

      PropertiesLoader testMe = new PropertiesLoader();

      assertTrue("Failed to load properties test file.", testMe.loadFile(testFile));
   }

   @Test
   public void getInt()
   {
      InputStream testFile = this.getClass().getResourceAsStream("propsLoaderTest.properties");
      PropertiesLoader testMe = new PropertiesLoader();
      testMe.loadFile(testFile);
      assertEquals("Failed to load integer", 42, testMe.getInt("test.int", 1));
   }

   @Test
   public void getDouble()
   {
      InputStream testFile = this.getClass().getResourceAsStream("propsLoaderTest.properties");
      PropertiesLoader testMe = new PropertiesLoader();
      testMe.loadFile(testFile);
      assertEquals("Failed to load double", 123.456, testMe.getDouble("test.double", 96.354), 0.00001);
   }

   @Test
   public void getString()
   {
      InputStream testFile = this.getClass().getResourceAsStream("propsLoaderTest.properties");
      PropertiesLoader testMe = new PropertiesLoader();
      testMe.loadFile(testFile);
      assertEquals("Failed to load string", "yay thesis!", testMe.getString("test.string", "fail"));
   }
}
