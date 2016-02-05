package thesis.worldgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.RoadNetwork;
import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.serialization.world.UAVStartCfg;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.uav.UAVTypeConfigs;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.Havens;
import thesis.core.world.WorldGIS;

public class WorldGenerator
{
   /**
    * The minimum allowed distance between two intersections is proportional to
    * the min(width,height) * this percentage.
    */
   private static final double MIN_INTERSECTION_SPACING_PERCENT = 0.15f;

   private Random randGen;

   private WorldGIS gis;

   public WorldGenerator(int randSeed, WorldGIS gis)
   {
      this.randGen = new Random(randSeed);
      this.gis = gis;
   }

   public WorldConfig generateWorld(EntityTypeCfgs entTypes, int numMobileTgts, int numStaticTgts, int numUAVs)
   {
      if (entTypes == null)
      {
         throw new NullPointerException("EntityTypes cannot be null.");
      }

      WorldConfig worldCfg = new WorldConfig();
      worldCfg.getWorld().getWorldGIS().copy(gis);

      RoadNetGenerator roadNetGen = new RoadNetGenerator(gis.getRowCount(), gis.getColumnCount());
      roadNetGen.generate(randGen, worldCfg.getWorld().getRoadNetwork());

      generateHavens(worldCfg.getWorld().getRoadNetwork(), worldCfg.getWorld().getHavens());

      generateTargets(worldCfg, entTypes.getTgtTypeCfgs(), numMobileTgts, numStaticTgts);
      generateUAVs(worldCfg, entTypes.getUAVTypeCfgs(), numUAVs);

      return worldCfg;
   }

   /**
    * Randomly selects locations along the roads to place havens.
    */
   private void generateHavens(RoadNetwork roadNet, Havens havensInWorld)
   {
      Set<CellCoordinate> havens = new HashSet<CellCoordinate>();
      List<CellCoordinate> traversable = roadNet.getTraversableCells();

      // This percentage of grid cells will contain safe havens for targets
      final double percentHavenCells = 0.05;
      int numVertices = traversable.size();
      int numHavens = (int) (numVertices * percentHavenCells);
      numHavens = Math.max(numHavens, 3);// Require at least 3 havens

      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.debug("Generating {} safe havens.", numHavens);

      // Generate the haven locations on the roads
      for (int i = 0; i < numHavens; ++i)
      {
         int index = randGen.nextInt(numVertices);
         CellCoordinate havenCoord = traversable.get(index);
         // In case we randomly generate two havens at the same location,
         // move the second one
         while (havens.contains(havenCoord))
         {
            index = randGen.nextInt(numVertices);
            havenCoord = traversable.get(index);
         }
         havens.add(havenCoord);
      }

      havensInWorld.reset(numHavens);
      Iterator<CellCoordinate> itr = havens.iterator();
      for(int i=0; i<numHavens; ++i)
      {
         havensInWorld.setHavenByIndx(i, itr.next());
      }
   }

   private void generateUAVs(WorldConfig worldCfg, UAVTypeConfigs uavTypes, int numUAVs)
   {
      final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);

      if (numUAVs == 0)
      {
         logger.error("No UAVs in the world!!!!!!");
         return;
      }

      for (int i = 0; i < numUAVs; ++i)
      {
         final int typeIndex = randGen.nextInt(uavTypes.getNumTypes());

         int row = randGen.nextInt(worldCfg.getWorld().getWorldGIS().getRowCount());
         int col = randGen.nextInt(worldCfg.getWorld().getWorldGIS().getColumnCount());

         UAVStartCfg uavStartCfg = new UAVStartCfg();
         uavStartCfg.setOrientation(randGen.nextDouble() * 360d);
         uavStartCfg.setUAVType(typeIndex);
         worldCfg.getWorld().getWorldGIS().convertCellToWorld(row, col, uavStartCfg.getLocation());

         worldCfg.getUAVCfgs().add(uavStartCfg);
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
   private void generateTargets(WorldConfig world, TargetTypeConfigs tgtTypeCfgs, int numMobileTgts, int numStaticTgts)
   {
      final Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      final List<Integer> mobileTypes = new ArrayList<Integer>();
      final List<Integer> staticTypes = new ArrayList<Integer>();

      // Sort the target types between static and mobile targets
      int NUM_TGT_TYPES = tgtTypeCfgs.getNumTypes();
      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         if (tgtTypeCfgs.isMobile(i))
         {
            mobileTypes.add(i);
         }
         else
         {
            staticTypes.add(i);
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
         generateRandomTarget(world, typeIndex);
      }

      // Generate mobile targets
      for (int i = 0; i < numMobileTgts; ++i)
      {
         final int typeIndex = randGen.nextInt(mobileTypes.size());
         generateRandomTarget(world, typeIndex);
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
   private void generateRandomTarget(WorldConfig world, int type)
   {
      int row = randGen.nextInt(world.getWorld().getWorldGIS().getRowCount());
      int col = randGen.nextInt(world.getWorld().getWorldGIS().getColumnCount());

      TargetStartCfg tgtStartCfg = new TargetStartCfg();
      tgtStartCfg.setOrientation(randGen.nextDouble() * 360d);
      tgtStartCfg.setTargetType(type);
      world.getWorld().getWorldGIS().convertCellToWorld(row, col, tgtStartCfg.getLocation());

      world.getTargetCfgs().add(tgtStartCfg);
   }
}
