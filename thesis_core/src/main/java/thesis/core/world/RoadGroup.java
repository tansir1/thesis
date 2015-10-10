package thesis.core.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *An origin location and the destinations that it connects to.
 */
public class RoadGroup
{
   public CellCoordinate origin;
   public Set<CellCoordinate> destinations;
   
   public RoadGroup(CellCoordinate origin)
   {
      this.origin = origin;
      destinations = new HashSet<CellCoordinate>();
   }
   
   public void addDestination(CellCoordinate dest)
   {
      destinations.add(dest);
   }
   
   public CellCoordinate getOrigin()
   {
      return origin;
   }
   
   public Set<CellCoordinate> getDestinations()
   {
      return Collections.unmodifiableSet(destinations);
   }
}
