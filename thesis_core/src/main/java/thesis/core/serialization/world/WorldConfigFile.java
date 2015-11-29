package thesis.core.serialization.world;

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
import javax.xml.transform.OutputKeys;
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
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.DirectedEdge;
import thesis.core.common.graph.Graph;
import thesis.core.common.graph.Vertex;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.CoreUtils;

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
    * @return A {@link WorldConfig} initialized with the data from the stream or
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

         if (!CoreUtils.loadVersionID().isMatch(root.getAttribute("version")))
         {
            logger.warn("Application version and world config version do not match.  Unexpected errors may occur!");
         }

         WorldConfig tempCfg = new WorldConfig();

         decodeSizeElement(tempCfg, (Element) root.getElementsByTagName("Size").item(0));

         decodeRoadNetwork(tempCfg, (Element) root.getElementsByTagName("RoadNetwork").item(0));
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
      FileOutputStream fos = new FileOutputStream(cfgFile);
      boolean success = saveConfig(fos, cfg);
      fos.close();
      return success;
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
         root.setAttribute("version", CoreUtils.loadVersionID().toString());

         root.appendChild(encodeSizeElement(cfg, document));
         root.appendChild(encodeRoadNetwork(cfg, document));
         root.appendChild(encodeHavens(cfg, document));
         root.appendChild(encodeTargets(cfg, document));
         root.appendChild(encodeUAVs(cfg, document));

         document.appendChild(root);

         // Write the document to the outputstream
         TransformerFactory tFactory = TransformerFactory.newInstance();
         Transformer transformer = tFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

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
      cfg.setNumRows(Integer.parseInt(sizeElem.getAttribute("rows")));
      cfg.setNumColumns(Integer.parseInt(sizeElem.getAttribute("cols")));

      cfg.getWorldWidth().copy(width);
      cfg.getWorldHeight().copy(height);
   }

   private static Element encodeSizeElement(WorldConfig cfg, Document dom)
   {
      Element sizeElem = dom.createElement("Size");
      sizeElem.setAttribute("w", Double.toString(cfg.getWorldWidth().asMeters()));
      sizeElem.setAttribute("h", Double.toString(cfg.getWorldHeight().asMeters()));
      sizeElem.setAttribute("rows", Integer.toString(cfg.getNumRows()));
      sizeElem.setAttribute("cols", Integer.toString(cfg.getNumColumns()));
      return sizeElem;
   }

   private static void decodeRoadNetwork(WorldConfig cfg, Element roadNetElem)
   {
      decodeRoadVertices(cfg.getRoadNetwork(), roadNetElem);
      decodeRoadEdges(cfg.getRoadNetwork(), roadNetElem);
   }

   /**
    * Scan the road network element for all vertices.
    *
    * @param roadNet
    *           Vertices will be added to this graph.
    * @param roadNetElem
    *           Scan this element and its children for vertex data.
    */
   private static void decodeRoadVertices(Graph<WorldCoordinate> roadNet, Element roadNetElem)
   {
      NodeList vertexNodeList = roadNetElem.getElementsByTagName("Vertex");
      final int numNodes = vertexNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element vertexElem = (Element) vertexNodeList.item(i);

         int id = Integer.parseInt(vertexElem.getAttribute("id"));
         Vertex<WorldCoordinate> vert = roadNet.createVertex(id);
         vert.setUserData(coordFromAttr(vertexElem));
      }
   }

   /**
    * Scan the road network element for all the edges connecting the vertices.
    * Must be called AFTER
    * {@link WorldConfigFile#decodeRoadVertices(Graph, Element)} otherwise edge
    * construction will fail because the vertex end points do not yet exist.
    *
    * @param roadNet
    *           Edges will be added to this graph.
    * @param roadNetElem
    *           Scan this element and its children for edge data.
    * @see WorldConfigFile#decodeRoadVertices(Graph, Element)
    */
   private static void decodeRoadEdges(Graph<WorldCoordinate> roadNet, Element roadNetElem)
   {
      NodeList vertexNodeList = roadNetElem.getElementsByTagName("Vertex");
      final int numNodes = vertexNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element vertexElem = (Element) vertexNodeList.item(i);
         int vertID = Integer.parseInt(vertexElem.getAttribute("id"));

         Element incomingEdgesElem = (Element) vertexElem.getElementsByTagName("IncomingEdges").item(0);
         NodeList incomingNodeList = incomingEdgesElem.getElementsByTagName("IncomingEdge");
         final int numIncoming = incomingNodeList.getLength();
         for (int j = 0; j < numIncoming; ++j)
         {
            Element edgeElem = (Element) incomingNodeList.item(j);
            double cost = Double.parseDouble(edgeElem.getAttribute("cost"));
            int startID = Integer.parseInt(edgeElem.getAttribute("startID"));
            roadNet.createDirectionalEdge(startID, vertID, cost);
         }

         Element outgoingEdgesElem = (Element) vertexElem.getElementsByTagName("OutgoingEdges").item(0);
         NodeList outgoingNodeList = outgoingEdgesElem.getElementsByTagName("OutgoingEdge");
         final int numOutgoing = outgoingNodeList.getLength();
         for (int j = 0; j < numOutgoing; ++j)
         {
            Element edgeElem = (Element) outgoingNodeList.item(j);
            double cost = Double.parseDouble(edgeElem.getAttribute("cost"));
            int endID = Integer.parseInt(edgeElem.getAttribute("endID"));
            roadNet.createDirectionalEdge(vertID, endID, cost);
         }
      }
   }

   private static Element encodeRoadNetwork(WorldConfig cfg, Document dom)
   {
      Element roadNetElem = dom.createElement("RoadNetwork");

      for (Vertex<WorldCoordinate> vert : cfg.getRoadNetwork().getVertices())
      {
         Element vertElem = dom.createElement("Vertex");
         vertElem.setAttribute("id", Integer.toString(vert.getID()));
         vertElem.setAttribute("north", Double.toString(vert.getUserData().getNorth().asMeters()));
         vertElem.setAttribute("east", Double.toString(vert.getUserData().getEast().asMeters()));
         roadNetElem.appendChild(vertElem);

         Element incomingEdges = dom.createElement("IncomingEdges");
         for (DirectedEdge<WorldCoordinate> incoming : vert.getIncomingEdges())
         {
            Element edge = dom.createElement("IncomingEdge");
            edge.setAttribute("startID", Integer.toString(incoming.getStartVertex().getID()));
            edge.setAttribute("cost", Double.toString(incoming.getCost()));

            incomingEdges.appendChild(edge);
         }
         vertElem.appendChild(incomingEdges);

         Element outgoingEdges = dom.createElement("OutgoingEdges");
         for (DirectedEdge<WorldCoordinate> outgoing : vert.getOutgoingEdges())
         {
            Element edge = dom.createElement("OutgoingEdge");
            edge.setAttribute("endID", Integer.toString(outgoing.getEndVertex().getID()));
            edge.setAttribute("cost", Double.toString(outgoing.getCost()));

            outgoingEdges.appendChild(edge);
         }
         vertElem.appendChild(outgoingEdges);
      }

      return roadNetElem;
   }

   private static void decodeHavens(WorldConfig cfg, Element havensElem)
   {
      NodeList haveNodeList = havensElem.getElementsByTagName("Haven");
      final int numNodes = haveNodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element havenElem = (Element) haveNodeList.item(i);
         cfg.getHavens().add(coordFromAttr(havenElem));
      }
   }

   private static Element encodeHavens(WorldConfig cfg, Document dom)
   {
      Element havens = dom.createElement("Havens");

      for (WorldCoordinate havenCoord : cfg.getHavens())
      {
         Element havenElem = dom.createElement("Haven");
         havenElem.setAttribute("north", Double.toString(havenCoord.getNorth().asMeters()));
         havenElem.setAttribute("east", Double.toString(havenCoord.getEast().asMeters()));
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
         int type = Integer.parseInt(tarElem.getAttribute("type"));
         double orient = Double.parseDouble(tarElem.getAttribute("orientation"));

         TargetEntityConfig tarCfg = new TargetEntityConfig();
         tarCfg.getLocation().setCoordinate(coordFromAttr(tarElem));
         tarCfg.getOrientation().setAsDegrees(orient);
         tarCfg.setTargetType(type);

         cfg.targetCfgs.add(tarCfg);
      }
   }

   private static Element encodeTargets(WorldConfig cfg, Document dom)
   {
      Element targets = dom.createElement("Targets");

      for (TargetEntityConfig tarCfg : cfg.targetCfgs)
      {
         Element tarElem = dom.createElement("Target");
         tarElem.setAttribute("north", Double.toString(tarCfg.getLocation().getNorth().asMeters()));
         tarElem.setAttribute("east", Double.toString(tarCfg.getLocation().getEast().asMeters()));
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
         int type = Integer.parseInt(uavElem.getAttribute("type"));
         double orient = Double.parseDouble(uavElem.getAttribute("orientation"));

         UAVEntityConfig uavCfg = new UAVEntityConfig();
         uavCfg.getLocation().setCoordinate(coordFromAttr(uavElem));
         uavCfg.getOrientation().setAsDegrees(orient);
         uavCfg.setUAVType(type);

         cfg.uavCfgs.add(uavCfg);
      }
   }

   private static Element encodeUAVs(WorldConfig cfg, Document dom)
   {
      Element uavs = dom.createElement("UAVs");

      for (UAVEntityConfig uavCfg : cfg.uavCfgs)
      {
         Element uavElem = dom.createElement("UAV");
         uavElem.setAttribute("north", Double.toString(uavCfg.getLocation().getNorth().asMeters()));
         uavElem.setAttribute("east", Double.toString(uavCfg.getLocation().getEast().asMeters()));
         uavElem.setAttribute("type", Integer.toString(uavCfg.getUAVType()));
         uavElem.setAttribute("orientation", Double.toString(uavCfg.getOrientation().asDegrees()));
         uavs.appendChild(uavElem);
      }
      return uavs;
   }

   private static WorldCoordinate coordFromAttr(Element coordElem)
   {
      return coordFromAttr(coordElem.getAttribute("north"), coordElem.getAttribute("east"));
   }

   private static WorldCoordinate coordFromAttr(String northAttr, String eastAttr)
   {
      double northM = Double.parseDouble(northAttr);
      double eastM = Double.parseDouble(eastAttr);

      Distance north = new Distance();
      Distance east = new Distance();

      north.setAsMeters(northM);
      east.setAsMeters(eastM);

      WorldCoordinate wc = new WorldCoordinate();
      wc.setCoordinate(north, east);
      return wc;
   }
}
