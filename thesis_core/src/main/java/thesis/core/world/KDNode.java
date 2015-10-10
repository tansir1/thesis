package thesis.core.world;

public class KDNode
{
   private CellCoordinate location;
   private KDNode leftChild;
   private KDNode rightChild;
   private boolean isVerticalSplit;

   public KDNode(CellCoordinate location, KDNode leftChild, KDNode rightChild, boolean isVerticalSplit)
   {
      this.location = location;
      this.leftChild = leftChild;
      this.rightChild = rightChild;
      this.isVerticalSplit = isVerticalSplit;
   }

   public CellCoordinate getLocation()
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
