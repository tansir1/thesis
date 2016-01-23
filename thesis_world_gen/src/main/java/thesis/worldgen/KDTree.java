package thesis.worldgen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import thesis.core.common.CellCoordinate;

/**
 * A naive KDTree implementation to partition the world space.
 */
public class KDTree
{
	/**
	 * Utilize the KD Tree algorithm to partition the world space using the
	 * given coordinates as vertices to connect.
	 *
	 * @param nodes
	 *            The world is partitioned based on these locations.
	 * @return The root node of the generated tree.
	 */
	public static KDNode generateTree(List<CellCoordinate> nodes)
	{
		return generateTree(nodes, 0);
	}

	private static KDNode generateTree(List<CellCoordinate> nodes, int depth)
	{
		if (nodes.isEmpty())
		{
			// Occurs when the final leaf element in the tree is processed
			return null;
		}

		int axis = depth % 2;// Assumes we only use a cartesian coordinate plane

		Collections.sort(nodes, new CoordComparator(axis == 0));

		int median = nodes.size() / 2;

		CellCoordinate location = nodes.get(median);

		KDNode leftChild = null;
		KDNode rightChild = null;

		if (median != 0) // Occurs when the nodes list contains a single element
		{
			leftChild = generateTree(nodes.subList(0, median), depth + 1);
			rightChild = generateTree(nodes.subList(median + 1, nodes.size()), depth + 1);
		}

		return new KDNode(location, leftChild, rightChild, axis == 0);
	}

	private static class CoordComparator implements Comparator<CellCoordinate>
	{
		private boolean sortVertically;

		public CoordComparator(boolean sortVertically)
		{
			this.sortVertically = sortVertically;
		}

		@Override
		public int compare(CellCoordinate o1, CellCoordinate o2)
		{
			int retVal = 0;

			if (sortVertically)
			{
				if (o1.getRow() < o2.getRow())
				{
					retVal = -1;
				}
				else if (o1.getRow() > o2.getRow())
				{
					retVal = 1;
				}
				else
				{
					retVal = 0;
				}
			}
			else
			{
				if (o1.getColumn() < o2.getColumn())
				{
					retVal = -1;
				}
				else if (o1.getColumn() > o2.getColumn())
				{
					retVal = 1;
				}
				else
				{
					retVal = 0;
				}
			}
			return retVal;
		}

	}
}
