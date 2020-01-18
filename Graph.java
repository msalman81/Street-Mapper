/* Project 3
 * Abdul Moid Munawar, amunawar, amunawar@u.rochester.edu
 * Moazzam Salman, msalman, msalman@u.rochseter.edu
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
/**This class is to do all Graph theory related computations of our map**/
public class Graph {
	// Store list of vertices in a hash set to prevent duplicates
	//Every Node has an IntersectionID, and every edge has a RoadID
	HashMap<String, Node> vertices;//IntersectionId goes into key, and Node itself goes into Value
	public boolean pathExists;
	double maxLatitude;
	double minLatitude;
	double maxLongitude;
	double minLongitude;
	/**construct a graph from a text file**/
	public Graph(String filename) {
		vertices = new HashMap<String, Node>();
		maxLatitude = maxLongitude = -1*Double.MAX_VALUE;
		minLatitude = minLongitude = Double.MAX_VALUE;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			String type;
			while ((line = reader.readLine()) != null) {

				String[] tokenArray=line.split("	");
				type = tokenArray[0];
				if (type.equals("i")) {
					this.addIntersection(tokenArray[1], Double.parseDouble(tokenArray[2]),
							Double.parseDouble(tokenArray[3]));
				} else if (type.equals("r")) {
					this.addEdge (tokenArray[1], tokenArray[2], tokenArray[3]);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}//End of Constructor
	
	
/** this method adds a new Node into the HashMap vertices and also resets max and min lattitude
 * and longitude if needed
 * @param IntersecID
 * @param latitude
 * @param longitude
 */
	void addIntersection(String IntersecID,double latitude, double longitude) {
		vertices.put(IntersecID, new Node(IntersecID, latitude, longitude));
		if(latitude<minLatitude) {
			minLatitude=latitude;
		}
		else if(latitude>maxLatitude) {
			maxLatitude=latitude;
		}
		if(longitude<minLongitude) {
			minLongitude=longitude;
		}
		else if(longitude>maxLongitude) {
			maxLongitude=longitude;
		}
	}
	
	/**This method adds a new Edge by adding both nodes into each others adj lists
	 * @param roadID
	 * @param intersec1ID
	 * @param intersec2ID
	 */
	void addEdge(String roadID, String intersec1ID, String intersec2ID) {
		Node intersec1 = vertices.get(intersec1ID);
		Node intersec2 = vertices.get(intersec2ID);

		if (intersec1 == null) {
			System.out.println(intersec1ID + " does not exist on the graph");
			return;
		}
		if (intersec2 == null) {
			System.out.println(intersec2ID + " does not exist on the graph");
			return;
		}

		intersec1.adjlist.put(intersec2, new Edge(roadID, intersec1, intersec2));
		intersec2.adjlist.put(intersec1, new Edge(roadID, intersec2, intersec1));

	}
	
	/**Method which implements Dijsktras Algorithm to find shortest Path between 2 nodes
	 * 
	 * @param intersec1ID
	 * @param intersec2ID
	 * @return
	 */
	public LinkedList<Node> findShortest(String intersec1ID,String intersec2ID){

		for(String x:vertices.keySet()) {
			vertices.get(x).distToSource=Integer.MAX_VALUE;
			vertices.get(x).visited=false;
		}
		Node Node1;
		Node Node2;

		Node1=vertices.get(intersec1ID);
		Node2=vertices.get(intersec2ID);
		
		//If either Node does not exist then print on console that input is incorrect
		if(Node1==null) {
			System.out.println("The entered points are wrong");
			return null;
		}
		if(Node2==null) {
			System.out.println("The entered points are wrong");
			return null;
		}


		Node path = null;//this Node will be used to stitch together path 
		LinkedList<Node> shortestPath=new LinkedList<Node>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		Node1.distToSource=0;//source distance set to zero
		queue.add(Node1);
		ArrayList<Node> visitedNodes= new ArrayList<Node>();
		if(Node1==Node2) {//If both nodes are equal then special case, return distance 0 and Node itself as path
			shortestPath.add(Node1);
			shortestPath.add(Node2);
			return shortestPath;//end method
		}
		
		//Start of Alogrithm to find shortest path//
		while(!queue.isEmpty()) {
			Node current=queue.poll();//remove min Node from queue, will nver be added again once removed
			visitedNodes.add(current);
			if(current==Node2) {
				path=current;
				break;
			}
			for(Node s:current.adjlist.keySet()){
				if(s.visited==false) {
					if(current.distToSource+(current.adjlist.get(s).weight)<s.distToSource) {//Update distance to source of Node is shorter path found
						queue.remove(s);
						s.updater=current;//keep track of node that updates other node to restitch path in the end
						s.distToSource=current.distToSource+(current.adjlist.get(s).weight);
						queue.add(s);//add updated node back to queue
					}
				}
			}
			current.visited=true;		
		}
		Stack<Node> stack = new Stack<Node>(); 	
		stack.push(path);//add last node  to queue, will be destination
		do {

			try {
				path=path.updater;//path will jump to updater of previous nodes one by one
				stack.push(path);// and thus add shortest path to stack in reverse order
			}catch(Exception e) {//if any null is found while going back then path does not exist
				System.out.println("The path between these two points do not exist.");		
				break;			}
		}while(path!=Node1);//continue pushed stack till first node is found and added. Will exit after Node1 is added

		while(!stack.isEmpty()) {
			shortestPath.add(stack.pop());//path is popped so that Node1 is popped first and destination Node last
		}
		return shortestPath;
	}

}
