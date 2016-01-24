package thesis.core.weapons;

/**
 * Performance specification data for all weapon types.
 */
public class WeaponTypeConfigs
{

   /**
    * Field of view in degrees of the weapon.
    */
   private float fov[];
   /**
    * Minimum launch distance in meters.
    */
   private double minRng[];
   /**
    * Maximum launch distance in meters.
    */
   private double maxRng[];

   public WeaponTypeConfigs()
   {

   }

   public void copy(WeaponTypeConfigs copy)
   {
      int numTypes = copy.fov.length;
      reset(copy.fov.length);

      System.arraycopy(copy.fov, 0, fov, 0, numTypes);
      System.arraycopy(copy.minRng, 0, minRng, 0, numTypes);
      System.arraycopy(copy.maxRng, 0, maxRng, 0, numTypes);
   }

   public void reset(int numWpnTypes)
   {
      fov = new float[numWpnTypes];
      minRng = new double[numWpnTypes];
      maxRng = new double[numWpnTypes];

      for(int i=0; i<numWpnTypes; ++i)
      {
         fov[0] = -1f;
         minRng[0] = -1f;
         maxRng[0] = -1f;
      }
   }

   public void setWeaponData(int wpnType, float fov, double minRng, double maxRng)
   {
      this.fov[wpnType] = fov;
      this.minRng[wpnType] = minRng;
      this.maxRng[wpnType] = maxRng;
   }

   public float getFOV(int wpnType)
   {
      return fov[wpnType];
   }

   public int getNumTypes()
   {
      return fov.length;
   }

   public double getMinRange(int wpnType)
   {
      return minRng[wpnType];
   }

   public double getMaxRange(int wpnType)
   {
      return maxRng[wpnType];
   }

   public boolean typeExists(int wpnType)
   {
      return wpnType >=0 && wpnType < fov.length;
   }
}

