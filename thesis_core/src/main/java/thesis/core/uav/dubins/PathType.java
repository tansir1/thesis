package thesis.core.uav.dubins;

public enum PathType
{
   NO_PATH(null, null, null), 
   LSL(SegmentType.Left, SegmentType.Straight, SegmentType.Left), 
   LSR(SegmentType.Left, SegmentType.Straight, SegmentType.Right), 
   RSL(SegmentType.Right, SegmentType.Straight, SegmentType.Left), 
   RSR(SegmentType.Right, SegmentType.Straight, SegmentType.Right), 
   RLR(SegmentType.Right, SegmentType.Left, SegmentType.Right), 
   LRL(SegmentType.Left, SegmentType.Right, SegmentType.Left);
   
   private SegmentType[] types;

   public SegmentType getSegmentType(PathPhase phase)
   {
      SegmentType type = null;
      
      switch(phase)
      {
      case Phase1:
         type = types[0];
         break;
      case Phase2:
         type = types[1];
         break;
      case Phase3:
         type = types[2];
         break;
      }
      
      return type;
   }
   
   private PathType(SegmentType type1, SegmentType type2, SegmentType type3)
   {
      types = new SegmentType[3];
      types[0] = type1;
      types[1] = type2;
      types[2] = type3;
   }
   
}
