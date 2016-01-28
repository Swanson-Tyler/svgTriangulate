
public class Point {

	float x,y;
	boolean sentinel;
	
	public Point(float x, float y, boolean sentinel){
		
		this.x = x;
		this.y = y;
		this.sentinel = sentinel;
	}
	public Point(float x, float y){
		
		this.x = x;
		this.y = y;
		sentinel = false;
	}
}
