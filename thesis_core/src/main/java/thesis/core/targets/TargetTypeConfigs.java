package thesis.core.targets;

public class TargetTypeConfigs
{
   /**
    * If the target moves, it moves at this speed in m/s.
    */
   private float spds[];
   private float bestAngles[];

   public TargetTypeConfigs()
   {

   }

   public void copy(TargetTypeConfigs copy)
   {
      int numTgts = copy.spds.length;
      reset(copy.spds.length);

      System.arraycopy(copy.spds, 0, spds, 0, numTgts);
      System.arraycopy(copy.bestAngles, 0, bestAngles, 0, numTgts);
   }

   public void reset(int numTgtTypes)
   {
      spds = new float[numTgtTypes];
      bestAngles = new float[numTgtTypes];

      for(int i=0; i<spds.length; ++i)
      {
         spds[i] = -1f;//Used to indicate a non-mobile target
         bestAngles[i] = 0f;//Default to "head on" for best angle
      }
   }

   /**
    * @param tgtType
    * @param spd Set to a negative value to indicate that the target is stationary.  Units of m/s.
    * @param bestAngle The best angle to attack or scan the target.  Degrees from north.
    */
   public void setTargetData(int tgtType, float spd, float bestAngle)
   {
      spds[tgtType] = spd;
      bestAngles[tgtType] = bestAngle;
   }

   public float getBestAngle(int tgtType)
   {
      return bestAngles[tgtType];
   }

   public float getSpeed(int tgtType)
   {
      return spds[tgtType];
   }

   public int getNumTypes()
   {
      return spds.length;
   }

   public boolean isMobile(int tgtType)
   {
      return spds[tgtType] > 0;
   }

   public boolean typeExists(int tgtType)
   {
      return tgtType >=0 && tgtType < spds.length;
   }
}
