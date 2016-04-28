package thesis.core.targets;

public interface ITrueTargetStatusProvider
{
   /**
    * @param ID of the target to look up.
    * @return True if the target is alive.
    */
   public boolean isAlive(int trueTgtID);
}
