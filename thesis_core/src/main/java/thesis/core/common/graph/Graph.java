package thesis.core.common.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
   
   public DirectedEdge<T> createDirectionalEdge(int startVertID, int endVertID)
   {
      Vertex<T> start = getVertexByID(startVertID);
      Vertex<T> end = getVertexByID(endVertID);
      return createDirectionalEdge(start, end);
   }

   public DirectedEdge<T> createDirectionalEdge(Vertex<T> start, Vertex<T> end)
   {
      DirectedEdge<T> edge = new DirectedEdge<T>(start, end);
      edges.add(edge);
      return edge;
   }
   
   public void createBidirectionalEdge(int vertID1, int vertID2)
   {
      Vertex<T> vert1 = getVertexByID(vertID1);
      Vertex<T> vert2 = getVertexByID(vertID2);
      createBidirectionalEdge(vert1, vert2);
   }
   
   public void createBidirectionalEdge(Vertex<T> vert1, Vertex<T> vert2)
   {
      edges.add(new DirectedEdge<T>(vert1, vert2));
      edges.add(new DirectedEdge<T>(vert2, vert1));
   }
   
   public Vertex<T> getVertexByData(T data)
   {
      Vertex<T> find = null;
      // Brute force search.
      for (Vertex<T> vert : vertices)
      {
         if (vert.getUserData() != null &&
               vert.getUserData().equals(data))
         {
            find = vert;
            break;
         }
      }
      return find;
   }

   public List<DirectedEdge<T>> getPath(final Vertex<T> start, final Vertex<T> end)
   {
      for(Vertex<T> vert : vertices)
      {
         vert.searchCost = Integer.MAX_VALUE;
         vert.searchParent = null;
      }
      
      //Simple breadth-first-search algorithm
      Queue<Vertex<T>> searchQ = new LinkedList<Vertex<T>>();
      start.searchCost = 0;
      boolean targetFound = false;
      while(!searchQ.isEmpty() && !targetFound)
      {
         //SearchNode<T> searchMe = searchQ.remove();
         Vertex<T> searchMe = searchQ.remove();
         
         for(DirectedEdge<T> edge : searchMe.getOutgoingEdges())
         {
            Vertex<T> edgeEnd = edge.getEndVertex();
            if (edgeEnd.searchCost == Integer.MAX_VALUE)
            {
               edgeEnd.searchCost = searchMe.searchCost + 1;
               edgeEnd.searchParent = searchMe;
               
               if(edgeEnd.getID() == end.getID())
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
      
      //Generate the path in reverse by vertex
      List<Vertex<T>> pathByVertex = new ArrayList<Vertex<T>>();
      pathByVertex.add(end);
      Vertex<T> iterator = end;
      while(iterator.searchParent != null)
      {
         pathByVertex.add(iterator.searchParent);
         iterator = iterator.searchParent;
      }
      
      //pathByVertex has the path from end to start, convert to edges
      List<DirectedEdge<T>> path = new ArrayList<DirectedEdge<T>>();
      //Start at the end of pathByVertex since it is the starting node
      for(int i=pathByVertex.size() - 1; i > 0; --i)
      {
         Vertex<T> edgeStart = pathByVertex.get(i);
         Vertex<T> edgeEnd = pathByVertex.get(i-1);
         
         //This is horribly inefficient, sort the edges somehow or give them lookup ids
         for(DirectedEdge<T> edge : edges)
         {
            if(edge.getStartVertex().getID() == edgeStart.getID() &&
                  edge.getEndVertex().getID() == edgeEnd.getID())
            {
               path.add(edge);
               break;
            }
         }
      }
      return path;
   }
}
