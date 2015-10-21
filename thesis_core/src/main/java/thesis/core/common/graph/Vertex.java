package thesis.core.common.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A vertex in a graph that can hold arbitrary user data.
 *
 * @param <T>
 *           Arbitrary user data.
 */
public class Vertex<T>
{
   /**
    * A unique identifier per instance of {@link Vertex}.
    */
   private int id;

   private T data;

   private List<DirectedEdge<T>> inEdges, outEdges;

   protected Vertex<T> searchParent;
   protected int searchCost;

   public Vertex(int id)
   {
      this.id = id;
      data = null;
      inEdges = new ArrayList<DirectedEdge<T>>();
      outEdges = new ArrayList<DirectedEdge<T>>();
   }

   public Vertex(int id, T data)
   {
      this.id = id;
      this.data = data;
      inEdges = new ArrayList<DirectedEdge<T>>();
      outEdges = new ArrayList<DirectedEdge<T>>();
   }

   public T getUserData()
   {
      return data;
   }

   public void setUserData(T data)
   {
      this.data = data;
   }

   public int getID()
   {
      return id;
   }

   /**
    * Get an unmodifiable view of the incoming edges.
    *
    * @return
    */
   public List<DirectedEdge<T>> getIncomingEdges()
   {
      return Collections.unmodifiableList(inEdges);
   }

   /**
    * Get an unmodifiable view of the outgoing edges.
    *
    * @return
    */
   public List<DirectedEdge<T>> getOutgoingEdges()
   {
      return Collections.unmodifiableList(outEdges);
   }

   public void addOutgoingEdge(DirectedEdge<T> edge)
   {
      if (edge == null)
      {
         throw new NullPointerException("edge cannot be null.");
      }

      outEdges.add(edge);
   }

   public void addIncomingEdge(DirectedEdge<T> edge)
   {
      if (edge == null)
      {
         throw new NullPointerException("edge cannot be null.");
      }

      inEdges.add(edge);
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
      result = prime * result + id;
      return result;
   }

   /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Vertex<T> other = (Vertex<T>) obj;
      if (id != other.id)
         return false;
      return true;
   }

}
