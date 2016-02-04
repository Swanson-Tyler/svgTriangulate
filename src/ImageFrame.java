import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ImageFrame extends JFrame {

	JSlider accuracySlider;
	JSlider pointRateSlider;
	JSlider pointCountSlider;
	JPanel panel;
	JButton calculateButton;
	JButton svgButton;
	Graphics2D g2d;
	JCheckBox colorCheck;

	JTextField hexThreshold;
	JCheckBox greaterThan;
	JCheckBox lessThan;

	JLabel outputImage;
	JLabel pointCountLabel;
	JLabel accuracyLabel;
	JLabel colorThresholdLabel;

	File file;
	BufferedImage myPicture;
	JLabel picLabel;

	JPanel imagePanel;
	JPanel sliderPanel;
	JPanel thresholdPanel;

	private BufferedImage bufferedImage;
	private final JFileChooser chooser;
	private ArrayList<Triangle> triangles;
	
	public ImageFrame(int width, int height){

		this.setTitle("Vector Outline");
		this.setSize(width, height);
		accuracySlider = new JSlider(1, 100, 50);
		calculateButton = new JButton("Calculate");
		calculateButton.setSize(new Dimension(50, 50));

		svgButton = new JButton("Save SVG");
		svgButton.setSize(new Dimension(50, 50));

		setUpButtons();
		pointRateSlider = new JSlider(0, 100, 50);
		pointCountSlider = new JSlider(0, 100, 50);
		colorCheck = new JCheckBox();
		colorCheck.setText("Black & White");

		hexThreshold = new JTextField();
		hexThreshold.setSize(100, 20);
		hexThreshold.setText("#");

		greaterThan = new JCheckBox();
		greaterThan.setText("Greater than (>)");

		lessThan = new JCheckBox();
		lessThan.setText("Less than (<)");
		setUpCheckboxes();

		sliderPanel = new JPanel();
		sliderPanel.setSize(50, 100);

		panel = new JPanel();
		panel.setSize(200, height);

		thresholdPanel = new JPanel();
		thresholdPanel.setSize(200, height);

		pointCountLabel = new JLabel();
		pointCountLabel.setText("Point Count:");

		accuracyLabel = new JLabel();
		accuracyLabel.setText("Accuracy:");

		colorThresholdLabel = new JLabel();
		colorThresholdLabel.setText("Color Threshold:");

		outputImage = new JLabel();
		imagePanel = new JPanel();
		imagePanel.setSize(200, 200);

		setBackgroundImage();

		addMenu();
		
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
	}

	public void setBackgroundImage(){
		try {
			myPicture = ImageIO.read(new File("background.png"));
		}catch(Exception e){}

		picLabel = new JLabel(new ImageIcon(myPicture));
		picLabel.setBounds(0, 0, 1920, 1080);
		this.add(picLabel);
		repaint();
		validate();
	}
	public void setUpCheckboxes(){
		greaterThan.setSelected(true);
		greaterThan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lessThan.isSelected()) {
					lessThan.setSelected(false);
				}

			}
		});
		lessThan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (greaterThan.isSelected()) {
					greaterThan.setSelected(false);
				}
			}
		});
	}

	public void setUpButtons(){

		svgButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(lessThan.isSelected() || greaterThan.isSelected()){
					ArrayList<Triangle> filteredTriangles = filterTriangles(lessThan.isSelected(), greaterThan.isSelected(), hexThreshold.getText());
					if(filteredTriangles != null){

						g2d.setBackground(new Color(0, 0, 0, 0));
						g2d.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
						drawTriangles(filteredTriangles, g2d);
						displayBufferedImage(bufferedImage);

						createSVG(filterTriangles(lessThan.isSelected(), greaterThan.isSelected(), hexThreshold.getText()));
					}else{
						createSVG(triangles);
					}
				}
			}
		});

		calculateButton.addActionListener(new ActionListener() {

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

	public void displayBufferedImage(BufferedImage image) {

		float aspectRatio = (float) image.getWidth()/(float) image.getHeight();
		image = scaleImage(image, 500 , (int) (500 / aspectRatio));
		this.setLayout(new GridLayout(0, 1));

		thresholdPanel.setLayout(new GridLayout(0, 2));

		outputImage.setVisible(false);
		outputImage.setIcon(new ImageIcon(image));
		outputImage.setVisible(true);

		sliderPanel.add(accuracyLabel);
		sliderPanel.add(accuracySlider);
		sliderPanel.add(pointCountLabel);
		sliderPanel.add(pointRateSlider);

		thresholdPanel.add(greaterThan);
		thresholdPanel.add(lessThan);
		thresholdPanel.add(hexThreshold);
		thresholdPanel.add(svgButton);

		imagePanel.add(outputImage);

		panel.add(colorCheck);
		panel.add(calculateButton);
		panel.add(sliderPanel);
		panel.add(thresholdPanel);
		panel.add(imagePanel);

		this.add(panel);

		repaint();
		this.validate();		

	}
	
	private void drawVectors(File file){
	
		
		try{
			 bufferedImage = ImageIO.read(file);
			 // b = Blur.fastblur(b, 7);
			  int[] image_data = computePixels(bufferedImage);
			  int[] colorData = computePixels(bufferedImage);
			  if(colorCheck.isSelected()){
				  image_data = GreyScale.greyScale(image_data);
			  }

			  int[] edgeImageData = EdgeDetector.detectEdges(bufferedImage, image_data, 100 - accuracySlider.getValue(), 5 , 1);
			  ArrayList<Point> edgePoints = EdgePoints.getEdgePoints(bufferedImage.getWidth(), bufferedImage.getHeight(), edgeImageData, 50, 100 - accuracySlider.getValue());
			  ArrayList<Point> edgeVertices = RandomVertices.getRandomVertices(edgePoints, scaleRange(pointRateSlider.getValue(), 0.0f, 100.0f, .001f, .1f), (int) scaleRange(pointCountSlider.getValue(), 0, 100, 100, 5000), 100 - accuracySlider.getValue(), bufferedImage.getWidth(), bufferedImage.getHeight());
			
			  ArrayList<Triangle> polygons = Triangulate.triangulate(edgeVertices);

			  //check whether we should color the triangles with color data or black and white data
			  if(colorCheck.isSelected()){
				   triangles = getColorfulTriangles(polygons, image_data, bufferedImage.getWidth());
			  }else{
				   triangles = getColorfulTriangles(polygons, colorData, bufferedImage.getWidth());
			  }
			  
			  g2d = bufferedImage.createGraphics();
			  drawTriangles(triangles, g2d);
			  displayBufferedImage(bufferedImage);

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
	public void setUpSVGBody(FileWriter writer){
		try{
		 writer.write("<svg width=\"" + bufferedImage.getWidth() + "\" height=\"" + bufferedImage.getHeight() + "\" viewBox=\"0 0 " + bufferedImage.getWidth() + " " + bufferedImage.getHeight() + "\"\n"
		      		+ "\t xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n"
		      		+ "<desc>Example polyline01 - increasingly larger bars</desc>\n"
		      		+ "<!-- Show outline of canvas using \'rect\' element -->\n");
		}catch(IOException e){}
		
	}
	public void writeSVGPath(FileWriter writer,Triangle triangle){
		
		try{
		
			writer.write("<path d=\"M" + triangle.a.x + "," + triangle.a.y + "\n");
			writer.write("\t\t\t  L " + triangle.b.x + "," + triangle.b.y + " \n");
			writer.write("\t\t\t  L " + triangle.c.x + "," + triangle.c.y + " \n");
			writer.write("  z\"\n");
            writer.write("fill=\"" + toHexString(triangle.color) +  "\" stroke-width=\" "+ 0 + "\" ");
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
			
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			  setUpSVGHeader(writer);	
			  setUpSVGBody(writer);
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

	public static BufferedImage scaleImage(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public ArrayList<Triangle> filterTriangles(boolean lessThanChecked, boolean greaterThanChecked, String threshold) {

		String hexValue;
		ArrayList<Triangle> filteredTriangles = new ArrayList<Triangle>();
		if(threshold.length() == 7 && threshold.substring(0,1).equals("#")) {

			if (greaterThanChecked) {
				for (Triangle triangle : triangles) {
					hexValue = toHexString(triangle.color);
					if (Integer.parseInt(hexValue.substring(1), 16) < Integer.parseInt(threshold.substring(1), 16)) {
						filteredTriangles.add(triangle);
					}
				}
			} else {
				for (Triangle triangle : triangles) {
					hexValue = toHexString(triangle.color);
					if (Integer.parseInt(hexValue.substring(1), 16) > Integer.parseInt(threshold.substring(1), 16)) {
						filteredTriangles.add(triangle);
					}
				}
			}
			return filteredTriangles;
		}
		return null;

	}
}