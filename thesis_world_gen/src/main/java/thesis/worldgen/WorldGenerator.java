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

public class WorldGenerator
{
	/**
	 * The minimum allowed distance between two intersections is proportional to
	 * the min(width,height) * this percentage.
	 */
	private static final double MIN_INTERSECTION_SPACING_PERCENT = 0.15f;

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
	 * Number of columns in the world.
	 */
	private int numCols;

	public WorldGenerator(int randSeed, Distance width, Distance height, int numRows, int numCols)
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

	private void kdNodeToRoadGroup(Vertex<WorldCoordinate> vertex, KDNode node, Graph<WorldCoordinate> roadNet)
	{
		KDNode left = node.getLeftChild();
		KDNode right = node.getRightChild();

		if (left == null && right == null)
		{
			return;// End of the tree branch
		}

		//Vertex<WorldCoordinate> rootVert = roadNet.createVertex(node.getLocation());

		if (left != null)
		{
			insertIntermediateVertices(roadNet, vertex, left, node.isVerticalSplit());
		}

		if (right != null)
		{
			insertIntermediateVertices(roadNet, vertex, right, node.isVerticalSplit());
		}
	}

	private void insertIntermediateVertices(Graph<WorldCoordinate> roadNet, Vertex<WorldCoordinate> startVert, KDNode endNode, boolean isVertSplit)
	{
		final double minVertexDistBuf = Math.min(width.asMeters(), height.asMeters())
				* MIN_INTERSECTION_SPACING_PERCENT;

		WorldCoordinate intersection = computeRoadIntersectionFromNode(startVert.getUserData(), endNode, isVertSplit);

		final double distStartToInter = intersection.distanceTo(startVert.getUserData()).asMeters();
		final double distInterToEnd = intersection.distanceTo(endNode.getLocation()).asMeters();

		Vertex<WorldCoordinate> endVert = null;

		if (distStartToInter > minVertexDistBuf && distInterToEnd > minVertexDistBuf)
		{
			// Connect root to intermediate road intersection
			Vertex<WorldCoordinate> vertInter = roadNet.createVertex(intersection);
			roadNet.createBidirectionalEdge(startVert, vertInter, distStartToInter);

			// Connect intersection to the node location
			endVert = roadNet.createVertex(endNode.getLocation());
			roadNet.createBidirectionalEdge(vertInter, endVert, distInterToEnd);
		}
		else
		{
			// Either the start or ending vertex is too close to the
			// intersection, so drop the intersection and draw a diagonal line
			// between the two vertices instead of the manhattan line connecting them

			// Connect start to the end vertex
			endVert = roadNet.createVertex(endNode.getLocation());
			roadNet.createBidirectionalEdge(startVert, endVert, distInterToEnd);
		}

		// Recursively move down the tree
		kdNodeToRoadGroup(endVert, endNode, roadNet);
	}

	private WorldCoordinate computeRoadIntersectionFromNode(WorldCoordinate root, KDNode node, boolean isVertical)
	{
		WorldCoordinate intersection = null;

		if (isVertical)
		{
			intersection = new WorldCoordinate(node.getLocation().getNorth(), root.getEast());
		}
		else
		{
			intersection = new WorldCoordinate(root.getNorth(), node.getLocation().getEast());
		}

		return intersection;
	}

	/**
	 * Checks if the new cell location satisfies all the rules for new road seed
	 * generation.
	 *
	 * @param existingLocations
	 *            All pre-existing road seed coordinates.
	 * @param newLocation
	 *            The potential new seed location to validate.
	 * @return True if the new location is a valid location, false otherwise.
	 */
	private boolean isValidRoadSeedLocation(List<WorldCoordinate> existingLocations, WorldCoordinate newLocation)
	{
		boolean valid = true;

		// Prevents the seeds from clustering together
		double interSeedDistBuffer = Math.min(width.asMeters(), height.asMeters()) * MIN_INTERSECTION_SPACING_PERCENT;

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
		// numSeeds = Math.max(numSeeds, 3);

		Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
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
				seedCoord.setCoordinate(north, east);
			}

			logger.debug("Road seed {} at {}.", i, seedCoord);
			roadSeeds.add(seedCoord);
		}

		// Generate all the roads (edges) in the road network (tree).
		KDNode rootNode = KDTree.generateTree(roadSeeds);
		Vertex<WorldCoordinate> rootVert = roadNet.createVertex(rootNode.getLocation());
		kdNodeToRoadGroup(rootVert, rootNode, roadNet);
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
}
