package thesis.core.common.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Generic graph data structure that can store arbitrary user data at its
 * vertices.
 *
 * @param <T>
 */
public class Graph<T>
{
   private List<DirectedEdge<T>> edges;
   private HashMap<Integer, Vertex<T>> vertices;
   private int idCounter;

   public Graph()
   {
      idCounter = 0;
      edges = new ArrayList<DirectedEdge<T>>();
      vertices = new HashMap<Integer, Vertex<T>>();
   }

   /**
    * Create a new vertex with no user data.
    *
    * @return The new vertex.
    */
   public Vertex<T> createVertex()
   {
      while (vertices.containsKey(idCounter))
      {
         ++idCounter;
      }
      return createVertex(idCounter, null);
   }

   /**
    * Create a new vertex with the specified user data.
    *
    * @param data
    *           This user data will be attached to the new vertex.
    * @return The new vertex.
    */
   public Vertex<T> createVertex(T data)
   {
      Vertex<T> vert = createVertex();
      vert.setUserData(data);
      return vert;
   }

   /**
    * Create a new vertex with the requested ID and no user data.
    *
    * @param id
    *           The ID for the new vertex.
    * @return The new vertex.
    * @throws IllegalArgumentException
    *            Occurs when the requested ID is already being used by another
    *            vertex.
    */
   public Vertex<T> createVertex(int id)
   {
      return createVertex(id, null);
   }

   /**
    * Create a new vertex with the requested ID and attached user data.
    *
    * @param id
    *           The ID for the new vertex.
    * @param data
    *           The user data to attach to the vertex.
    * @return The new vertex.
    * @throws IllegalArgumentException
    *            Occurs when the requested ID is already being used by another
    *            vertex.
    */
   public Vertex<T> createVertex(int id, T data)
   {
      if (vertices.containsKey(id))
      {
         throw new IllegalArgumentException("Vertex ID is already in use.");
      }

      Vertex<T> vert = new Vertex<T>(id, data);
      vertices.put(id, vert);
      return vert;
   }

   /**
    *
    * @return An unmodifiable view of the vertices in the graph.
    */
   public Collection<Vertex<T>> getVertices()
   {
      return Collections.unmodifiableCollection(vertices.values());
   }

   public int getNumVertices()
   {
      return vertices.size();
   }

   public int getNumEdges()
   {
      return edges.size();
   }

   public List<DirectedEdge<T>> getEdges()
   {
      return edges;
   }

   /**
    *
    * @param id
    *           Get the vertex with this ID value.
    * @return The requested vertex or null if no such vertex exists.
    */
   public Vertex<T> getVertexByID(int id)
   {
      return vertices.get(id);
   }

   public DirectedEdge<T> createDirectionalEdge(int startVertID, int endVertID, double cost)
   {
      Vertex<T> start = getVertexByID(startVertID);
      Vertex<T> end = getVertexByID(endVertID);
      return createDirectionalEdge(start, end, cost);
   }

   public DirectedEdge<T> createDirectionalEdge(Vertex<T> start, Vertex<T> end, double cost)
   {
      DirectedEdge<T> edge = new DirectedEdge<T>(start, end, cost);
      edges.add(edge);
      start.addOutgoingEdge(edge);
      end.addIncomingEdge(edge);
      return edge;
   }

   public void createBidirectionalEdge(int vertID1, int vertID2, double cost)
   {
      Vertex<T> vert1 = getVertexByID(vertID1);
      Vertex<T> vert2 = getVertexByID(vertID2);
      createBidirectionalEdge(vert1, vert2, cost);
   }

   public void createBidirectionalEdge(Vertex<T> vert1, Vertex<T> vert2, double cost)
   {
      createDirectionalEdge(vert1, vert2, cost);
      createDirectionalEdge(vert2, vert1, cost);
   }

   /**
    *
    * @param data
    *           Search the vertices for a vertex with this user data (compared
    *           using T.equals(T)).
    * @return The vertex containing the requested user data or null if no such
    *         vertex is found.
    */
   public Vertex<T> getVertexByData(T data)
   {
      Vertex<T> find = null;
      // Brute force search.
      for (Vertex<T> vert : vertices.values())
      {
         if (vert.getUserData() != null && vert.getUserData().equals(data))
         {
            find = vert;
            break;
         }
      }
      return find;
   }

   /**
    * Performs a breadth first search across the graph to find the requested
    * path.
    *
    * @param start
    *           The vertex to start at.
    * @param end
    *           The vertex to end at.
    * @return A list of edges connecting the start to the end vertex or an empty
    *         list if no such path exists.
    */
   public List<DirectedEdge<T>> findPath(final Vertex<T> start, final Vertex<T> end)
   {
      for (Vertex<T> vert : vertices.values())
      {
         vert.searchCost = Integer.MAX_VALUE;
         vert.searchParent = null;
      }

      // Simple breadth-first-search algorithm
      Queue<Vertex<T>> searchQ = new LinkedList<Vertex<T>>();
      searchQ.add(start);
      start.searchCost = 0;
      boolean targetFound = false;
      while (!searchQ.isEmpty() && !targetFound)
      {
         // SearchNode<T> searchMe = searchQ.remove();
         Vertex<T> searchMe = searchQ.remove();

         for (DirectedEdge<T> edge : searchMe.getOutgoingEdges())
         {
            Vertex<T> edgeEnd = edge.getEndVertex();
            if (edgeEnd.searchCost == Integer.MAX_VALUE)
            {
               edgeEnd.searchCost = searchMe.searchCost + 1;
               edgeEnd.searchParent = searchMe;

               if (edgeEnd.getID() == end.getID())
               {
                  targetFound = true;
                  break;
               }
               else
               {
                  searchQ.add(edgeEnd);
               }
            }
         }
      }

      // Generate the path in reverse by vertex
      List<Vertex<T>> pathByVertex = new ArrayList<Vertex<T>>();
      pathByVertex.add(end);
      Vertex<T> iterator = end;
      while (iterator.searchParent != null)
      {
         pathByVertex.add(iterator.searchParent);
         iterator = iterator.searchParent;
      }

      // pathByVertex has the path from end to start, convert to edges
      List<DirectedEdge<T>> path = new ArrayList<DirectedEdge<T>>();
      // Start at the end of pathByVertex since it is the starting node
      for (int i = pathByVertex.size() - 1; i > 0; --i)
      {
         Vertex<T> edgeStart = pathByVertex.get(i);
         Vertex<T> edgeEnd = pathByVertex.get(i - 1);

         // This is horribly inefficient, sort the edges somehow or give them
         // lookup ids
         for (DirectedEdge<T> edge : edges)
         {
            if (edge.getStartVertex().getID() == edgeStart.getID() && edge.getEndVertex().getID() == edgeEnd.getID())
            {
               path.add(new DirectedEdge<T>(edge));
               break;
            }
         }
      }
      return path;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((edges == null) ? 0 : edges.hashCode());
      result = prime * result + ((vertices == null) ? 0 : vertices.hashCode());
      return result;
   }

   /* (non-Javadoc)
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
      Graph other = (Graph) obj;
      if (edges == null)
      {
         if (other.edges != null)
            return false;
      }
      else if (edges.size() != other.edges.size())
      {
         return false;
      }

      if(!edges.containsAll(other.edges))
         return false;

      if (vertices == null)
      {
         if (other.vertices != null)
            return false;
      }
      else if (!vertices.equals(other.vertices))
         return false;
      return true;
   }


}
