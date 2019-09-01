This project takes as an input a real life map, the position (x,y) of a client,
the position (x,y) of taxis and finds the taxi closest to the client, as well as 
the path it should follow . After that, it calculates the path towards the client's
destination.



For the shortest path, I use a combination of A* with prolog.
For every combination of a taxi and the client, except from the absolute distance
(in meters) there are other variables too that may cause a taxi to go slower, or
NOT pick a specific path. These variables are:
	- type of road
		* if the road can be driven by a taxi
		* the ability of the taxi to run in a high speed
	- traffic



The main functionw are in the PrologExample.java file.



client.csv	: The clientss position, destination, the time of day, his language and 
		  whether he/she is carrying luggage.

nodes.csv	: A google-maps log, with every available node (x,y), the line and node id 

taxis.csv	: The taxis' position, id, availability, languages known, rating, whether
		  they are in for a long distance, and the vehicle type

lines.csv	: A google-maps log with the line id, type of road, oneway or not, lit at night, 
		  if it has lanes, maxspeed, if its railway, boundary, the access, if its natural
		  ,has barrier,tunnel,bridge, its incline, waterway, busway and toll

traffic.csv	: For various streets identified by their id, their logged (through google)
		  traffic, during the day. 

mykml.kml	: File with the series of nodes making a line on the map (path)

lines.pl	: Every line (id,type of road,,)

traffic.pl	: Traffic during the day (id,timeA,timeB,volume_of_traffic)

nodes.pl	: Every node, (id,x,y)

taxis.pl	: Every taxi-taxidriver (x,y,id,,,language,rating,long_distance,vehicle type)

prolog.pl	: Calculates a number which represents the velocity of the vehicle given custome (and try and error) 
		  numbers. For instance, with medium traffic (2.4), the given line will be 2.4 times slower. If the 
		  line is a motorway (6.4) it will be 6.4 times faster.



