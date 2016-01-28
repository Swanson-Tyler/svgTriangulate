
import java.util.ArrayList;


// most parts taken from http://jsdo.it/akm2/xoYx
// (starting line 293++)
/*global define*/
class EdgePoints{
	
	
		public static ArrayList<Point> getEdgePoints( int width, int height, int[] image_data, int sensitivity, int accuracy )
		{
			int multiplier =   (accuracy / 10) | 1;
			int edge_detect_value = sensitivity;
			
			
			int[] data = image_data;
			ArrayList<Point> points = new ArrayList<Point>();
			int x, y, row, col, sx, sy, step, sum, total;

			for ( y = 0; y < height; y += multiplier )
			{
				for ( x = 0; x < width; x += multiplier )
				{
					sum = total = 0;

					for ( row = -1; row <= 1; row++ )
					{
						sy = y + row;
						step = sy * width;

						if ( sy >= 0 && sy < height )
						{
							for ( col = -1; col <= 1; col++ )
							{
								sx = x + col;

								if ( sx >= 0 && sx < width )
								{
									
										sum += data[( sx + step ) << 2];
										total++;
								
								}
							}
						}
					}

					if ( total > 0)
					{
						sum /= total;
					}

					if ( sum > edge_detect_value )
					{
						points.add(new Point( x, y ));
					}
				}
			}

			return points;
		}
		
		
	
		
}