package thesis.core.serialization.entities;

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

import thesis.core.entities.Sensor;
import thesis.core.entities.SensorType;
import thesis.core.entities.TargetType;
import thesis.core.entities.UAVType;
import thesis.core.entities.Weapon;
import thesis.core.entities.WeaponType;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.Utils;

/**
 * Static class wrapping serialization utilities for reading and writing a
 * {@link EntityTypes} to streams.
 */
public class EntityTypesFile
{
   /**
    * Attempt to load a {@link EntityTypes} from disk.
    *
    * @param typesFile
    *           The type data file to read from disk.
    * @return A {@link EntityTypes} initialized with the data from the file or
    *         null if the reading the file failed.
    * @throws FileNotFoundException
    *            Thrown if <i>typesFile</i> does not point to an existing file.
    */
   public static EntityTypes loadTypes(File typesFile) throws FileNotFoundException
   {
      if (typesFile == null)
      {
         throw new NullPointerException("typesFile cannot be null.");
      }

      if (!typesFile.exists())
      {
         throw new FileNotFoundException("Types file does not exist. File: " + typesFile.getAbsolutePath());
      }

      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      logger.info("Loading entity types file {}", typesFile);

      return loadTypes(new FileInputStream(typesFile));
   }

   /**
    * Attempt to load a {@link EntityTypes} from an input stream.
    *
    * @param typesStream
    *           Data will be read from this stream.
    * @return A {@link EntityTypes} initialized with the data from the stream or
    *         null if the reading the stream failed.
    */
   public static EntityTypes loadTypes(InputStream typesStream)
   {
      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      EntityTypes types = null;

      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse(typesStream);

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
            logger.warn("Application version and entity types version do not match.  Unexpected errors may occur!");
         }

         EntityTypes tempTypes = new EntityTypes();

         decodeSensorTypes(tempTypes, (Element) root.getElementsByTagName("SensorTypes").item(0));
         decodeWeaponTypes(tempTypes, (Element) root.getElementsByTagName("WeaponTypes").item(0));
         decodeTargetTypes(tempTypes, (Element) root.getElementsByTagName("TargetTypes").item(0));
         //Must be run after decoding weapon and sensor types
         decodeUAVTypes(tempTypes, (Element) root.getElementsByTagName("UAVTypes").item(0));

         // Only initialize the returned entity types after successfully parsing
         // all data otherwise we may try to init the entity types model with
         // incomplete information.
         types = tempTypes;
      }
      catch (ParserConfigurationException | SAXException | IOException e)
      {
         logger.error("Failed to load {}.  Details: {}", typesStream, e.getLocalizedMessage());
      }

      return types;
   }

   /**
    * Save the given entity types data into the given file.
    *
    * @param typesFile
    *           Where to save the data.
    * @param types
    *           The data to write to the file.
    * @throws IOException
    * @return True upon successfully writing the data, false otherwise.
    */
   public static boolean saveTypes(File typesFile, EntityTypes types) throws IOException
   {
      if (typesFile == null)
      {
         throw new NullPointerException("typesFile cannot be null.");
      }

      if (!typesFile.exists())
      {
         typesFile.createNewFile();
      }

      Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
      logger.info("Saving entity types file {}", typesFile);

      return saveTypes(new FileOutputStream(typesFile), types);
   }

   /**
    * Save the given entity types data into the given stream.
    *
    * @param outStream
    *           The data will be written to this stream.
    * @param types
    *           The data to save.
    * @return True upon successfully writing the data, false otherwise.
    */
   public static boolean saveTypes(OutputStream outStream, EntityTypes types)
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

         Element root = document.createElement("EntityTypes");
         root.setAttribute("version", Utils.loadVersionID().toString());

         root.appendChild(encodeSensorTypes(types, document));
         root.appendChild(encodeWeaponTypes(types, document));
         root.appendChild(encodeTargetTypes(types, document));
         root.appendChild(encodeUAVTypes(types, document));

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

   private static void decodeSensorTypes(EntityTypes entTypes, Element parentElem)
   {
      NodeList nodeList = parentElem.getElementsByTagName("SensorType");
      final int numNodes = nodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element typeElem = (Element) nodeList.item(i);

         int type = Integer.parseInt(typeElem.getAttribute("type"));
         double minRngM = Double.parseDouble(typeElem.getAttribute("minRng"));
         double maxRngM = Double.parseDouble(typeElem.getAttribute("maxRng"));
         double fovDeg = Double.parseDouble(typeElem.getAttribute("fov"));
         double slewDegSec = Double.parseDouble(typeElem.getAttribute("maxSlew"));

         SensorType st = new SensorType(type);
         st.getMinRange().setAsMeters(minRngM);
         st.getMaxRange().setAsMeters(maxRngM);
         st.getFov().setAsDegrees(fovDeg);
         st.getMaxSlewRate().setAsDegreesPerSecond(slewDegSec);

         entTypes.getSensorTypes().add(st);
      }
   }

   private static Element encodeSensorTypes(EntityTypes entTypes, Document dom)
   {
      Element parentElem = dom.createElement("SensorTypes");
      for (SensorType st : entTypes.getSensorTypes())
      {
         Element elem = dom.createElement("SensorType");
         elem.setAttribute("type", Integer.toString(st.getTypeID()));
         elem.setAttribute("minRng", Double.toString(st.getMinRange().asMeters()));
         elem.setAttribute("maxRng", Double.toString(st.getMaxRange().asMeters()));
         elem.setAttribute("fov", Double.toString(st.getFov().asDegrees()));
         elem.setAttribute("maxSlew", Double.toString(st.getMaxSlewRate().asDegreesPerSecond()));

         parentElem.appendChild(elem);
      }
      return parentElem;
   }

   private static void decodeWeaponTypes(EntityTypes entTypes, Element parentElem)
   {
      NodeList nodeList = parentElem.getElementsByTagName("WeaponType");
      final int numNodes = nodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element typeElem = (Element) nodeList.item(i);

         int type = Integer.parseInt(typeElem.getAttribute("type"));
         double minRngM = Double.parseDouble(typeElem.getAttribute("minRng"));
         double maxRngM = Double.parseDouble(typeElem.getAttribute("maxRng"));
         double fovDeg = Double.parseDouble(typeElem.getAttribute("fov"));

         WeaponType wt = new WeaponType(type);
         wt.getMinRange().setAsMeters(minRngM);
         wt.getMaxRange().setAsMeters(maxRngM);
         wt.getFov().setAsDegrees(fovDeg);

         entTypes.getWeaponTypes().add(wt);
      }
   }

   private static Element encodeWeaponTypes(EntityTypes entTypes, Document dom)
   {
      Element parentElem = dom.createElement("WeaponTypes");
      for (WeaponType wt : entTypes.getWeaponTypes())
      {
         Element elem = dom.createElement("WeaponType");
         elem.setAttribute("type", Integer.toString(wt.getTypeID()));
         elem.setAttribute("minRng", Double.toString(wt.getMinRange().asMeters()));
         elem.setAttribute("maxRng", Double.toString(wt.getMaxRange().asMeters()));
         elem.setAttribute("fov", Double.toString(wt.getFov().asDegrees()));

         parentElem.appendChild(elem);
      }
      return parentElem;
   }

   /**
    * Decode the UAV type data. Must be run AFTER decoding weapons and sensor
    * types.
    *
    * @param entTypes
    *           The parsed {@link UAVType} will be loaded into this container.
    * @param parentElem
    *           The XML element to parse containing UAV type information.
    */
   private static void decodeUAVTypes(EntityTypes entTypes, Element parentElem)
   {
      NodeList uavTypesNodeList = parentElem.getElementsByTagName("UAVType");
      for (int i = 0; i < uavTypesNodeList.getLength(); ++i)
      {
         Element typeElem = (Element) uavTypesNodeList.item(i);

         // Load UAV type specific data
         int type = Integer.parseInt(typeElem.getAttribute("type"));
         double spdM = Double.parseDouble(typeElem.getAttribute("maxSpd"));
         double maxTurnRtDegSec = Double.parseDouble(typeElem.getAttribute("maxTurnRt"));

         UAVType uavType = new UAVType(type);
         uavType.getMaxSpd().setAsMetersPerSecond(spdM);
         uavType.getMaxTurnRt().setAsDegreesPerSecond(maxTurnRtDegSec);

         // Load sensor data for the uav
         Element sensorsElem = (Element) typeElem.getElementsByTagName("Sensors").item(0);
         NodeList sensorsNodeList = sensorsElem.getElementsByTagName("Sensor");
         for (int j = 0; j < sensorsNodeList.getLength(); ++j)
         {
            Element sensorElem = (Element) sensorsNodeList.item(j);
            int sensorTypeID = Integer.parseInt(sensorElem.getAttribute("type"));
            SensorType st = entTypes.getSensorType(sensorTypeID);
            if (st != null)
            {
               uavType.getSensors().add(new Sensor(st));
            }
            else
            {
               Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
               logger.error("Referential integrity error.  There is no corresponding sensor type with an ID of {}.",
                     sensorTypeID);
            }
         }

         // Load weapon data for the uav
         Element weaponsElem = (Element) typeElem.getElementsByTagName("Weapons").item(0);
         NodeList weaponsNodeList = weaponsElem.getElementsByTagName("Weapon");
         for (int j = 0; j < weaponsNodeList.getLength(); ++j)
         {
            Element weaponElem = (Element) weaponsNodeList.item(j);
            int weaponTypeID = Integer.parseInt(weaponElem.getAttribute("type"));
            int quantity = Integer.parseInt(weaponElem.getAttribute("initQty"));

            WeaponType wt = entTypes.getWeaponType(weaponTypeID);
            if (wt != null)
            {
               Weapon wpn = new Weapon(wt);
               wpn.setQuantity(quantity);
               uavType.getWeapons().add(wpn);
            }
            else
            {
               Logger logger = LoggerFactory.getLogger(LoggerIDs.UTILS);
               logger.error("Referential integrity error.  There is no corresponding weapon type with an ID of {}.",
                     weaponTypeID);
            }
         }

         entTypes.getUAVTypes().add(uavType);
      }
   }

   private static Element encodeUAVTypes(EntityTypes entTypes, Document dom)
   {
      Element parentElem = dom.createElement("UAVTypes");
      for (UAVType uavType : entTypes.getUAVTypes())
      {
         Element uavTypeElem = dom.createElement("UAVType");
         uavTypeElem.setAttribute("type", Integer.toString(uavType.getTypeID()));
         uavTypeElem.setAttribute("maxSpd", Double.toString(uavType.getMaxSpd().asMeterPerSecond()));
         uavTypeElem.setAttribute("maxTurnRt", Double.toString(uavType.getMaxTurnRt().asDegreesPerSecond()));

         Element sensorsElem = dom.createElement("Sensors");
         for (Sensor sensor : uavType.getSensors())
         {
            Element sensorElem = dom.createElement("Sensor");
            sensorElem.setAttribute("type", Integer.toString(sensor.getType().getTypeID()));
            sensorsElem.appendChild(sensorElem);
         }
         uavTypeElem.appendChild(sensorsElem);

         Element weaponsElem = dom.createElement("Weapons");
         for (Weapon weapon : uavType.getWeapons())
         {
            Element weaponElem = dom.createElement("Weapon");
            weaponElem.setAttribute("type", Integer.toString(weapon.getType().getTypeID()));
            weaponElem.setAttribute("initQty", Integer.toString(weapon.getQuantity()));
            weaponsElem.appendChild(weaponElem);
         }
         uavTypeElem.appendChild(weaponsElem);

         parentElem.appendChild(uavTypeElem);
      }
      return parentElem;
   }

   private static void decodeTargetTypes(EntityTypes entTypes, Element parentElem)
   {
      NodeList nodeList = parentElem.getElementsByTagName("TargetType");
      final int numNodes = nodeList.getLength();
      for (int i = 0; i < numNodes; ++i)
      {
         Element typeElem = (Element) nodeList.item(i);

         int type = Integer.parseInt(typeElem.getAttribute("type"));
         double maxSpd = Double.parseDouble(typeElem.getAttribute("maxSpd"));

         TargetType tt = new TargetType(type);
         tt.getMaxSpeed().setAsMetersPerSecond(maxSpd);

         entTypes.getTargetTypes().add(tt);
      }
   }

   private static Element encodeTargetTypes(EntityTypes entTypes, Document dom)
   {
      Element parentElem = dom.createElement("TargetTypes");
      for (TargetType tt : entTypes.getTargetTypes())
      {
         Element elem = dom.createElement("TargetType");
         elem.setAttribute("type", Integer.toString(tt.getTypeID()));
         elem.setAttribute("maxSpd", Double.toString(tt.getMaxSpeed().asMeterPerSecond()));

         parentElem.appendChild(elem);
      }
      return parentElem;
   }
}
