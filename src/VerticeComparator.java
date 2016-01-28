import java.util.Comparator;


public class VerticeComparator implements Comparator<Point> {


	@Override
	public int compare(Point o1, Point o2) {
		// TODO Auto-generated method stub
		if(o2.x > o1.x){
			return 1;
		}else{
			return -1;
		}
	}

	

}
