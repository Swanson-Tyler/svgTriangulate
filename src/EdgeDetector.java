import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;

// most parts taken from http://jsdo.it/akm2/xoYx
// (starting line 366++)


		/**
		 * @see http://jsdo.it/akm2/iMsL
		 */
class EdgeDetector{
	
	
		public static int[] detectEdges(BufferedImage buff, int[] image_data, int accuracy, int edge_size, int divisor )
		{
			ArrayList<Integer> matrix = getEdgeMatrix( edge_size );
			
			
			int multiplier =  (( accuracy | 50 ) / 10) | 1;			


			int divscalar = divisor != 0 ? 1 / divisor : 0;
			int k, len;

			if ( divscalar != 1 )
			{
				for ( k = 0, len = matrix.size(); k < matrix.size(); k++ )
				{
					int el = matrix.get(k) * divscalar;
					matrix.set(k,  el);
				}
			}
			
			int[] data = image_data;
			
			len = data.length >> 2;

			byte[] copy = new byte[len];
			

			for (int i = 0; i < len; i++)
			{
				

				int el = data[i << 2];
				byte b = (byte) i;
				copy[i] = b;
			}

			int width  = buff.getWidth()  | 0;
			int height = buff.getHeight()  | 0;

			int size  = (int)   Math.sqrt( matrix.size());
			
			int range = ((int)(size  * 0.5)) | 0;


			int x, y;
			int r, g, b, v;
			int col, row, sx, sy;
			int i, istep, jstep, kstep;

			for ( y = 0; y < height ; y += multiplier )
			{
				
				istep = y * width;

				for ( x = 0; x < width; x += multiplier  )
				{
					r = g = b = 0;

					for ( row = -range; row <= range; row++ )
					{
						

						sy = y + row;
						jstep = sy * width;
						kstep =  (int) ((row + range) * (size));

						if ( sy >= 0 && sy < height )
						{
							for ( col = -range; col <= range; col++ )
							{
								sx = x + col;
								
								v = matrix.get(( col + range ) + kstep);
							

								if (sx >= 0 && sx < width &&( v!= 0)){
									

										r += copy[sx + jstep] * v;
									
								}
							}
						}
					}

					if ( r < 0 )
					{
						r = 0;
					}

					else
					{
						if ( r > 255 )
						{
							r = 255;
						}
					}

					
				}
					
				

			}

			return image_data;
		}

		public static ArrayList<Integer> getEdgeMatrix( int size )
		{
			ArrayList<Integer> matrix = new ArrayList<Integer>();
			int side = size * 2 + 1;
			int i;
			int len = side * side;
			int center = len / 2 | 0;
           
			for ( i = 0; i < len ; i++ )
			{
				int el = i == center ? -len + 1 : 1;
				matrix.add(el);
				
			}

			return matrix;
		}
		
		
		public static byte[] extractBytes (BufferedImage img) throws IOException {
			 // open image
			 

			 // get DataBufferBytes from Raster
			 WritableRaster raster = img .getRaster();
			 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

			 return ( data.getData() );
			}

	}
