package thesis.core.common.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Generic graph data structure that can store arbitrary user data at its
 * vertices.
 *
 * @param <T>
 */
public class Graph<T>
{
   private Set<DirectedEdge<T>> edges;
   private Set<Vertex<T>> vertices;
   private int idCounter = 0;

   public Graph()
   {
      edges = new HashSet<DirectedEdge<T>>();
      vertices = new HashSet<Vertex<T>>();
   }

   public Vertex<T> createVertex()
   {
      Vertex<T> vert = new Vertex<T>(idCounter);
      vertices.add(vert);
      ++idCounter;
      return vert;
   }

   public Vertex<T> createVertex(T data)
   {
      Vertex<T> vert = new Vertex<T>(idCounter, data);
      vertices.add(vert);
      ++idCounter;
      return vert;
   }

   /**
    *
    * @return An unmodifiable view of the vertices in the graph.
    */
   public Set<Vertex<T>> getVertices()
   {
      return Collections.unmodifiableSet(vertices);
   }

   public int getNumVertices()
   {
      return vertices.size();
   }

   public int getNumEdges()
   {
      return edges.size();
   }

   /**
    *
    * @return An unmodifiable view of the edges in the graph.
    */
   public Set<DirectedEdge<T>> getEdges()
   {
      return Collections.unmodifiableSet(edges);
   }

   public Vertex<T> getVertexByID(int id)
   {
      Vertex<T> find = null;
      // Brute force search. Could be better by keeping vertices sorted
      for (Vertex<T> vert : vertices)
      {
         if (vert.getID() == id)
         {
            find = vert;
            break;
         }
      }
      return find;
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

   public Vertex<T> getVertexByData(T data)
   {
      Vertex<T> find = null;
      // Brute force search.
      for (Vertex<T> vert : vertices)
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
      for (Vertex<T> vert : vertices)
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
               path.add(edge);
               break;
            }
         }
      }
      return path;
   }
}
