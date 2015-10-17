package thesis.worldgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.Graph;
import thesis.core.common.graph.Vertex;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;

public class WorldGen
{
   private Random randGen;

   /**
    * Width of the world.
    */
   private Distance width;

   /**
    * Height of the world.
    */
   private Distance height;

   /**
    * Number of rows in the world.
    */
   private int numRows;

   /**
    *Number of columns in the world.
    */
   private int numCols;

   public WorldGen(int randSeed, Distance width, Distance height, int numRows, int numCols)
   {
      if (width == null)
      {
         throw new NullPointerException("World width cannot be null.");
      }

      if (height == null)
      {
         throw new NullPointerException("World height cannot be null.");
      }

      this.randGen = new Random(randSeed);
      this.width = width;
      this.height = height;
      this.numRows = numRows;
      this.numCols = numCols;
   }

   public WorldConfig generateWorld()
   {
      WorldConfig world = new WorldConfig();
      world.getWorldHeight().copy(height);
      world.getWorldWidth().copy(width);
      world.setNumColumns(numCols);
      world.setNumRows(numRows);

      world.setRoadNetwork(generateRoadNetwork());
      world.getHavens().addAll(generateHavens(world.getRoadNetwork()));

      return world;
   }

   private void kdNodeToRoadGroup(KDNode node, Graph<WorldCoordinate> roadNet)
   {
      WorldCoordinate root = node.getLocation();
      KDNode left = node.getLeftChild();
      KDNode right = node.getRightChild();

      //RoadGroup rootRG = new RoadGroup(root);
      Vertex<WorldCoordinate> rootVert = roadNet.createVertex(root);

      if (left != null)
      {
         WorldCoordinate intersection = computeRoadFromNode(root, left, node.isVerticalSplit());

         // Connect root to intermediate road intersection
         //rootRG.addDestination(intersection);
         Vertex<WorldCoordinate> vertInter = roadNet.createVertex(intersection);
         roadNet.createBidirectionalEdge(rootVert, vertInter, root.distanceTo(intersection).asMeters());

         // Connect intersection to the node location
         Vertex<WorldCoordinate> vertEnd = roadNet.createVertex(left.getLocation());
         roadNet.createBidirectionalEdge(vertInter, vertEnd, intersection.distanceTo(left.getLocation()).asMeters());
         //RoadGroup leftRG = new RoadGroup(intersection);
         //leftRG.addDestination(left.getLocation());
         //roadNetEdges.add(leftRG);

         // Recursively move down the tree
         kdNodeToRoadGroup(left, roadNet);
      }

      if (right != null)
      {
         WorldCoordinate intersection = computeRoadFromNode(root, right, node.isVerticalSplit());

         // Connect root to intermediate road intersection
         //rootRG.addDestination(intersection);
         Vertex<WorldCoordinate> vertInter = roadNet.createVertex(intersection);
         roadNet.createBidirectionalEdge(rootVert, vertInter, root.distanceTo(intersection).asMeters());

         // Connect intersection to the node location
         Vertex<WorldCoordinate> vertEnd = roadNet.createVertex(right.getLocation());
         roadNet.createBidirectionalEdge(vertInter, vertEnd, intersection.distanceTo(right.getLocation()).asMeters());
         //RoadGroup rightRG = new RoadGroup(intersection);
         //rightRG.addDestination(right.getLocation());
         //roadNetEdges.add(rightRG);

         // Recursively move down the tree
         kdNodeToRoadGroup(right, roadNet);
      }

      //roadNetEdges.add(rootRG);
   }

   private WorldCoordinate computeRoadFromNode(WorldCoordinate root, KDNode node, boolean isVertical)
   {
      WorldCoordinate intersection = null;

      if (isVertical)
      {
         intersection = new WorldCoordinate(node.getLocation().getNorth(), root.getEast());
      }
      else
      {
         intersection = new WorldCoordinate(root.getEast(), node.getLocation().getNorth());
      }

      return intersection;
   }

   /**
    * Checks if the new cell location satisfies all the rules for new road seed
    * generation.
    *
    * @param existingLocations
    *           All pre-existing road seed coordinates.
    * @param newLocation
    *           The potential new seed location to validate.
    * @return True if the new location is a valid location, false otherwise.
    */
   private boolean isValidRoadSeedLocation(List<WorldCoordinate> existingLocations, WorldCoordinate newLocation)
   {
      boolean valid = true;

      // Prevents the seeds from clustering together
      double interSeedDistBuffer = Math.min(width.asMeters(), height.asMeters()) * 0.1;

      if (existingLocations.contains(newLocation))
      {
         // Cannot put two seeds on top of each other
         valid = false;
      }

      for (WorldCoordinate otherSeed : existingLocations)
      {
         if (newLocation.distanceTo(otherSeed).asMeters() < interSeedDistBuffer)
         {
            valid = false;
            break;
         }
      }

      return valid;
   }

   /**
    * Randomly/procedurally generate a network of roads for the world.
    */
   private Graph<WorldCoordinate> generateRoadNetwork()
   {
      Graph<WorldCoordinate> roadNet = new Graph<WorldCoordinate>();

      // This percentage of grid cells will contain road seed locations
      final double percentRoadCells = 0.01;
      int numSeeds = (int) (numRows * numCols * percentRoadCells);
      numSeeds = Math.max(numSeeds, 4);

      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.debug("Generating road network with {} seeds.", numSeeds);

      List<WorldCoordinate> roadSeeds = new ArrayList<WorldCoordinate>();

      // Generate seed locations
      for (int i = 0; i < numSeeds; ++i)
      {
         Distance north = new Distance();
         Distance east = new Distance();

         north.setAsMeters(randGen.nextDouble() * height.asMeters());
         east.setAsMeters(randGen.nextDouble() * width.asMeters());

         WorldCoordinate seedCoord = new WorldCoordinate(north, east);
         while (!isValidRoadSeedLocation(roadSeeds, seedCoord))
         {
            // Regenerate a new location until we get a valid one
            north.setAsMeters(randGen.nextDouble() * height.asMeters());
            east.setAsMeters(randGen.nextDouble() * width.asMeters());
         }
         logger.debug("Road seed {} at {}.", i, seedCoord);
         roadSeeds.add(seedCoord);
      }

      // Generate all the roads (edges) in the road network (tree).
      kdNodeToRoadGroup(KDTree.generateTree(roadSeeds), roadNet);
      return roadNet;
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

      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.debug("Generating {} safe havens.", numHavens);

      // Generate the haven locations on the roads
      for (int i = 0; i < numHavens; ++i)
      {
         int index = randGen.nextInt(numVertices);
         WorldCoordinate havenCoord = roadNet.getVertexByID(index).getUserData();
         // In case we randomly generate two havens at the same location, move
         // the second one
         while (havens.contains(havenCoord))
         {
            index = randGen.nextInt(numVertices);
            havenCoord = roadNet.getVertexByID(index).getUserData();
         }
         havens.add(havenCoord);
      }
      return havens;
   }
}
