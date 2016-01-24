package thesis.core.sensors;

/**
 * Performance specification data for all sensor types.
 */
public class SensorTypeConfigs
{

   /**
    * Field of view in degrees of the sensor.
    */
   private float fov[];
   /**
    * Minimum sensing distance in meters.
    */
   private double minRng[];
   /**
    * Maximum sensing distance in meters.
    */
   private double maxRng[];
   /**
    * Speed that the sensor slews in degrees/second.
    */
   private float slewRate[];

   public SensorTypeConfigs()
   {

   }

   public void copy(SensorTypeConfigs copy)
   {
      int numTgts = copy.fov.length;
      reset(copy.fov.length);

      System.arraycopy(copy.fov, 0, fov, 0, numTgts);
      System.arraycopy(copy.minRng, 0, minRng, 0, numTgts);
      System.arraycopy(copy.maxRng, 0, maxRng, 0, numTgts);
      System.arraycopy(copy.slewRate, 0, slewRate, 0, numTgts);
   }

   public void reset(int numTgtTypes)
   {
      fov = new float[numTgtTypes];
      minRng = new double[numTgtTypes];
      maxRng = new double[numTgtTypes];
      slewRate = new float[numTgtTypes];

      for(int i=0; i<numTgtTypes; ++i)
      {
         fov[0] = -1f;
         minRng[0] = -1f;
         maxRng[0] = -1f;
         slewRate[0] = 0f;
      }
   }

   public void setSensorData(int snsrType, float fov, double minRng, double maxRng, float slewRt)
   {
      this.fov[snsrType] = fov;
      this.minRng[snsrType] = minRng;
      this.maxRng[snsrType] = maxRng;
      this.slewRate[snsrType] = slewRt;
   }

   public float getFOV(int snsrType)
   {
      return fov[snsrType];
   }

   public float getSlewRate(int snsrType)
   {
      return slewRate[snsrType];
   }

   public int getNumTypes()
   {
      return fov.length;
   }

   public double getMinRange(int snsrType)
   {
      return minRng[snsrType];
   }

   public double getMaxRange(int snsrType)
   {
      return maxRng[snsrType];
   }

   public boolean typeExists(int snsrType)
   {
      return snsrType >=0 && snsrType < fov.length;
   }
}

