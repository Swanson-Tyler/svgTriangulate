

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ED {
	
	String filename;
	int width, height;
	static BufferedImage original;
	static BufferedImage product;
	
	static int[][] filter1 = { { -1,  0,  1 },
             { -2,  0,  2 },
             { -1,  0,  1 }
           };
	static int[][] filter2 = { {  1,  2,  1 },
             {  0,  0,  0 },
             { -1, -2, -1 }
           };
	
	/* 
	 *------------------------ Constructors -----------------------------------*
	 */
	public ED(String filename){
		this.filename = filename;
		original = getImageFromFile(filename);
		width = original.getWidth();
		height = original.getHeight();
		product = setUpBlankImage(width, height);
		
		
	}
	
	public ED(BufferedImage img){
		original = img;
		width = original.getWidth();
		height = original.getHeight();
		product = setUpBlankImage(width, height);
		
		
	}
	/*
	 *--------------------------------------------------------------------------*
	 */
	public BufferedImage setUpBlankImage(int width, int height){
		BufferedImage image;
		if (width < 0) throw new IllegalArgumentException("width must be nonnegative");
        if (height < 0) throw new IllegalArgumentException("height must be nonnegative");

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // set to TYPE_INT_ARGB to support transparency
        
        return image;
	}
	
	//Method to get a BufferedImage from a String filename
	public BufferedImage getImageFromFile(String filename){
		BufferedImage image;
		this.filename = filename;
        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                image = ImageIO.read(file);
            }

            //try to read from file in same directory as this .class file
            else {
                URL url = getClass().getResource(filename);
                if (url == null) { url = new URL(filename); }
                image = ImageIO.read(url);
            }
            
        }
        catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }
        return image;
    }
	
	
    // truncate color to be between 0 and 255
    public  int truncate(int num) {
        if(num <   0){
        	
        	return 0;
        }else if(num > 255){
        	
        	return 255;
        }else{
        	
        	return num;
        }
    }
    
    // return the monochrome luminance of a color
    public  double lum(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299*r + .587*g + .114*b;
    }
    
    // return a gray version of this Color
    public Color toGray(Color color) {
        int c = (int) (Math.round(lum(color)));  
        Color gray = new Color(c, c, c);
        return gray;
    }
    
 // are the two colors compatible?
    public boolean compatible(Color a, Color b) {
        return Math.abs(lum(a) - lum(b)) >= 128.0;
    }
    
   //______________________________________________________________*
    
    
    //Runs the edge detection algorithm
    public  BufferedImage runDetection(){
    	 for (int y = 1; y < original.getHeight() - 1; y++) {
             for (int x = 1; x < original.getWidth() - 1; x++) {

                 // get 3-by-3 array of colors in neighborhood
                 int[][] gray = new int[3][3];
                 for (int i = 0; i < 3; i++) {
                     for (int j = 0; j < 3; j++) {
                         gray[i][j] = (int) lum(new Color(original.getRGB(x-1+i, y-1+j)));
                     }
                 }

                 // apply filter
                 int gray1 = 0, gray2 = 0;
                 for (int i = 0; i < 3; i++) {
                     for (int j = 0; j < 3; j++) {
                         gray1 += gray[i][j] * filter1[i][j];
                         gray2 += gray[i][j] * filter2[i][j];
                     }
                 }
                 // int magnitude = 255 - truncate(Math.abs(gray1) + Math.abs(gray2));
                 int magnitude = 255 - truncate((int) Math.sqrt(gray1*gray1 + gray2*gray2));
                 Color grayscale = new Color(magnitude, magnitude, magnitude);
                 product.setRGB(x, y, grayscale.getRGB());
             }
         }
         
        return product;
     }
    

    
    public BufferedImage getProduct(){
    	
		return product;
		
	}
  
	
   
}