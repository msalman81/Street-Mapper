/* Project 3
 * Abdul Moid Munawar, amunawar, amunawar@u.rochester.edu
 * Moazzam Salman, msalman, msalman@u.rochseter.edu
 */
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**This class is called in StreetMap used to draw Map and to perform some of our extra credit**/
public class Canvas extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double length; 
	double width;
	int megaCount=0;
	Timer timer;
	public boolean nightMode;
	String file;
	public String finalDistance;
	Graph graph; 
	int xaxis1,xaxis2,yaxis1,yaxis2;
	boolean showPath=false;
	int globalCounter=0;
	boolean animate=false;
	LinkedList<Node> shortestPath;
	public Canvas(Graph g) {
		this.graph = g;
		showPath=false;
	}
	/**Constructor For Canvas**/
	public Canvas(Graph g,List<Node> n,String file,boolean nightMode) {
		this.nightMode=nightMode;
		this.file=file;
		graph=g;
		shortestPath=(LinkedList<Node>) n;
		showPath=true;
		timer = new Timer(300, new TimerCallBack());
		timer.start();

	}
	/**For the animating line along path**/
	public class TimerCallBack implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				Node i=shortestPath.get(globalCounter);
				switch(file) {
				case "ur.txt":
					globalCounter=globalCounter+1;
					break;
				case "monroe.txt":
					globalCounter=globalCounter+4;
					break;
				case "nys.txt":
					globalCounter=globalCounter+100;
					break;
				default:
					globalCounter++;
				}

				Node b=shortestPath.get(globalCounter);
				xaxis1= (int) generateX(i);

				yaxis1=(int) generateY(i);

				yaxis2=(int) generateY(b);
				xaxis2=(int) generateX(b);
				animate=true;
				repaint();
			}catch(Exception e) {
				timer.stop();
				animate=false;
				repaint();//called repeatedly to redraw line along path, thus animating it
			}
		}
	}
	/**generate Xcoordinate of each Node keeping scaling in mind**/
	public double generateX(Node n) {
		double padding = 0.0;
		if(getWidth() > getHeight()) {
			padding = (getWidth() - getHeight())/2;
		}

		return ((n.longitude-graph.minLongitude)/(graph.maxLongitude - graph.minLongitude)*width) + padding;
	}
	/**generate Ycoordinate of each Node keeping scaling in mind**/
	public double generateY(Node n) {
		double padding = 0.0;
		if(getHeight() > getWidth()) {
			padding = (getHeight() - getWidth())/2;
		}
		return (length - (n.lattitude-graph.minLatitude)/(graph.maxLatitude - graph.minLatitude)*length)+ padding;
	}


	@Override
	public void paintComponent(Graphics g) {

		length = getHeight();
		width = getWidth();

		Graphics2D g2 = (Graphics2D) g;

		if(nightMode==true) {
			g.setColor(Color.BLACK);
			g2.fillRect(0,0,(int)width,(int)length);
		}else {
			g.setColor(Color.WHITE);
			g2.fillRect(0,0,(int)width,(int)length);
		}
		BufferedImage img = null;
		BufferedImage img2=null;

		try {
			img = ImageIO.read(new File("map.png"));//yellow icon that will be added on starting location
			img2 = ImageIO.read(new File("map2.png"));//purple icon that will be added on starting location
		} catch (IOException e) {}
		if (getWidth() > getHeight()) {
			length = width = getHeight();
		} else {
			length = width = getWidth();
		}

		for(String s:graph.vertices.keySet()) {
			Node node=graph.vertices.get(s);

			int x1= (int) generateX(node);

			int y1=(int) generateY(node);
			for(Node b:node.adjlist.keySet()) {
				int y2=(int) generateY(b);
				int x2=(int) generateX(b);
				if(nightMode) {
					g.setColor(Color.WHITE);
					g.drawLine(x1, y1, x2, y2);
				}
				else {
					g.setColor(Color.BLACK);
					g.drawLine(x1, y1, x2, y2);
				}


			}
		}
		//if path needs to be drawn then following code is run
		if(showPath) {
			int counter=0;
			Node initial = null,second = null;
			int y2 = 0,x2 = 0;
			int x1=0;
			int y1=0;

			if(shortestPath!=null) {
				for (Iterator<Node> i = shortestPath.iterator(); i.hasNext();) {
					try {
						if(counter==0) {
							initial=(Node) i.next();

						}else {
							initial=second;
						}
						second=(Node) i.next();;
						x1= (int) generateX(initial);

						y1=(int) generateY(initial);
						if(counter==shortestPath.size()/2) {
							int centerx=x1;
							int centery=y1;
						}
						y2=(int) generateY(second);
						x2=(int) generateX(second);
						if(counter==0) {
							g2.drawImage(img, x1-8, y1-20, 17, 20, this);
						}
						g2.setColor(Color.RED);
						g2.setStroke(new BasicStroke(3));
						g2.drawLine(x1, y1, x2, y2);
						counter++;

					}catch(Exception e) {

					}

				}
				g2.drawImage(img2, x2-8, y2-20, 17, 20, this);
			}
		}//end of drawpath

		//if animation needs to be drawn along path
		if(animate==true) {
			do {
				g2.setColor(Color.black);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(xaxis1, yaxis1, xaxis2, yaxis2);
				animate=false;
			}while(animate==true);
		}

	}
	public boolean setNightMode() {//Extra credit part that sets to nightmode or back to day mode
		if(this.nightMode==true) {
			this.nightMode=false;
			repaint();
			return this.nightMode;

		}
		else {
			this.nightMode=true;
			repaint();
			return this.nightMode;
		}
	}



}
