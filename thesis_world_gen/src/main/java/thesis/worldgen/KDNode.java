package thesis.worldgen;

import thesis.core.common.WorldCoordinate;

public class KDNode
{
   private WorldCoordinate location;
   private KDNode leftChild;
   private KDNode rightChild;
   private boolean isVerticalSplit;

   public KDNode(WorldCoordinate location, KDNode leftChild, KDNode rightChild, boolean isVerticalSplit)
   {
      this.location = location;
      this.leftChild = leftChild;
      this.rightChild = rightChild;
      this.isVerticalSplit = isVerticalSplit;
   }

   public WorldCoordinate getLocation()
   {
      return location;
   }

   public KDNode getLeftChild()
   {
      return leftChild;
   }

   public KDNode getRightChild()
   {
      return rightChild;
   }
   
   public boolean isVerticalSplit()
   {
      return isVerticalSplit;
   }

}
