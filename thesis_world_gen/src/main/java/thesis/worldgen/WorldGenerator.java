package thesis.worldgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.Graph;
import thesis.core.entities.uav.UAVType;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.TargetEntityConfig;
import thesis.core.serialization.world.UAVEntityConfig;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;

public class WorldGenerator
{
   /**
    * The minimum allowed distance between two intersections is proportional to
    * the min(width,height) * this percentage.
    */
   private static final double MIN_INTERSECTION_SPACING_PERCENT = 0.15f;

   private Random randGen;

   /**
    * Width of the world in meters.
    */
   private double width;

   /**
    * Height of the world in meters.
    */
   private double height;

   /**
    * Number of rows in the world.
    */
   private int numRows;

   /**
    * Number of columns in the world.
    */
   private int numCols;

   public WorldGenerator(int randSeed, double width, double height, int numRows, int numCols)
   {
      this.randGen = new Random(randSeed);
      this.width = width;
      this.height = height;
      this.numRows = numRows;
      this.numCols = numCols;
   }

   public WorldConfig generateWorld(EntityTypes entTypes, int numMobileTgts, int numStaticTgts, int numUAVs)
   {
      if (entTypes == null)
      {
         throw new NullPointerException("EntityTypes cannot be null.");
      }

      WorldConfig world = new WorldConfig();
      world.setWorldHeight(height);
      world.setWorldWidth(width);
      world.setNumColumns(numCols);
      world.setNumRows(numRows);

      RoadNetGenerator roadNetGen = new RoadNetGenerator(numRows, numCols);
      roadNetGen.gen(randGen, world.getRoadNetwork());
      world.getHavens().addAll(generateHavens(world.getRoadNetwork()));

      generateTargets(world, entTypes, numMobileTgts, numStaticTgts);
      generateUAVs(world, entTypes, numUAVs);

      return world;
   }

   /**
    * Randomly selects locations along the roads to place havens.
    */
   private Set<WorldCoordinate> generateHavens(Graph<WorldCoordinate> roadNet)
   {
      Set<WorldCoordinate> havens = new HashSet<WorldCoordinate>();

      // This percentage of grid cells will contain safe havens for targets
      final double percentHavenCells = 0.05;
      int numVertices = roadNet.getNumVertices();
      int numHavens = (int) (numVertices * percentHavenCells);
      numHavens = Math.max(numHavens, 3);// Require at least 3 havens

      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.debug("Generating {} safe havens.", numHavens);

      // Generate the haven locations on the roads
      for (int i = 0; i < numHavens; ++i)
      {
         int index = randGen.nextInt(numVertices);
         WorldCoordinate havenCoord = roadNet.getVertexByID(index).getUserData();
         // In case we randomly generate two havens at the same location,
         // move the second one
         while (havens.contains(havenCoord))
         {
            index = randGen.nextInt(numVertices);
            havenCoord = roadNet.getVertexByID(index).getUserData();
         }
         havens.add(havenCoord);
      }
      return havens;
   }

   private void generateUAVs(WorldConfig world, EntityTypes entTypes, int numUAVs)
   {
      final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

      if (numUAVs == 0)
      {
         logger.error("No UAVs in the world!!!!!!");
         return;
      }

      final double maxEastM = world.getWorldWidth();
      final double maxNorthM = world.getWorldHeight();

      List<UAVType> types = new ArrayList<UAVType>(entTypes.getAllUAVTypes());

      for (int i = 0; i < numUAVs; ++i)
      {
         final int typeIndex = randGen.nextInt(types.size());

         UAVEntityConfig uavCfg = new UAVEntityConfig();

         uavCfg.getLocation().setNorth(randGen.nextDouble() * maxNorthM);
         uavCfg.getLocation().setEast(randGen.nextDouble() * maxEastM);
         uavCfg.setOrientation(randGen.nextDouble() * 360);

         uavCfg.setUAVType(types.get(typeIndex).getTypeID());

         world.uavCfgs.add(uavCfg);
      }
   }

   /**
    * Randomly generate targets across the world.
    *
    * @param world
    *           Targets will be generated in this world.
    * @param entTypes
    *           The types of entities that can be randomly placed in the world.
    * @param numMobileTgts
    *           The number of mobile targets to generate.
    * @param numStaticTgts
    *           The number of static tarets to generate.
    */
   private void generateTargets(WorldConfig world, EntityTypes entTypes, int numMobileTgts, int numStaticTgts)
   {
      final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      final List<TargetType> mobileTypes = new ArrayList<TargetType>();
      final List<TargetType> staticTypes = new ArrayList<TargetType>();

      // Sort the target types between static and mobile targets
      for (TargetType tt : entTypes.getAllTargetTypes())
      {
         if (tt.isMobile())
         {
            mobileTypes.add(tt);
         }
         else
         {
            staticTypes.add(tt);
         }
      }

      if (numMobileTgts > 0 && mobileTypes.size() == 0)
      {
         logger.error(
               "{} mobile targets requested but there are not mobile entity types to use. Skipping mobile target requests.",
               numMobileTgts);
         numMobileTgts = 0;
      }

      if (numStaticTgts > 0 && staticTypes.size() == 0)
      {
         logger.error(
               "{} static targets requested but there are not mobile entity types to use. Skipping mobile target requests.",
               numMobileTgts);
         numStaticTgts = 0;
      }

      // Generate static targets
      for (int i = 0; i < numStaticTgts; ++i)
      {
         final int typeIndex = randGen.nextInt(staticTypes.size());

         TargetEntityConfig tgtCfg = generateRandomTarget(world);
         tgtCfg.setTargetType(staticTypes.get(typeIndex).getTypeID());

         world.targetCfgs.add(tgtCfg);
      }

      // Generate mobile targets
      for (int i = 0; i < numMobileTgts; ++i)
      {
         final int typeIndex = randGen.nextInt(mobileTypes.size());

         TargetEntityConfig tgtCfg = generateRandomTarget(world);
         tgtCfg.setTargetType(mobileTypes.get(typeIndex).getTypeID());

         world.targetCfgs.add(tgtCfg);
      }

   }

   /**
    * Generates a target entity configuration at a random location and
    * orientation. The caller must still set the target type.
    *
    * @param world
    *           Configuration data about the world. Used to bound the random
    *           location.
    * @return A randomly generated tareget entity configuration.
    */
   private TargetEntityConfig generateRandomTarget(WorldConfig world)
   {
      final double maxEastM = world.getWorldWidth();
      final double maxNorthM = world.getWorldHeight();

      TargetEntityConfig tgtCfg = new TargetEntityConfig();
      tgtCfg.getLocation().setNorth(randGen.nextDouble() * maxNorthM);
      tgtCfg.getLocation().setEast(randGen.nextDouble() * maxEastM);
      tgtCfg.setOrientation(randGen.nextDouble() * 360);

      return tgtCfg;
   }
}
