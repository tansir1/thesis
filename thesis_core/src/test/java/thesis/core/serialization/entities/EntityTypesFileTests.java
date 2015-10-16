package thesis.core.serialization.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import thesis.core.TestUtils;

public class EntityTypesFileTests
{

   @Test
   public void serializeTest()
   {
      EntityTypes testMe = new EntityTypes();
      
      final int NUM_SENSORS = 3;
      final int NUM_WEAPONS = 3;
      final int NUM_UAVS = 4;
      final int NUM_TARGETS = 7;
      
      for(int i=0; i<NUM_SENSORS; ++i)
      {
         testMe.getSensorTypes().add(TestUtils.randSensorType());   
      }
      
      for(int i=0; i<NUM_WEAPONS; ++i)
      {
         testMe.getWeaponTypes().add(TestUtils.randWeaponType());   
      }
      
      for (int i = 0; i < NUM_UAVS; ++i)
      {
         testMe.getUAVTypes().add(TestUtils.randUAVType(testMe.getWeaponTypes(), testMe.getSensorTypes()));
      }
      
      for (int i=0; i<NUM_TARGETS; ++i)
      {
         testMe.getTargetTypes().add(TestUtils.randTargetType());
      }
      
      File file = new File("et.xml");
      

      EntityTypes results = null;
      try
      {
         assertTrue("Failed to write to output stream.", EntityTypesFile.saveTypes(file, testMe));
         results = EntityTypesFile.loadTypes(file);
      }
      catch (FileNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }      
      
/*
      //Write the data to a byte buffer
      ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
      assertTrue("Failed to write to output stream.", EntityTypesFile.saveTypes(outBuff, testMe));
      
      //Read in the data buffer and parse it
      ByteArrayInputStream inBuff = new ByteArrayInputStream(outBuff.toByteArray());
      EntityTypes results = EntityTypesFile.loadTypes(inBuff);
      */
      for(int i=0; i<NUM_SENSORS; ++i)
      {
         assertEquals("Failed to read correct sensor type.", testMe.getSensorTypes().get(i), results.getSensorTypes().get(i));
      }
 
      for(int i=0; i<NUM_WEAPONS; ++i)
      {
         assertEquals("Failed to read correct weapon type.", testMe.getWeaponTypes().get(i), results.getWeaponTypes().get(i));
      }
      
      for(int i=0; i<NUM_UAVS; ++i)
      {
         assertEquals("Failed to read correct UAV type.", testMe.getUAVTypes().get(i), results.getUAVTypes().get(i));
      }
      
      for(int i=0; i<NUM_TARGETS; ++i)
      {
         assertEquals("Failed to read correct target type.", testMe.getTargetTypes().get(i), results.getTargetTypes().get(i));
      }
   }

}
