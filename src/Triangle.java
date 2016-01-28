import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

class Triangle{
	
	Point a, b, c;
	float minx, miny, dx, dy;
	float x, y, r;
	Color color;

	
    public Triangle(Point a, Point b, Point c) {
      this.a = a;
      this.b = b;
      this.c = c;

      color = new Color(0, 0, 0);

      float A = b.x - a.x;
      float B = b.y - a.y;
      float C = c.x - a.x;
      float D = c.y - a.y;
      float E = A * (a.x + b.x) + B * (a.y + b.y);
      float F = C * (a.x + c.x) + D * (a.y + c.y);
      float G = 2 * (A * (c.y - b.y) - B * (c.x - b.x));
       
      
      /* If the points of the triangle are collinear, then just find the
       * extremes and use the midpoint as the center of the circumcircle. */
      if(Math.abs(G) < 0.000001) {
        minx = Math.min(a.x, Math.min( b.x, c.x));
        miny = Math.min(a.y, Math.min(b.y, c.y));
        dx   = (Math.max(a.x, Math.max(b.x, c.x)) - minx) * 0.5f;
        dy   = (Math.max(a.y,Math.max (b.y, c.y)) - miny) * 0.5f;

        this.x = minx + dx;
        this.y = miny + dy;
        this.r = dx * dx + dy * dy;
      }

      else {
        this.x = (D*E - B*F) / G;
        this.y = (A*F - C*E) / G;
        dx = this.x - a.x;
        dy = this.y - a.y;
        this.r = dx * dx + dy * dy;
      }
    }
    
    public void setColor(Color c){
    	
    	this.color = c;
    }

    public void draw(Graphics2D g2d) {
    	g2d.setColor(color);
		Polygon polygon = new Polygon();
		polygon.addPoint((int) (this.a.x), (int) (this.a.y));
		polygon.addPoint((int) (this.b.x), (int) (this.b.y));
		polygon.addPoint((int) (this.c.x), (int) (this.c.y));
		g2d.fillPolygon(polygon);
      
    }
}