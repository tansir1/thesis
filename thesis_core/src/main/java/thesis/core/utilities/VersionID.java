package thesis.core.utilities;

public class VersionID
{
   private int major, minor, patch;

   public VersionID(int major, int minor, int patch)
   {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
   }
   
   public int getMajor()
   {
      return major;
   }

   public int getMinor()
   {
      return minor;
   }

   public int getPatch()
   {
      return patch;
   }

   @Override
   public String toString()
   {
      return major + "." + minor + "." + patch;
   }

}
