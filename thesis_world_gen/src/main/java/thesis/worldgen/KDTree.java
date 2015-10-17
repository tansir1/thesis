package thesis.worldgen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import thesis.core.common.WorldCoordinate;

/**
 *A naive KDTree implementation to partition the world space.
 */
public class KDTree
{
   /**
    * Utilize the KD Tree algorithm to partition the world space using the given
    * coordinates as vertices to connect.
    * 
    * @param nodes
    *           The world is partitioned based on these locations.
    * @return The root node of the generated tree.
    */
   public static KDNode generateTree(List<WorldCoordinate> nodes)
   {
      return generateTree(nodes, 0);
   }

   private static KDNode generateTree(List<WorldCoordinate> nodes, int depth)
   {
      if (nodes.isEmpty()) // Occurs when the final leaf element in the tree is
                           // processed
      {
         return null;
      }

      int axis = depth % 2;// Assumes we only use a cartesian coordinate plane

      Collections.sort(nodes, new CoordComparator(axis == 0));

      int median = nodes.size() / 2;

      WorldCoordinate location = nodes.get(median);

      KDNode leftChild = null;
      KDNode rightChild = null;

      if (median != 0) // Occurs when the nodes list contains a single element
      {
         leftChild = generateTree(nodes.subList(0, median), depth + 1);
         rightChild = generateTree(nodes.subList(median + 1, nodes.size()), depth + 1);
      }

      return new KDNode(location, leftChild, rightChild, axis == 0);
   }

   private static class CoordComparator implements Comparator<WorldCoordinate>
   {
      private boolean sortVertically;

      public CoordComparator(boolean sortVertically)
      {
         this.sortVertically = sortVertically;
      }

      @Override
      public int compare(WorldCoordinate o1, WorldCoordinate o2)
      {
         int retVal = 0;

         if (sortVertically)
         {
            if (o1.getNorth().asMeters() < o2.getNorth().asMeters())
            {
               retVal = -1;
            }
            else if (o1.getNorth().equals(o2.getNorth()))
            {
               retVal = 0;
            }
            else
            {
               retVal = 1;
            }
         }
         else
         {
            if (o1.getEast().asMeters() < o2.getEast().asMeters())
            {
               retVal = -1;
            }
            else if (o1.getEast().equals(o2.getEast()))
            {
               retVal = 0;
            }
            else
            {
               retVal = 1;
            }
         }
         return retVal;
      }

   }
}
