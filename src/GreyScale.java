import java.awt.image.BufferedImage;

class GreyScale{
		
		public static int[] greyScale( int[] image_data)
		{
			
			int[] data = image_data;
			int len = data.length;

			
			for ( int i = 0; i < len - 2; i += 4 )
			{
				int brightness = (int) (0.34 * data[i] + 0.5 * data[i + 1] + 0.16 * data[i + 2]);

				data[i] = brightness;
				data[i + 1] = brightness;
				data[i + 2] = brightness;
			}

		  
		  
		return data;
		
		}



	

	

}