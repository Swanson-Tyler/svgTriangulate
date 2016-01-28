import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;


  // https://github.com/ironwallaby/delaunay/blob/master/delaunay.js
class Triangulate{

    public float byX(Point a, Point b) {
      return b.x - a.x;
    }

    public static void dedup(ArrayList<Point> edges) {
      int j = edges.size();
      int i;
      Point a, b, m, n;

      while(j > 0) {
        b = edges.get(--j);
        a = edges.get(--j);
        i = j;
        while(i > 0) {
          n = edges.get(--i);
          m = edges.get(--i);

          if((a.equals(m) && b.equals(n)) || (a.equals(n) && b.equals(m))) {
        	 
        	  edges.remove(j + 1);
        	  edges.remove(j);
        	
        	
        	 edges.remove(i + 1);
            edges.remove(i);
           

         
            j -= 2;
            break;
          }
        }
      }
    }

    public static ArrayList<Triangle>  triangulate(ArrayList<Point> vertices) {
      /* Bail if there aren't enough vertices to form any triangles. */
      if(vertices.size() < 3)
        return null;

      /* Ensure the vertex array is in order of descending X coordinate
       * (which is needed to ensure a subquadratic runtime), and then find
       * the bounding box around the points. */
      Collections.sort(vertices, new VerticeComparator());
     
      

     int i = vertices.size() - 1;
     float xmin = vertices.get(i).x;
     float xmax = vertices.get(0).x;
     float ymin = vertices.get(i).y;
     float ymax = ymin;

      while(i > 0) {
        if(vertices.get(i).y < ymin) ymin = vertices.get(i).y;
        if(vertices.get(i).y > ymax) ymax = vertices.get(i).y;
        --i;
      }

      /* Find a supertriangle, which is a triangle that surrounds all the
       * vertices. This is used like something of a sentinel value to remove
       * cases in the main algorithm, and is removed before we return any
       * results.
       *
       * Once found, put it in the "open" list. (The "open" list is for
       * triangles who may still need to be considered; the "closed" list is
       * for triangles which do not.) */
        float dx     = xmax - xmin;
        float dy     = ymax - ymin;
        float dmax   = (dx > dy) ? dx : dy;
        float xmid   = (xmax + xmin) * 0.5f;
        float ymid   = (ymax + ymin) * 0.5f;
        ArrayList<Triangle> open   = new ArrayList<Triangle>();
        open.add(new Triangle( 
        		new Point(xmid - 20 * dmax, ymid - dmax, true),
        		new Point(xmid, ymid + 20 * dmax , true), 
        		new Point(xmid + 20 * dmax, ymid - dmax, true))
        		
        		
        		
        		
        );
        ArrayList<Triangle> closed   = new ArrayList<Triangle>();
        ArrayList<Point> edges = new ArrayList<Point>();
        int j;
        Point a;
        Point b;

      /* Incrementally add each vertex to the mesh. */
      i = vertices.size();
      while(i-- > 0) {
        /* For each open triangle, check to see if the current point is
         * inside it's circumcircle. If it is, remove the triangle and add
         * it's edges to an edge list. */
        edges.clear();
      
        j = open.size();
        while(j-- > 0) {
          /* If this point is to the right of this triangle's circumcircle,
           * then this triangle should never get checked again. Remove it
           * from the open list, add it to the closed list, and skip. */
          dx = vertices.get(i).x - open.get(j).x;
          if(dx > 0 && dx * dx > open.get(j).r){
            closed.add(open.get(j));
            open.remove(j);
            continue;
          }

          /* If not, skip this triangle. */
       
         
          dy = vertices.get(i).y - open.get(j).y;
          //System.out.println("dx: "+ dx + " ,dy: " + dy + "\n");
          if(dx * dx + dy * dy > open.get(j).r)
            continue;

          /* Remove the triangle and add it's edges to the edge list. */
          /*
      
         */
          
          edges.add(open.get(j).a);
          edges.add(open.get(j).b);
          edges.add(open.get(j).b);
          edges.add(open.get(j).c);
          edges.add(open.get(j).c);
          edges.add(open.get(j).a);

            
          open.remove(j);
        }

        /* Remove any doubled edges. */
        dedup(edges);

        /* Add a new triangle for each edge. */
        //System.out.println("j: " + edges.size() + "\n");
        j = edges.size();
        while(j > 0) {
          b = edges.get(--j);
          a = edges.get(--j);
          open.add(new Triangle(a, b, vertices.get(i)));
        }
      }

      /* Copy any remaining open triangles to the closed list, and then
       * remove any triangles that share a vertex with the supertriangle. */
     
     
    	 closed.addAll(open);
      

      i = closed.size();
    //  int w = 0;
      while(i-- > 0)
        if(closed.get(i).a.sentinel ||
           closed.get(i).b.sentinel ||
           closed.get(i).c.sentinel){
        		
        		
        		closed.remove(i);
        }
      		
      /* Yay, we're done! */
     // System.out.println(closed.size());
      return closed;
    }
}

  