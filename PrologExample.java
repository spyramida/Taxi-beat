package com.ai;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Hashtable;
import com.ai.ParseJava;
import org.jpl7.*;
import org.jpl7.fli.*;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/*
 *-Create KML. For every taxi in the map, calculates A* distance from client.
 * 	The choice of the best taxi, is taking under consideration the distance 
 * 	between the taxis and the client, as well as other logic parameters given
 * 	by swipl
 * 
 *-aStar. Given a taxi and a client finds min distance between them. The F and G
 *	values used for A*, are being modified through logic clauses using swipl.
 *
 *-main. Responsible for the file inputs as well as pre-processing of the data,
 *	such as creating a map using the given nodes, finding the exact spot of the 
 *	taxis and the client (closest node)
 *
 *
 *
 *
 *
 *
 *
*/


public class MapGraph {

	// A list containing all the nodes
	static List<Node> allnodes = new ArrayList<Node>();
	static int time = 200;
	
	//partition for quicksort on 2-dim array
	public static int partition(double arr[][], int low, int high)
    {
        double pivot = arr[high][0]; 
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j][0] <= pivot)
            {
                i++;
 
                // swap arr[i] and arr[j]
                double temp = arr[i][0];
                arr[i][0] = arr[j][0];
                arr[j][0] = temp;
                temp = arr[i][1];
                arr[i][1] = arr[j][1];
                arr[j][1] = temp;
            }
        }
 
        // swap arr[i+1] and arr[high] (or pivot)
        double temp = arr[i+1][0];
        arr[i+1][0] = arr[high][0];
        arr[high][0] = temp;
        temp = arr[i+1][1];
        arr[i+1][1] = arr[high][1];
        arr[high][1] = temp;
 
        return i+1;
    }
	
	//quicksort for two dim array. sorts on 1st element
	public static void sort(double arr[][], int low, int high)
	{
	    if (low < high)
	    {
	        /* pi is partitioning index, arr[pi] is now
	           at right place */
	        int pi = partition(arr, low, high);

	        sort(arr, low, pi - 1);  // Before pi
	        sort(arr, pi + 1, high); // After pi
	    }
	}
	
	// finds the maximum element of the given list
	public static Node maxf(List<Node> searchFront) {
		Node maximum;
		maximum = searchFront.get(0);
		for (Node tmp : searchFront) {
			if (tmp.getF() > maximum.getF()) {
				maximum = tmp;
			}
		}
		return maximum;
	}

	// absolute value of two doubles
	public static double abs(double a, double b) {
		if (a < b)
			return (b - a);
		return (a - b);
	}

	// manhattan distance of two nodes
	public static double dist(Node a, Node b) {
		return (abs(a.x, b.x) + abs(a.y, b.y));
	}

	// eucledian dist of two nodes
	public static double EUdist(Node a, Node b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	// finds and removes the minimum element of the given list
	public static Node minf(List<Node> searchFront) {
		Node minimum;
		minimum = searchFront.get(0);
		for (Node tmp : searchFront) {
			if (tmp.getF() < minimum.getF()) {
				minimum = tmp;
			}
		}
		searchFront.remove(minimum);
		return minimum;
	}
	
	//Creates the KML file as well as present user his options
	public static void createKML(Client clientNode,Node startNode,Node destNode,List<Node> taxis,int MaximumCapacity,int num_of_taxis,int k) {
		try {
			PrintWriter writer = new PrintWriter("mykml.kml", "UTF-8");
			String stringstart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.1\"><Document><name>Taxi Routes</name><Style id=\"green\"><LineStyle><color>ff009900</color><width>4</width></LineStyle></Style><Style id=\"red\"><LineStyle><color>ff0000ff</color><width>4</width></LineStyle></Style>";
			String stringopen="";
			String stringclose = "</coordinates></LineString></Placemark>";
			String stringend = "</Document></kml>";
			String output = "";
			
			String teliko = stringstart;
			List<Node> daarray;
			List<String> colorieString,outputString;
			colorieString = new ArrayList<String>();
			outputString = new ArrayList<String>();
			daarray = new ArrayList<Node>();
			//array with all the taxis
			ArrayList<Node> taxis_array = new ArrayList<Node>();
			//array with the taxis-distances and a pointer. usefull so that i can
			//pinpoint the taxis with the minimum distance after sorting dist[][]
			double[][] dist = new double[taxis.size()+1][2];
			
			/* * * * * * * * * * * * * * * * */
			/* E V E R Y   T A X I   T R I P */
			/* * * * * * * * * * * * * * * * */
			for (int i=0; i<num_of_taxis; i++){
				
				Node endNode = taxis.get(i);
				taxis_array.add(endNode);
				/* * * * * * * * * * * * */
				/* A*  E X E C U T I O N */
				/* * * * * * * * * * * * */
				daarray=aStar(startNode,startNode,endNode,MaximumCapacity);
				dist[i][0] = 0;
				dist[i][1] = i;
				Node lastnode=daarray.get(0);
				//for every point of the A* path, store the total distance
				for (Node j : daarray) {
					dist[i][0]=dist[i][0]+EUdist(j,lastnode);
					lastnode=j;
				}
				
			}
			
			sort(dist,0,dist.length-1);
			
			//I want to present the k-best taxis. For k=5:
			
			if (k>taxis_array.size())
				k=taxis_array.size()-1;
			System.out.println("Choose one of the following "+k+"-taxis: \n");
			for (int q=1; q<=k; q++){
				int tmp = ((int) dist[q][1]);
				Node tmpNode = taxis_array.get( tmp );
				System.out.println(tmpNode.id+"("+tmpNode.x+","+tmpNode.y+") at dist:"+dist[q][0]);
				//Prolog:
			}
			
			/* * * * * * * * * * */
			/* F I N D   T R I P */
			/* * * * * * * * * * */
			daarray=aStar(startNode,startNode,destNode,MaximumCapacity);
			Node lastnode=daarray.get(0);
			//for every point of the A* path, store the total distance
			for (Node j : daarray) {
				lastnode=j;
				String no1 = Double.toString(j.x);
				String no2 = Double.toString(j.y);
				output = output.concat(no1).concat(",").concat(no2).concat("\n");
			}

			outputString.add(output);
			stringopen = "<Placemark><name>Trip</name><styleUrl>#red</styleUrl><LineString><altitudeMode>relative</altitudeMode><coordinates>";
			colorieString.add(stringopen);
			output="";
			stringopen=colorieString.get(0);
			output=outputString.get(0);
			teliko = teliko.concat(stringopen).concat(output).concat(stringclose);
			teliko=teliko.concat(stringend);
			writer.println(teliko);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static class Node {
		public String roadName;
		public int id;
		public double x;
		public double y;
		public ArrayList<Node> adjacencyList = new ArrayList<Node>();
		Node previous;
		double Fdist;
		double Gdist;
		double Hdist;

		public Node(int ID, double x, double y) {
			// this.roadName = roadName;
			this.id = ID;
			this.x = x;
			this.y = y;
		}

		public void setPrevious(Node tmp) {
			this.previous = tmp;
		}

		//real distance
		public void setGdist(double tmp) {
			this.Gdist = tmp;

		}

		//hieuristic distance
		public void setHdist(double dist) {
			this.Hdist = dist;

		}

		public double getF() {
			return Hdist + Gdist;
		}

	}

	static class Client {
		private String line;
		private String[] tempInput = new String[9];
		public double clientX;
		public double clientY;
		public double destX;
		public double destY;
		public int time;
		public int persons;
		public String language;
		public int luggage;

		public void readClient(String file) {
			try (BufferedReader bh = new BufferedReader(new FileReader(file))) {
				line = bh.readLine();// skipparw thn 1h grammh
				final ParseJava something = new ParseJava();
				while ((line = bh.readLine()) != null) {
					tempInput = line.split(",");
					this.clientX = Double.parseDouble(tempInput[0]);
					this.clientY = Double.parseDouble(tempInput[1]);
					this.destX = Double.parseDouble(tempInput[2]);
					this.destY = Double.parseDouble(tempInput[3]);
					this.time = something.parse_this(tempInput[4].split(":")[0])*100+something.parse_this(tempInput[4].split(":")[1]);
					this.persons = something.parse_this(tempInput[5]);
					this.language = tempInput[6];
					this.luggage = something.parse_this(tempInput[7]);

				}
			} catch (IOException f) {
				f.printStackTrace();
			}
		}

	}

	//A* implementation
	public static List<Node> aStar(Node current, Node startNode, Node endNode, int MaximumCapacity) {
		List<Node> searchFront, searched;
		double tmpdist,velocity=1;
		searchFront = new ArrayList<Node>();
		searched = new ArrayList<Node>();
		current.setGdist(0);
		searchFront.add(current);
		//System.out.println("Starting A* with start:("+startNode.x+","+startNode.y+") and client at: (1,1)\n andend:("+endNode.x+","+endNode.y+") ");
		int capacity = 0;
		//int num_of_steps=0;
		//int real_size=0;
		
		/* * * * * * */
		/* L O G I C */
		/* 
		/* * * * * * */
		
		
		while (!searchFront.isEmpty()) {
			if (current == endNode) {
				break;
			}
			//num_of_steps++;
			searched.add(current);
			// add the not-searched neighboors of the current node
			// in the searchFront list
			for (Node neighb : current.adjacencyList) {
				String t1;
				Query q1;
				if (!searched.contains(neighb)) {
					//real_size++;
					if (neighb.getF() != 0) {
						// if getF!=0, there is already an instance of neighb.
						// If that instance, has smaller F
						// then I keep it, else, I change it with the new neighb
						if (neighb.getF() > dist(neighb, endNode) + EUdist(neighb, current) + current.Gdist) {
							
							/*!!!*/
							//Hdist and velocity will be taken by prolog
							//Is there traffic? (current,neighb,id,time)
							//Is it a highway? (current,neighb,id)
							neighb.setHdist(dist(neighb, endNode));
							
							t1 = "next("+current.id+","+neighb.id+").";
							q1 = new Query(t1);
							if (q1.hasSolution()){
								t1 = "velocity("+current.id+","+neighb.id+","+time+",X).";
								q1 = new Query(t1);
								velocity = q1.oneSolution().get("X").doubleValue();
							}
							else 
								velocity=1000000;

							tmpdist=dist(neighb, current)*velocity;
							neighb.setGdist(tmpdist + current.Gdist);
							
							neighb.setPrevious(current);
						}
					} else if (capacity == MaximumCapacity) {
						// if getF=0 but searchFront is full, i find its biggest
						// element, and if its F is
						// bigger than neighb's, then I take it out and put
						// neighb instead
						Node anotherTemp = maxf(searchFront);
						if ((anotherTemp.getF() > dist(neighb, endNode) + EUdist(neighb, current) + current.Gdist)) {
							searchFront.remove(anotherTemp);
							
							/*!!!*/
							//Hdist and velocity will be taken by prolog
							//Is there traffic? (current,neighb,id,time)
							//Is it a highway? (current,neighb,id)
							neighb.setHdist(dist(neighb, endNode));
							
							t1 = "next("+current.id+","+neighb.id+").";
							q1 = new Query(t1);
							if (q1.hasSolution()){
								t1 = "velocity("+current.id+","+neighb.id+","+time+",X).";
								q1 = new Query(t1);
								velocity = q1.oneSolution().get("X").doubleValue();
							}
							else 
								velocity=1000000;

							tmpdist=dist(neighb, current)*velocity;
							neighb.setGdist(tmpdist + current.Gdist);
							neighb.setPrevious(current);
							searchFront.add(neighb);
						}
					} else {
						// easiest case of them all

						/*!!!*/
						//Hdist and velocity will be taken by prolog
						//Is there traffic? (current,neighb,id,time)
						//Is it a highway? (current,neighb,id)
						neighb.setHdist(dist(neighb, endNode));

						t1 = "next("+current.id+","+neighb.id+").";
						q1 = new Query(t1);
						if (q1.hasSolution()){
							t1 = "velocity("+current.id+","+neighb.id+","+time+",X).";
							q1 = new Query(t1);
							velocity = q1.oneSolution().get("X").doubleValue();
						}
						else 
							velocity=1000000;

						tmpdist=dist(neighb, current)*velocity;
						neighb.setGdist(tmpdist + current.Gdist);
						
						neighb.setPrevious(current);
						searchFront.add(neighb);
						capacity++;
					}
				}
			}
			//real_size--;
			// take the node with the minimum F=G+H.
			Node newcurrent = minf(searchFront);
			capacity--;
			// set the parent of the newcurrent as current
			// current.setGdist(current.previous.Gdist+EUdist(current,current.previous));
			current = newcurrent;
		}
		
		List<Node> final_array = new ArrayList<Node>();
		//int mhkos=0;
		while (current != null) {
			//mhkos++;
			final_array.add(current);
			current = current.previous;
		}
		for (Node s : allnodes){
			s.setGdist(0);
			s.setHdist(0);
		}
		//System.out.println("Steps:"+num_of_steps+" , RealSize:"+real_size+" , Mhkos:"+mhkos);
		return final_array;
	}

	//reads the parameters for each taxi from the csv file and writes them to a prolog file
	public static List<Node> taxis_input(String file) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("taxis.pl","UTF-8");
		List<Node> alltaxis = new ArrayList<Node>();
		alltaxis.add(new Node(-2,-2,-2));
		final ParseJava something = new ParseJava();
		String line;
		try (BufferedReader bh = new BufferedReader(new FileReader(file))) {
			line = bh.readLine();// skipparw thn 1h grammh
			String[] tempInput2 = new String[10];
			while ((line = bh.readLine()) != null) {
				tempInput2 = line.split(",",-1);
				Node tempNode = new Node(something.parse_this(tempInput2[2]), Double.parseDouble(tempInput2[0]),
						Double.parseDouble(tempInput2[1]));
				String available = tempInput2[3];
				int capsmall = something.parse_this(tempInput2[4].split("-")[0]);
				int capbig = something.parse_this(tempInput2[4].split("-")[1]);
				double rating = Double.parseDouble(tempInput2[6]);
				String long_distance = tempInput2[7];
				//split the last field, at the spaces. I dont want to keep the (district) cause it messes with prolog
				String type = tempInput2[8].split("\\s+")[0];
				String[] tempInput3 = new String[3];
				tempInput3 = tempInput2[5].split("\\|");
				for (int p=0; p<tempInput3.length; p++){
					String language = tempInput3[p];
					//Prolog
					writer.println("taxi("+tempNode.x+","+tempNode.y+","+tempNode.id+","+available+","+capsmall+","+capbig+","+language+","+rating+","+long_distance+","+type+").");
					//taxi(tempNode.x,tempNode.y,tempNode.id,available,capsmall,capbig,language,rating,long_distance,type)
					//prolog ie--> taxi(23.741587,37.984125,100,yes,1,4,greek,9.2,yes,subcompact). taxi at(23.741587,37.984125) with id 100, is available, 
					//with a capacity 1-4 people,speaks greek langage, has a tating 9.2, can drive long distance and has a subcompact taxi
					//the for loop, makes another instance of the same taxi-taxi driver for all the languages he speaks
				}
				// find the taxis, which as the client probably wont be on a node
				double mindist = 9999999.99;
				Node taxiNode = new Node(-1, -1, -1);
				for (Node k : allnodes) {
					if (mindist > dist(k, tempNode)) {
						mindist = dist(k, tempNode);
						taxiNode = k;
						taxiNode.id = tempNode.id;
						
					}
				}
				alltaxis.add(taxiNode);
			}
		} catch (IOException f) {
			f.printStackTrace();
		}
		writer.close();
		return alltaxis;
	}
	
	//reads the parameters for each traffic-line from the csv file and writes them to a prolog file
	public static void traffic_input(String file) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("traffic.pl","UTF-8");
		String line;
		final ParseJava something = new ParseJava();
		try(BufferedReader bh = new BufferedReader(new FileReader(file))){ 
			String[] tempInput2 = new String[4]; 	  
			String[] tempInput4 = new String[2]; 
			String[] tempInput5 = new String[2];
			String[] tempInput6 = new String[2];
			line = bh.readLine(); 
			while ((line = bh.readLine()) != null){ 
				tempInput2 = line.split(",",-1); 
				String[] toremove = line.split("\"");
				if (toremove.length>1){
					line = line.replace(toremove[1]," ");
				}
				int tmpid = something.parse_this(tempInput2[0]);
				if (tempInput2.length <3)
					continue;
				String[] tempInput3 = tempInput2[2].split("\\|",-1); 
				//System.out.println(tempInput3[0]);
				if (tempInput3.length <= 1)
					continue;
				for(int i = 0; i < tempInput3.length; i++){ 
					tempInput4 = tempInput3[i].split("=",-1);
					tempInput5 = tempInput4[0].split("-",-1);
					tempInput6 = tempInput5[0].split(":",-1); 
					int tmpnum1 = something.parse_this(tempInput6[0])*100+something.parse_this(tempInput6[1]); 
					tempInput6 = tempInput5[1].split(":",-1); 
					int tmpnum2 = something.parse_this(tempInput6[0])*100+something.parse_this(tempInput6[1]); 
					//traffic(tmpid,tmpnum1,tmpnum2,tempInput4[1]) 
					//prolog ie->traffic(15,1200,1400,high)-->lineid=15, has high traffic at 12:00-14:00
					writer.println("traffic("+tmpid+","+tmpnum1+","+tmpnum2+","+tempInput4[1]+").");
				} 
			} 
		} 
		catch (IOException f){ 
			f.printStackTrace(); 
		}
		writer.close();
	}
	
	//reads the parameters for each line from the csv file and writes them to a prolog file
	public static void lines_input(String file) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("lines.pl","UTF-8");
		String line;
		final ParseJava something = new ParseJava();
		try (BufferedReader bh = new BufferedReader(new FileReader(file))) { 
			String[] tempInput2 = new String[5]; line = bh.readLine();
			// skipparw thn 1h grammh 
			while ((line = bh.readLine()) != null) { 
				String[] toremove = line.split("\"");
				if (toremove.length>1){
					line = line.replace(toremove[1]," ");
				}
				tempInput2= line.split(",",-1); 
				int tmpid = something.parse_this (tempInput2[0]);
				String tmphighway = tempInput2[1]; 
				if (tmphighway.length()==0)
					tmphighway="unknown";
				String tmponeway = tempInput2[3];
				if (tmponeway.length()==0)
					tmponeway="unknown";
				String tmplit = tempInput2[4];
				if (tmplit.length()==0)
					tmplit="unknown";
				writer.println("line("+tmpid+","+tmphighway+","+tmponeway+","+tmplit+").");
				//line(tmpid,tmphighway,tmponeway,tmplit)
				//prolog ie--> line(5168803,primary,yes,yes). line with id 5168803, is a primary line, oneway and is lit at night
			} 
		} 
		catch (IOException f) { 
			f.printStackTrace(); 
		}
		writer.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		String clientcsv = "client.csv";
		String nodescsv = "nodes.csv";
		String taxiscsv = "taxis.csv";
		String linescsv = "lines.csv";
		String trafficcsv = "traffic.csv";
		
		// a hashtable usefull for crossroad-adjacency list
		Hashtable<String, Node> hashtable = new Hashtable<String, Node>();
		// variable to read from the buffer, nodeHashID the hashid of each node
		String line, nodeHashID;
		// temp array to parse the data from the line into the node
		String[] tempInput = new String[5];
		// previous usefull for the adjacency list
		Node tempNode, previousNode;
		int previousid = -1;
		previousNode = new Node(-1, -1, -1);
		int hashcount = 0;
		int newcount = 0;
		final ParseJava something = new ParseJava();
		
		String t1="consult('prolog.pl')";
		Query q1 = new Query(t1);
		System.out.println(t1 + " " + (q1.hasSolution() ? "lines-succeded" :"lines-failed"));
		
		/* * * * * * * * * * * * */ 
		/* I N P U T   L I N E S */ 
		/* * * * * * * * * * * * */
		
		lines_input(linescsv);
		
		t1="consult('lines.pl')";
		q1 = new Query(t1);
		System.out.println(t1 + " " + (q1.hasSolution() ? "lines-succeded" :"lines-failed"));
		
		/* * * * * * * * */
		/* T R A F F I C */
		/* * * * * * * * */

		traffic_input(trafficcsv);
		
		t1="consult('traffic.pl')";
		q1 = new Query(t1);
		System.out.println(t1 + " " + (q1.hasSolution() ? "traffic-succeded" :"traffic-failed"));
		
		/* * * * * * * * * * * */
		/* R E A D   N O D E S */
		/* * * * * * * * * * * */
		
		int nodescnt=0;
		try (BufferedReader bh = new BufferedReader(new FileReader(nodescsv))) {
			PrintWriter writer = new PrintWriter("nodes.pl","UTF-8");
			line = bh.readLine();// skipparw thn 1h grammh
			while ((line = bh.readLine()) != null) {
				tempInput = line.split(",");
				

				nodeHashID = tempInput[0].concat(".").concat(tempInput[1]);
				if (hashtable.containsKey(nodeHashID)) {
					// an to node exei ksanaypar3ei
					tempNode = hashtable.get(nodeHashID);
					hashcount++;
					writer.println("node("+Double.parseDouble(tempInput[0])+","+Double.parseDouble(tempInput[1])+","+tempNode.id+","+something.parse_this(tempInput[2])+").");
				} else {
					// an den exei ypar3ei to dhmiourgw
					tempNode = new Node(nodescnt, Double.parseDouble(tempInput[0]),
							Double.parseDouble(tempInput[1]));
					writer.println("node("+Double.parseDouble(tempInput[0])+","+Double.parseDouble(tempInput[1])+","+nodescnt+","+something.parse_this(tempInput[2])+").");
					//node(x,y,nodeid,lineid).
					hashtable.put(nodeHashID, tempNode);
					allnodes.add(tempNode);
					newcount++;
					nodescnt++;
				}
				
				// fill adjacency list
				if (something.parse_this(tempInput[2]) == previousid) {
				//if (something.parse_this(tempInput[2]) == previousid) {
					//edw 8a bei check gia diplhs katef8ynshs
					//t1 = "next("+tempNode.id+","+previousNode.id+")";
					//q1 = new Query(t1);
					//if (q1.hasSolution())
					tempNode.adjacencyList.add(previousNode);
					//String t2 = "next("+previousNode.id+","+tempNode.id+")";
					//Query q2 = new Query(t2);
					//if (q2.hasSolution())
					previousNode.adjacencyList.add(tempNode);
				}
				previousid = something.parse_this(tempInput[2]);
				previousNode = tempNode;
				
			}
			writer.close();
		} catch (IOException f) {
			f.printStackTrace();
		}
		
		t1="consult('nodes.pl')";
		q1 = new Query(t1);
		System.out.println(t1 + " " + (q1.hasSolution() ? "nodes-succeded" :"nodes-failed"));

		/* * * * * * * * * * * * * */
		/* C L I E N T   I N P U T */
		/* * * * * * * * * * * * * */
		Client theclient = new Client();
		theclient.readClient(clientcsv);

		Node clientNode = new Node(0, theclient.clientX, theclient.clientY);
		
		// since the client is not necessaryly on a given node, find the node
		// closest to him
		double mindist = 9999999.99;
		Node startNode = new Node(-1, -1, -1);
		for (Node k : allnodes) {
			if (mindist > dist(k, clientNode)) {
				mindist = dist(k, clientNode);
				startNode = k;
			}
		}
		//same for the clients-dest
		clientNode = new Node(0, theclient.destX, theclient.destY);
		mindist = 9999999.99;
		Node destNode = new Node(-1, -1, -1);
		for (Node k : allnodes) {
			if (mindist > dist(k, clientNode)) {
				mindist = dist(k, clientNode);
				destNode = k;
			}
		}
		
		/* * * * * * * * * * * * */
		/* T A X I S   I N P U T */
		/* * * * * * * * * * * * */

		List<Node> alltaxis = new ArrayList<Node>();
		alltaxis = taxis_input(taxiscsv);
		
		t1="consult('taxis.pl')";
		q1 = new Query(t1);
		System.out.println(t1 + " " + (q1.hasSolution() ? "taxis-succeded" :"taxis-failed"));


		
		/*t1="highwayVel(A,B,0)";
		q1 = new Query(t1);
		int asda = q1.oneSolution().get("A").intValue();
		int asdb = q1.oneSolution().get("B").intValue();
		System.out.println(asda+" dasdasdsadasd  "+asdb);*/
		//double asda = q1.oneSolution().get("X").doubleValue();
		//System.out.println( "X = " + q1.oneSolution().get("X")+"=="+asda);
		
		//System.out.println(t1 + " " + (q1.hasSolution() ? "Prolog-succeded" :"Prolog-failed"));
		//System.out.println( "X = " + q1.hasSolution());
		
		
		
		/* * * * * * * * * * */
		/* A*  A N D   K M L */
		/* * * * * * * * * * */
		//JIPEngine jip = new JIPEngine();
		//jip.consultFile("prolog.pl");
		
		//JIPTermParser parser = jip.getTermParser();
		
		System.out.println("Give SearchFront maximum capacity to continue:");
		int MaximumCapacity = System.in.read();
		System.out.println("How many taxis do you want displayed? ");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		int k = scanner.nextInt();
		// startNode.previous=null;
		createKML(theclient,startNode,destNode,alltaxis,MaximumCapacity,alltaxis.size(),k);
		System.out.println("-------------------\n------ E N D ------\n-------------------\n");
		return;
	}
}
