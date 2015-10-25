package thesis.core.common.graph;

/**
 * A unidirectional edge in a graph.
 *
 * @param <T>
 */
public class DirectedEdge<T>
{
   private Vertex<T> start;
   private Vertex<T> end;

   private double cost;

   /**
    * Create a zero cost edge from the start vertex to the end vertex.
    *
    * @param start
    *           The edge will start here.
    * @param end
    *           The edge will end here.
    */
   public DirectedEdge(Vertex<T> start, Vertex<T> end)
   {
      if (start == null)
      {
         throw new NullPointerException("start vertex cannot be null.");
      }

      if (end == null)
      {
         throw new NullPointerException("end vertex cannot be null.");
      }

      this.start = start;
      this.end = end;
      cost = 0;
   }

   /**
    * Create an edge from the start vertex to the end vertex with the specified
    * cost.
    *
    * @param start
    *           The edge will start here.
    * @param end
    *           The edge will end here.
    * @param cost
    *           The cost of traversing this edge.
    */
   public DirectedEdge(Vertex<T> start, Vertex<T> end, double cost)
   {
      if (start == null)
      {
         throw new NullPointerException("start vertex cannot be null.");
      }

      if (end == null)
      {
         throw new NullPointerException("end vertex cannot be null.");
      }

      this.start = start;
      this.end = end;
      this.cost = cost;
   }

   public Vertex<T> getStartVertex()
   {
      return start;
   }

   public Vertex<T> getEndVertex()
   {
      return end;
   }

   public double getCost()
   {
      return cost;
   }

   public void setCost(double cost)
   {
      this.cost = cost;
   }

   /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(cost);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((end == null) ? 0 : end.hashCode());
      result = prime * result + ((start == null) ? 0 : start.hashCode());
      return result;
   }

   /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @SuppressWarnings("rawtypes")
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DirectedEdge other = (DirectedEdge) obj;
      //if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
         //return false;
      if(Math.abs(cost - other.cost) > 0.000000001)
         return false;

      if (end == null)
      {
         if (other.end != null)
            return false;
      }
      else if (!end.equals(other.end))
         return false;
      if (start == null)
      {
         if (other.start != null)
            return false;
      }
      else if (!start.equals(other.start))
         return false;
      return true;
   }

}
