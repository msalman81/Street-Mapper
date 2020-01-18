/* Project 3
 * Abdul Moid Munawar, amunawar, amunawar@u.rochester.edu
 * Moazzam Salman, msalman, msalman@u.rochseter.edu
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/** This is the main class that will be run in cmd**/
public class StreetMap {
	static double length = 500;
	static double width = 500;
	static List<Node> n;
	static String formatted;
	static JLabel dist=null;
	static JPanel pa;
	static JLabel startLoc ;
	static JLabel endLoc;
	static String firstLocation;
	static String secondLocation;
	static Graph g;
	static String file;
	/**main method**/
	public static void main(String[] args) {
		file = args[0];
		g = new Graph(file);
		if(args[1].equals("--show")) {
			try {
				if (args[2].equals("--directions")) {//if cmd has format --show --directions
					firstLocation= args[3];
					secondLocation=args[4];
					n = g.findShortest(firstLocation, secondLocation);//find paths between given points
					drawFrame();
					if(n!=null) {//to print out path
						System.out.println("Path: " + n.toString());
					}
				}
			}catch(Exception e) {}//to not go into above lines if cmd did not write anything beyond --show
			if(args.length==2) {//if cmd just wrote --show
				drawFrame();//draw map without any paths drawn
			}
		} else if(args[1].equals("--directions")){// if cmd wrote directions before show or just directions
			try {
			if (args[2].equals("--show")) {// show written after directions
				firstLocation= args[3];
				secondLocation=args[4];
				n = g.findShortest(firstLocation, secondLocation);
				drawFrame();
				if(n!=null) {
					System.out.println("Path: " + n.toString());
				}
			} else {//directions written without show, though it makes no difference to what happends
				firstLocation= args[2];
				secondLocation=args[3];
				n = g.findShortest(firstLocation, secondLocation);
				drawFrame();
				if(n!=null) {
					System.out.println("Path: " + n.toString());
				}
			}
			}catch(Exception e) {}
		} 
	}
	/**this method draws JFrame and calls everything to draw path and output distance in miles**/
	public static void drawFrame() {
		JFrame frame = new JFrame("Mapify");
		frame.setLayout(new BorderLayout());
		JButton nightMode=new JButton("Switch to NightMode");
		JButton findLocation=new JButton("Find");
		JTextField n1=new JTextField("Start",4);
		JTextField n2=new JTextField("End",4);
		Canvas can = new Canvas(g, n,file,false);
		nightMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {//Extra Credit Part
				boolean nightMode2=can.setNightMode();
				if(nightMode2==true) {
					nightMode.setText("Switch to DayMode ");//set background white and lines black
				}
				else {
					nightMode.setText("Switch to NightMode ");//set background black and lines white
				}
			}
		});
		/**Action Listener for JButton used to find paths through text box inputs**/
		findLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String location1=n1.getText();//get starting node from left text box
				String location2=n2.getText();//get ending node from right text box
				n = g.findShortest(location1, location2);
				can.shortestPath=(LinkedList<Node>) n;
				can.animate=false;
				can.timer.restart();
				can.repaint();
				if(n!=null) {//until all paths of route are traversed
					if(n.get(n.size()-1) == null) {
						formatted="DNE";
						dist.setText("Distance(miles): "+formatted);
						startLoc.setText("Starting(Yellow): "+ location1);//will be printed in south panel of JFrame
						endLoc.setText("Ending(Purple): "+ location2);//will be printed in south panel of JFrame
					}else {
						DecimalFormat df = new DecimalFormat("#.###");
						formatted = df.format(n.get(n.size()-1).distToSource); 
						dist.setText("Distance(miles): "+formatted);
						startLoc.setText("Starting(Yellow): "+ location1);
						endLoc.setText("Ending(Purple): "+ location2);
					}
					System.out.println("Path: " + n.toString());
				}
				
			}
		});
		JPanel pa = new JPanel(); 
		JPanel pa2 = new JPanel(); 
		JPanel pa3 = new JPanel(); 
		JPanel pa4= new JPanel(); 
		pa.add(n1);
		pa.add(n2);
		pa.add(findLocation);
		pa.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 600);
		frame.setResizable(true);
		pa.setBackground(Color.white);//default is Day Mode
		JLabel space=new JLabel("  ");
		startLoc = new JLabel("Starting(Yellow): "+ firstLocation);//label will go in south panel 
		endLoc = new JLabel("Ending(Purple): "+ secondLocation);//label will go in south panel
		System.out.println();
		DecimalFormat df = new DecimalFormat("#.###");
		dist=null;
		if(n!=null) {
			if(n.get(n.size()-1) == null) {
				formatted="DNE";
				dist = new JLabel("Distance(miles): "+formatted);
			}else {
				formatted = df.format(n.get(n.size()-1).distToSource); 
				dist = new JLabel("Distance(miles): "+formatted);
			}
			pa.add(dist);
		}
		else {
			dist = new JLabel("Distance(miles): "+"DNE");
			pa.add(dist);
		}
		pa.add(startLoc);
		pa.add(space);
		pa.add(endLoc);
		pa.add(space);
		frame.add(pa, BorderLayout.SOUTH);
		frame.add(nightMode,BorderLayout.NORTH);
		frame.add(can,BorderLayout.CENTER);
		frame.setVisible(true);
	}
}



