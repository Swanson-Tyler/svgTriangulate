import java.util.ArrayList;

// most parts taken from akm2's script:
// http://jsdo.it/akm2/xoYx (line 230++)
/*global define*/
class RandomVertices{
	
	
	
	public static ArrayList<Point> getRandomVertices(ArrayList<Point> points, float rate, int max_num, int accuracy, int width, int height ){
		ArrayList<Point> result = new ArrayList<Point>();
		int j, i, i_len, t_len, limit;
			 
			 i = 0;
			 i_len = points.size();
			 t_len = i_len;
			 limit = Math.round( i_len * rate );


			if ( limit > max_num )
			{
				limit = max_num;
			}

			while ( i < limit && i < i_len )
			{
				j = (int) (t_len * Math.random()  ) | 0;
				result.add( points.get(j));
				

				// this seems to be extremely time
				// intensive.
				// points.splice( j, 1 );

				t_len--;
				i++;
			}

			int x, y;

			// gf: add more points along the edges so we always use the full canvas,
			for ( x = 0; x < width; x += (100 - accuracy) )
			{
				result.add( new Point( ~~x,   0  ));
				result.add( new Point( ~~x,  height  ));
			}

			for ( y = 0; y < height; y += (100 - accuracy) )
			{
				result.add(new Point( 0,~~y ));
				result.add(new Point( width,~~y));
			}

			result.add( new Point( 0, height ) );
			result.add( new Point( width, height ));

			return result;
		}

}
