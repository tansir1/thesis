package thesis.core.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import thesis.core.common.Distance;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.Utils;
import thesis.core.world.RoadSegment;
import thesis.core.world.WorldCoordinate;

public class WorldConfigFile
{
   public static WorldConfig loadConfig(File cfgFile) throws FileNotFoundException
   {
      if (cfgFile == null)
      {
         throw new NullPointerException("cfgFile cannot be null.");
      }

      if (!cfgFile.exists())
      {
         throw new FileNotFoundException("Config file does not exist. File: " + cfgFile.getAbsolutePath());
      }
      
      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      logger.info("Loading world config file {}", cfgFile);
      
      return loadConfig(new FileInputStream(cfgFile));
   }

   public static WorldConfig loadConfig(InputStream cfgFile)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      WorldConfig cfg = null;

      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse(cfgFile);

         // TODO Define schema
         // Schema schema = null;
         // try
         // {
         // String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
         // SchemaFactory schemaFact = SchemaFactory.newInstance(language);
         // schema = schemaFact.newSchema(new File(name));
         // }
         // catch (Exception e)
         // {
         // e.printStackTrace();
         // }
         //
         // Validator validator = schema.newValidator();
         // validator.validate(new DOMSource(document));

         Element root = document.getDocumentElement();

         if (Utils.loadVersionID().isMatch(root.getAttribute("version")))
         {
            logger.warn("Application version and world config file version do not match.  Unexpected errors may occur!");
         }
         
         WorldConfig tempCfg = new WorldConfig();
         
         decodeSizeElement(tempCfg, (Element)root.getElementsByTagName("Size").item(0));

         Element randSeedElem = (Element)root.getElementsByTagName("RandSeed").item(0);
         tempCfg.randSeed = Integer.parseInt(randSeedElem.getTextContent());
         
         decodeRoadSegments(tempCfg, (Element)root.getElementsByTagName("RoadSegments").item(0));
         decodeHavens(tempCfg, (Element)root.getElementsByTagName("Havens").item(0));
         
         //Only initialize the returned world config after successfully parsing all data
         //otherwise we may try to init the world model with incomplete information.
         cfg = tempCfg;
      }
      catch (ParserConfigurationException | SAXException | IOException e)
      {
         logger.error("Failed to load {}.  Details: {}", cfgFile, e.getLocalizedMessage());
      }

      return cfg;
   }

   public void saveFile(File cfgFile)
   {

   }
   
   private static void decodeSizeElement(WorldConfig cfg, Element sizeElem)
   {
      Distance width = new Distance();
      Distance height = new Distance();
      
      width.setAsMeters(Integer.parseInt(sizeElem.getAttribute("w")));
      height.setAsMeters(Integer.parseInt(sizeElem.getAttribute("h")));
      cfg.numRows = Integer.parseInt(sizeElem.getAttribute("rows"));
      cfg.numColums = Integer.parseInt(sizeElem.getAttribute("cols"));

      cfg.width = width;
      cfg.height = height;
   }
   
   private static void decodeRoadSegments(WorldConfig cfg, Element roadSegsElem)
   {
      NodeList segNodeList = roadSegsElem.getElementsByTagName("RoadSegment");
      final int numNodes = segNodeList.getLength();
      for(int i=0; i<numNodes; ++i)
      {
         Element roadSegElem = (Element)segNodeList.item(i);
         int north1 = Integer.parseInt(roadSegElem.getAttribute("north1"));
         int north2 = Integer.parseInt(roadSegElem.getAttribute("north2"));
         int east1 = Integer.parseInt(roadSegElem.getAttribute("east1"));
         int east2 = Integer.parseInt(roadSegElem.getAttribute("east2"));
         
         RoadSegment roadSeg = new RoadSegment();
         roadSeg.getStart().setCoordinate(north1, east1);
         roadSeg.getEnd().setCoordinate(north2, east2);
         cfg.roadSegments.add(roadSeg);
      }
   }
   
   private static void decodeHavens(WorldConfig cfg, Element havensElem)
   {
      NodeList haveNodeList = havensElem.getElementsByTagName("Haven");
      final int numNodes = haveNodeList.getLength();
      for(int i=0; i<numNodes; ++i)
      {
         Element havenElem = (Element)haveNodeList.item(i);
         int north = Integer.parseInt(havenElem.getAttribute("north"));
         int east = Integer.parseInt(havenElem.getAttribute("east"));
         
         WorldCoordinate havenLocation = new WorldCoordinate();
         havenLocation.setCoordinate(north, east);
         cfg.havens.add(havenLocation);
      }
   }
}
