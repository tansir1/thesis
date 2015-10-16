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
}
