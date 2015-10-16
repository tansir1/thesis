package thesis.core.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

/**
 * Static class wrapping serialization utilities for reading and writing a
 * {@link WorldConfig} to streams.
 */
public class WorldConfigFile
{
   /**
    * Attempt to load a {@link WorldConfig} from disk.
    * 
    * @param cfgFile
    *           The config data file to read from disk.
    * @return A {@link WorldConfig} initialized with the data from the file or
    *         null if the reading the file failed.
    * @throws FileNotFoundException
    *            Thrown if <i>cfgFile</i> does not point to an existing file.
    */
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

   /**
    * Attempt to load a {@link WorldConfig} from an input stream.
    * 
    * @param cfgStream
    *           Data will be read from this stream.
    * @return A {@link WorldConfig} initialized with the data from the file or
    *         null if the reading the stream failed.
    */
   public static WorldConfig loadConfig(InputStream cfgStream)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      WorldConfig cfg = null;

      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse(cfgStream);

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

         if (!Utils.loadVersionID().isMatch(root.getAttribute("version")))
         {
            logger.warn(
                  "Application version and world config file version do not match.  Unexpected errors may occur!");
         }

         WorldConfig tempCfg = new WorldConfig();

         decodeSizeElement(tempCfg, (Element) root.getElementsByTagName("Size").item(0));

         Element randSeedElem = (Element) root.getElementsByTagName("RandSeed").item(0);
         tempCfg.randSeed = Integer.parseInt(randSeedElem.getTextContent());

         decodeRoadSegments(tempCfg, (Element) root.getElementsByTagName("RoadSegments").item(0));
         decodeHavens(tempCfg, (Element) root.getElementsByTagName("Havens").item(0));
         decodeTargets(tempCfg, (Element) root.getElementsByTagName("Targets").item(0));
         decodeUAVs(tempCfg, (Element) root.getElementsByTagName("UAVs").item(0));
         // Only initialize the returned world config after successfully parsing
         // all data otherwise we may try to init the world model with
         // incomplete information.
         cfg = tempCfg;
      }
      catch (ParserConfigurationException | SAXException | IOException e)
      {
         logger.error("Failed to load {}.  Details: {}", cfgStream, e.getLocalizedMessage());
      }

      return cfg;
   }

   /**
    * Save the given configuration data into the given file.
    * 
    * @param cfgFile
    *           Where to save the data.
    * @param cfg
    *           The data to write to the file.
    * @throws IOException
    * @return True upon successfully writing the data, false otherwise.
    */
   public static boolean saveConfig(File cfgFile, WorldConfig cfg) throws IOException
   {
      if (cfgFile == null)
      {
         throw new NullPointerException("cfgFile cannot be null.");
      }

      if (!cfgFile.exists())
      {
         cfgFile.createNewFile();
      }

      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      logger.info("Saving world config file {}", cfgFile);

      return saveConfig(new FileOutputStream(cfgFile), cfg);
   }

   /**
    * Save the given configuration data into the given stream.
    * 
    * @param outStream
    *           The data will be written to this stream.
    * @param cfg
    *           The data to save.
    * @return True upon successfully writing the data, false otherwise.
    */
   public static boolean saveConfig(OutputStream outStream, WorldConfig cfg)
   {
      boolean success = true;
      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);

      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.newDocument();

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

         Element root = document.createElement("World");
         root.setAttribute("version", Utils.loadVersionID().toString());

         Element randSeedElem = document.createElement("RandSeed");
         randSeedElem.setTextContent(Integer.toString(cfg.randSeed));
         root.appendChild(randSeedElem);

         root.appendChild(encodeSizeElement(cfg, document));
         root.appendChild(encodeRoadSegments(cfg, document));
         root.appendChild(encodeHavens(cfg, document));
         root.appendChild(encodeTargets(cfg, document));
         root.appendChild(encodeUAVs(cfg, document));

         document.appendChild(root);

         // Write the document to the outputstream
         TransformerFactory tFactory = TransformerFactory.newInstance();
         Transformer transformer = tFactory.newTransformer();

         DOMSource source = new DOMSource(document);
         StreamResult result = new StreamResult(outStream);
         transformer.transform(source, result);
      }
      catch (ParserConfigurationException | TransformerException e)
      {
         success = false;
         logger.error("Failed to save {}.  Details: {}", outStream, e.getLocalizedMessage());
      }
      return success;
   }

   private static void decodeSizeElement(WorldConfig cfg, Element sizeElem)
   {
      Distance width = new Distance();
      Distance height = new Distance();

      width.setAsMeters(Double.parseDouble(sizeElem.getAttribute("w")));
      height.setAsMeters(Double.parseDouble(sizeElem.getAttribute("h")));
      cfg.numRows = Integer.parseInt(sizeElem.getAttribute("rows"));
      cfg.numColums = Integer.parseInt(sizeElem.getAttribute("cols"));

      cfg.width = width;
      cfg.height = height;
   }

   private static Element encodeSizeElement(WorldConfig cfg, Document dom)
   {
      Element sizeElem = dom.createElement("Size");
      sizeElem.setAttribute("w", Double.toString(cfg.width.asMeters()));
      sizeElem.setAttribute("h", Double.toString(cfg.height.asMeters()));
      sizeElem.setAttribute("rows", Integer.toString(cfg.numRows));
      sizeElem.setAttribute("cols", Integer.toString(cfg.numColums));
      return sizeElem;
   }

   private static void decodeRoadSegments(WorldConfig cfg, Element roadSegsElem)
   {
      NodeList segNodeList = roadSegsElem.getElementsByTagName("RoadSegment");
      final int numNodes = segNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element roadSegElem = (Element) segNodeList.item(i);
         double north1 = Double.parseDouble(roadSegElem.getAttribute("north1"));
         double north2 = Double.parseDouble(roadSegElem.getAttribute("north2"));
         double east1 = Double.parseDouble(roadSegElem.getAttribute("east1"));
         double east2 = Double.parseDouble(roadSegElem.getAttribute("east2"));

         RoadSegment roadSeg = new RoadSegment();
         roadSeg.getStart().setCoordinate(north1, east1);
         roadSeg.getEnd().setCoordinate(north2, east2);
         cfg.roadSegments.add(roadSeg);
      }
   }

   private static Element encodeRoadSegments(WorldConfig cfg, Document dom)
   {
      Element roadSegsElem = dom.createElement("RoadSegments");

      for (RoadSegment rs : cfg.roadSegments)
      {
         Element roadSegElem = dom.createElement("RoadSegment");
         roadSegElem.setAttribute("north1", Double.toString(rs.getStart().getNorth()));
         roadSegElem.setAttribute("east1", Double.toString(rs.getStart().getEast()));
         roadSegElem.setAttribute("north2", Double.toString(rs.getEnd().getNorth()));
         roadSegElem.setAttribute("east2", Double.toString(rs.getEnd().getEast()));
         roadSegsElem.appendChild(roadSegElem);
      }
      return roadSegsElem;
   }

   private static void decodeHavens(WorldConfig cfg, Element havensElem)
   {
      NodeList haveNodeList = havensElem.getElementsByTagName("Haven");
      final int numNodes = haveNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element havenElem = (Element) haveNodeList.item(i);
         double north = Double.parseDouble(havenElem.getAttribute("north"));
         double east = Double.parseDouble(havenElem.getAttribute("east"));

         WorldCoordinate havenLocation = new WorldCoordinate();
         havenLocation.setCoordinate(north, east);
         cfg.havens.add(havenLocation);
      }
   }

   private static Element encodeHavens(WorldConfig cfg, Document dom)
   {
      Element havens = dom.createElement("Havens");

      for (WorldCoordinate havenCoord : cfg.havens)
      {
         Element havenElem = dom.createElement("Haven");
         havenElem.setAttribute("north", Double.toString(havenCoord.getNorth()));
         havenElem.setAttribute("east", Double.toString(havenCoord.getEast()));
         havens.appendChild(havenElem);
      }
      return havens;
   }

   private static void decodeTargets(WorldConfig cfg, Element targetsElem)
   {
      NodeList targetNodeList = targetsElem.getElementsByTagName("Target");
      final int numNodes = targetNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element tarElem = (Element) targetNodeList.item(i);
         double north = Double.parseDouble(tarElem.getAttribute("north"));
         double east = Double.parseDouble(tarElem.getAttribute("east"));
         int type = Integer.parseInt(tarElem.getAttribute("type"));
         double orient = Double.parseDouble(tarElem.getAttribute("orientation"));

         TargetConfig tarCfg = new TargetConfig();
         tarCfg.getLocation().setCoordinate(north, east);
         tarCfg.getOrientation().setAsDegrees(orient);
         tarCfg.setTargetType(type);

         cfg.targetCfgs.add(tarCfg);
      }
   }

   private static Element encodeTargets(WorldConfig cfg, Document dom)
   {
      Element targets = dom.createElement("Targets");

      for (TargetConfig tarCfg : cfg.targetCfgs)
      {
         Element tarElem = dom.createElement("Target");
         tarElem.setAttribute("north", Double.toString(tarCfg.getLocation().getNorth()));
         tarElem.setAttribute("east", Double.toString(tarCfg.getLocation().getEast()));
         tarElem.setAttribute("type", Integer.toString(tarCfg.getTargetType()));
         tarElem.setAttribute("orientation", Double.toString(tarCfg.getOrientation().asDegrees()));
         targets.appendChild(tarElem);
      }
      return targets;
   }

   private static void decodeUAVs(WorldConfig cfg, Element uavsElem)
   {
      NodeList uavNodeList = uavsElem.getElementsByTagName("UAV");
      final int numNodes = uavNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element uavElem = (Element) uavNodeList.item(i);
         double north = Double.parseDouble(uavElem.getAttribute("north"));
         double east = Double.parseDouble(uavElem.getAttribute("east"));
         int type = Integer.parseInt(uavElem.getAttribute("type"));
         double orient = Double.parseDouble(uavElem.getAttribute("orientation"));

         UAVConfig uavCfg = new UAVConfig();
         uavCfg.getLocation().setCoordinate(north, east);
         uavCfg.getOrientation().setAsDegrees(orient);
         uavCfg.setUAVType(type);

         cfg.uavCfgs.add(uavCfg);
      }
   }

   private static Element encodeUAVs(WorldConfig cfg, Document dom)
   {
      Element uavs = dom.createElement("UAVs");

      for (UAVConfig uavCfg : cfg.uavCfgs)
      {
         Element uavElem = dom.createElement("UAV");
         uavElem.setAttribute("north", Double.toString(uavCfg.getLocation().getNorth()));
         uavElem.setAttribute("east", Double.toString(uavCfg.getLocation().getEast()));
         uavElem.setAttribute("type", Integer.toString(uavCfg.getUAVType()));
         uavElem.setAttribute("orientation", Double.toString(uavCfg.getOrientation().asDegrees()));
         uavs.appendChild(uavElem);
      }
      return uavs;
   }
}
