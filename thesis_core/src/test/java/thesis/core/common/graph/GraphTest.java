package thesis.core.common.graph;



import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class GraphTest
{

   @Test
   public void searchTest()
   {
      Graph<Object> testMe = new Graph<Object>();
      
      /*
       * V1---V2---V3
       * |    |    |
       * V4--V5---V6
       * |
       * V7
       */
      
      Vertex<Object> v1 = testMe.createVertex();
      Vertex<Object> v2 = testMe.createVertex();
      Vertex<Object> v3 = testMe.createVertex();
      Vertex<Object> v4 = testMe.createVertex();
      Vertex<Object> v5 = testMe.createVertex();
      Vertex<Object> v6 = testMe.createVertex();
      Vertex<Object> v7 = testMe.createVertex();
      
      testMe.createBidirectionalEdge(v1, v2, 0);
      testMe.createBidirectionalEdge(v1, v4, 0);
      
      testMe.createBidirectionalEdge(v2, v5, 0);
      testMe.createBidirectionalEdge(v2, v3, 0);
      
      testMe.createBidirectionalEdge(v3, v6, 0);
      
      testMe.createBidirectionalEdge(v4, v5, 0);
      testMe.createBidirectionalEdge(v4, v7, 0);
      
      testMe.createBidirectionalEdge(v5, v6, 0);
      
      List<DirectedEdge<Object>> path = testMe.findPath(v1, v7);
      
      assertEquals("Incorrect number of edges in path.", 2, path.size());
      assertEquals("Incorrect v1->v4 start.", v1.getID(), path.get(0).getStartVertex().getID());
      assertEquals("Incorrect v1->v4 end.", v4.getID(), path.get(0).getEndVertex().getID());
      assertEquals("Incorrect v4->v7 start.", v4.getID(), path.get(1).getStartVertex().getID());
      assertEquals("Incorrect v4->v7 end.", v7.getID(), path.get(1).getEndVertex().getID());
   }
}
