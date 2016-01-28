import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class ImageFrame extends JFrame {

	JSlider accuracySlider;
	JSlider pointRateSlider;
	JSlider pointCountSlider;
	JPanel panel;
	JButton processButton;
	JCheckBox colorCheck;
	JLabel j;
	File file;
	

	private BufferedImage o,b;
	private final JFileChooser chooser;
	
	public ImageFrame(int width, int height){
		
		this.setTitle("Vector Outline");
		this.setSize(width,height);
		accuracySlider = new JSlider(0, 100, 50);
		processButton = new JButton("Create");
		setUpButtons();
		pointRateSlider = new JSlider(0, 100, 50);
		pointCountSlider = new JSlider(0, 100, 50);
		colorCheck = new JCheckBox();
		colorCheck.setText("Black & White");
		panel = new JPanel();
		panel.setSize(width, height);
		j = new JLabel();
		j.setText("accuracy");
		accuracySlider.add(j);

		addMenu();
		
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
	}
	public void setUpButtons(){
		
		processButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				 drawVectors(file);
				
			}
				
		});
	}
		private void addMenu(){
			JMenu fileMenu = new JMenu( "File");
			
			JMenuItem openItem = new JMenuItem("Open");
			openItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event){
					file = open();
					if(file != null){
						
						drawVectors(file);
					}
				}
			});
		fileMenu.add(openItem);
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new 
				ActionListener()
				{
					public void actionPerformed( ActionEvent event){
						System.exit(0);
						
					}
			
				});
		
		fileMenu.add(exitItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		this.setJMenuBar(menuBar);
		
	}
		
		
	private File open(){
		
		File file = getFile();
		if (file != null){
			
			return file;
			
		}else{
			return null;
			
		}
	}
	
	private File getFile(){
		File file = null;
		if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		return file;
	}
	
	
	
	public void displayBufferedImage(BufferedImage image){
		this.setLayout(new BorderLayout());
		j.setVisible(false);
		j.setIcon(new ImageIcon(image));
		j.setVisible(true);
		panel.add(j);
		this.add(colorCheck, BorderLayout.EAST);
		this.add(processButton, BorderLayout.WEST);
		//this.add(pointCountSlider, BorderLayout.EAST);
		this.add(pointRateSlider,  BorderLayout.SOUTH);
		this.add(accuracySlider, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
		repaint();
		this.validate();		
		
	
	}
	
	private void drawVectors(File file){
	
		
		try{
			
			
			  b = ImageIO.read(file);
			 // b = Blur.fastblur(b, 7);
			  int[] image_data = computePixels(b);
			  int[] colorData = computePixels(b);
			  if(colorCheck.isSelected()){
				  image_data = GreyScale.greyScale(image_data);
			  }
			 
			  
			  int[] edgeImageData = EdgeDetector.detectEdges(b, image_data, accuracySlider.getValue(), 5 , 1);
			  ArrayList<Point> edgePoints = EdgePoints.getEdgePoints(b.getWidth(), b.getHeight(), edgeImageData, 50, accuracySlider.getValue());
			  ArrayList<Point> edgeVertices = RandomVertices.getRandomVertices(edgePoints, scaleRange(pointRateSlider.getValue(), 0.0f, 100.0f, .001f , .1f ), (int) scaleRange(pointCountSlider.getValue(), 0, 100, 100, 5000 ), accuracySlider.getValue(), b.getWidth(), b.getHeight());
			
			  ArrayList<Triangle> polygons = Triangulate.triangulate(edgeVertices);
			  ArrayList<Triangle> triangles;

			 
			  
			  //check whether we should color the triangles with color data or black and white data
			  if(colorCheck.isSelected()){
				   triangles = getColorfulTriangles(polygons, image_data, b.getWidth());
			  }else{
				   triangles = getColorfulTriangles(polygons, colorData, b.getWidth());

 
			  }
			  
			  //write to svg 
			  createSVG(triangles);
			  
			  
			  Graphics2D g2d = (Graphics2D) b.createGraphics();

			  drawTriangles(triangles, g2d);
			  displayBufferedImage(b);

			  
			  
			  //  tmp_ctx.drawImage( b, 0, 0 );
/*
				// get the image data
				image_data         = tmp_ctx.getImageData( 0, 0, tmp_canvas.width, tmp_canvas.height );

				// since the image data is blurred and greyscaled later on,
				// we need another copy of the image data with preserved colors
				color_data         = tmp_ctx.getImageData( 0, 0, tmp_canvas.width, tmp_canvas.height );

				// blur the imagedata using superfast blur by @quasimondo
				// not very accurate, but fast
				blurred_image_data = blur( image_data, values.blur, false );

				greyscale_data     = greyscale( image_data );
				edge_image_data    = detectEdges( greyscale_data, values.accuracy, 5 );

				// gets some of the edge points to construct triangles
				edge_points        = getEdgePoints( edge_image_data, 50, values.accuracy );
				edge_vertices      = getRandomVertices( edge_points, values['point-rate'], values['point-count'], values.accuracy, tmp_canvas.width, tmp_canvas.height );

				// makes triangles out of points
				polygons           = triangulate( edge_vertices );

				// get the color for every triangle
				triangles          = getColorfulTriangles( polygons, color_data );

				drawTriangles( ctx, triangles );
		*/
			
		}catch( IOException exception){
			JOptionPane.showMessageDialog(this,exception);
		}
		
	}
	
	public void drawTriangles(ArrayList<Triangle> triangles, Graphics2D g2d){
		
		for(Triangle t : triangles){
			
			t.draw(g2d);
			
		}
	}
	
	public ArrayList<Triangle> getColorfulTriangles( ArrayList<Triangle>triangles, int[] color_data , int width)
	{
		

		for ( Triangle triangle : triangles)
		{
			

			// triangle color = color at center of triangle
			float triangle_center_x = ( triangle.a.x + triangle.b.x + triangle.c.x ) * 0.33333f;
			float triangle_center_y = ( triangle.a.y + triangle.b.y + triangle.c.y ) * 0.33333f;

			int pixel = ( ((int) triangle_center_x ) * 4 ) + (((int)triangle_center_y) * width * 4);
			
			triangle.setColor(new Color(color_data[pixel], color_data[pixel + 1], color_data[pixel + 2]));
		}

		return triangles;
	}
	
	//Method to store the pixels of a buffered image into a single array
	public int[] computePixels(BufferedImage b){
		
		int[] pixels = new int[b.getWidth() * b.getHeight() * 4];

		for(int j = 0; j < b.getHeight(); j++){
			for(int i = 0; i < b.getWidth(); i++){
			
				
				// get aRGB values for each pixel and store them in a single array.
				int aRGB = b.getRGB(i, j);
				
				int alpha = (aRGB >>> 24) & 0x000000FF;
				int red = (aRGB >>> 16) & 0x000000FF;
				int green = (aRGB >>> 8 ) & 0x000000FF;
				int blue = (aRGB) & 0x000000FF;
				
				//width * 4 for each argb , get to the right row by multiplying j by the width
				pixels[ (i * 4) + (j * b.getWidth() * 4)] = red;
				pixels[(i * 4 + 1) + (j * b.getWidth() * 4)] = green;
				pixels[(i * 4 + 2) + (j * b.getWidth() * 4)] = blue;
				pixels[(i * 4 + 3) + (j * b.getWidth() * 4)] = alpha;


				

				
			}
			
			
		}

		return pixels;
	}
	
	
	public void setUpGraphics2D(Graphics2D g2d){
		
		//sets the rendering hints for the g2d
	  RenderingHints cR = new RenderingHints(
	             RenderingHints.KEY_COLOR_RENDERING,
	             RenderingHints.VALUE_COLOR_RENDER_SPEED);
	  g2d.setRenderingHints(cR);
	  
	  RenderingHints aA = new RenderingHints(
	             RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
	  g2d.setRenderingHints(aA);
	  
	  RenderingHints r = new RenderingHints(
	             RenderingHints.KEY_RENDERING,
	             RenderingHints.VALUE_RENDER_SPEED);
	  g2d.setRenderingHints(r);
	  
	  Color c = new Color(255, 0, 0);//set color
	  g2d.setColor(c);
	  
	  BasicStroke s = new BasicStroke();//set stroke
	  g2d.setStroke(s);
		
		
	}
	
	public BufferedImage pixelArrayToBuffer(BufferedImage b, int[] pixels){
		
		BufferedImage o = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);

	
		for(int i = 0; i < pixels.length - 3; i+=4){
				int y = i / (b.getWidth() * 4);
				int x = (i % (b.getWidth() * 4))/4;
				
				int red = pixels[i];
				int green = pixels[i + 1];
				int blue = pixels[i + 2];
				int alpha = pixels[i + 3];
				
				
				int aRGB = (alpha << 24) | (red << 16) | ( green << 8) | blue;
				o.setRGB(x, y,  aRGB);
				

			}
		
		return o;
	}

	public float scaleRange( float value, float low_1, float high_1,float low_2,float high_2 )
	{
		return low_2 + ( high_2 - low_2) * ( value - low_1 ) / (high_1 - low_1 );
	}
	
	public void setUpSVGHeader(FileWriter writer){
		try{
		 writer.write("<?xml version=\"1.0\" standalone=\"no\"?>\n"
		      		+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n"
		      		+ " \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		
		}catch(IOException e){}
		
	}
	public void setUpSVGBody(FileWriter writer, int width, int height, int x1, int y1, int x2, int y2 ){
		try{
		 writer.write("<svg width=\"12cm\" height=\"4cm\" viewBox=\"0 0 1200 400\"\n"
		      		+ "\t xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n"
		      		+ "<desc>Example polyline01 - increasingly larger bars</desc>\n"
		      		+ "<!-- Show outline of canvas using \'rect\' element -->\n");
		}catch(IOException e){}
		
	}
	public void writeSVGPath(FileWriter writer,Triangle triangle){
		
		try{
		
	      // Writes the content to the file
			writer.write("<path d=\"M" + triangle.a.x + "," + triangle.a.y + "\n");
			writer.write("\t\t\t  L " + triangle.b.x + "," + triangle.b.y + " \n");
			writer.write("\t\t\t  L " + triangle.c.x + "," + triangle.c.y + " \n");
     
			writer.write("  z\"\n");

            writer.write("fill=\"" + toHexString(triangle.color) +  "\" stroke=\" "+ toHexString(triangle.color) + "\" ");
           
            writer.write("/>\n");
           
		}catch(IOException i){}
	}
	public void setUpSVGEnd(FileWriter writer){
		try{
		  writer.write("</svg>");	
	      writer.flush();
	      writer.close();
		}catch(IOException ioe){}
	}
	
	
	void createSVG(ArrayList<Triangle> triangles){
		
	
		File file = new File("triangulate.svg");
		try{
			
			// creates the file
			file.createNewFile();
			// creates a FileWriter Object
			FileWriter writer = new FileWriter(file); 
			 
	  
		
			  setUpSVGHeader(writer);	
			  setUpSVGBody(writer, 12,4,0,0,1200,400);
			  for(Triangle triangle : triangles){
				  writeSVGPath(writer,triangle);
			  }
		      
		      setUpSVGEnd(writer);
			  
			  
		}catch(Exception e){}
	}
	
	public final static String toHexString(Color colour) throws NullPointerException {
		  String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
		  if (hexColour.length() < 6) {
		    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		  }
		  return "#" + hexColour;
		}
	
}