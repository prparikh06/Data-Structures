package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {


	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */


	private static Person getPerson(Graph g, String name) {
		//get index from graph hashmap
		return g.members[g.map.get(name)];
	}

	private static ArrayList<Person> getFriendsList(Graph g, Person person) {

		ArrayList<Person> friends = new ArrayList<Person>();
		Friend f = person.first;
		while (f != null) {
			//get friend from graph using fnum index
			Person e = g.members[f.fnum];			
			friends.add(e);
			f = f.next;
		}

		return friends;

	}
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {

		//Make list
		ArrayList<String> path = new ArrayList<String>();
		//get Person, if exists
		if (!g.map.containsKey(p1) || !g.map.containsKey(p2)) {
			return null;
		}
		Person src = getPerson(g,p1), dest = getPerson(g,p2);
		//make queue and hash map
		Queue<Person> q = new Queue<Person>();
		HashMap<Person,Person> prev = new HashMap<Person,Person>();

		//add source to queue and hash map
		q.enqueue(src);
		prev.put(src,null);

		//BFS
		while(!q.isEmpty()) {
			Person person = q.dequeue();
			if(person == dest) {
				break;
			}

			//for each friend add to map:
			for (Person friend:getFriendsList(g,person)) {
				//if map does not contain current 
				if (!prev.containsKey(friend)) {
					q.enqueue(friend);
					//previous of friend = person
					prev.put(friend, person);
				}
			}
		}


		//go backwards and add to path
		Person person = dest;


		while (person != null && prev.get(dest) != null) {
			//add to front of array list (0th element)
			path.add(0,person.name);
			//traverse by getting parent;
			person = prev.get(person);

		}

		return path;	
	}

	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */

	private static int getIndex(Graph g, Person person) {
		return g.map.get(person.name);
	}

	private static ArrayList<String> findClique(Graph g, String school, Person source, boolean[] visited){
		//if source does not go to school, mark visited and return
		if (!school.equals(source.school)) {
			visited[getIndex(g,source)] = true;
			return new ArrayList<String>();
		}

		//create a current clique and queue for BFS
		ArrayList<String> curr = new ArrayList<String>();
		Queue<Person> q = new Queue<Person>();

		//add first source
		q.enqueue(source);

		//BFS
		while(!q.isEmpty()) {
			//get person, mark visited, add to clique
			Person person = q.dequeue();
			visited[getIndex(g,person)] = true;

			//add to array list if not already in 
			if (!curr.contains(person.name))
				curr.add(person.name);

			//for ea friend, repeat
			for (Person friend: getFriendsList(g,person) ) {
				//if not visited; if match, enqueue
				if (!visited[getIndex(g,friend)] && school.equals(friend.school)) {
					q.enqueue(friend);
				}
			}
		}


		return curr;

	}

	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {

		//return array list of groups of cliques
		ArrayList<ArrayList<String>> cliques = new ArrayList<ArrayList<String>>();

		boolean[] visited = new boolean[g.members.length];

		for (boolean b:visited) {
			//get index of b in array
			int i = 0;
			while(visited[i] != b) {
				i++;
			}

			//if not visited
			if (!b) {	
				Person person = g.members[i];

				//call BFS
				ArrayList<String> tmp = findClique(g,school,person,visited);
				if (school.equals(person.school))
					cliques.add(tmp);
			}
		}

		return cliques;

	}




	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */

 
	private static void DFS(Graph g, int num, ArrayList<String> connectors, Person person, boolean[] visited, int[] dfsnum, int[] prev) {
		int indexOfCurrent = g.map.get(person.name); 
		
		/*int num = 0;
		for (boolean b:visited) {
			if(b) {
				num++;
			}	
		}
		*/
		dfsnum[indexOfCurrent] = num;
		prev[indexOfCurrent] = num;
		visited[indexOfCurrent] = true;
		num++;
		ArrayList<Person> friends = getFriendsList(g,person);
	
		
		for (int i = 0; i < friends.size(); i++) {
			int indexOfFriend = g.map.get(friends.get(i).name);
			if (visited[indexOfFriend]) {
				prev[indexOfCurrent] = Math.min(prev[indexOfCurrent], dfsnum[indexOfFriend]);
			}
			else {
				DFS(g, num,connectors, friends.get(i),visited,dfsnum,prev);
				
				if (dfsnum[indexOfCurrent] > prev[indexOfFriend]) {
					prev[indexOfCurrent] = Math.min(prev[indexOfCurrent], prev[indexOfFriend]);
				}
				else {
					System.out.println("CHECKING:" + person.name);
					String tmp = person.name;
					//if person is first in list, need to check if connector
					if (dfsnum[indexOfCurrent] == 0) {

						//is connector
						if (i == friends.size()-1) {

							//if not in list, add
							if (!connectors.contains(tmp))
								connectors.add(tmp);
						}
		
					}


					else {

						//if not in list, add
						if (!connectors.contains(tmp))
							connectors.add(tmp);
					}
				}
			}
		}

		return;
	}


	public static ArrayList<String> connectors(Graph g) {
		String[] names = new String[g.members.length];
		for (int i = 0; i<names.length;i++) {
			names[i] = g.members[i].name;
		}
		
		System.out.println(names);
		
	
		ArrayList<String> connectors = new ArrayList<String>();
		int[] dfsnum = new int[g.members.length], prev = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];
		int num = 0;

		// DFS for ea visited
		for (int i =0; i<visited.length;i++) {
			boolean b = visited[i];

			if (!b) {
				Person person = g.members[i];

				//call DFS
				DFS(g,num,connectors,person,visited,dfsnum,prev);

			}
		}


		// check number of edges each connector has
		for (int i = 0; i < connectors.size(); i++) {
			//get Person
			Person person = getPerson(g, connectors.get(i));

			//check # of edges by checking # of friends in friends list
			int edges = getFriendsList(g, person).size();
			if (edges == 1) {
				connectors.remove(i);
			}

		}

		
		return connectors;
	}



}