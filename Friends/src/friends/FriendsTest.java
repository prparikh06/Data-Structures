package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FriendsTest {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("exampleGraph5"));
		Graph g = new Graph(sc);
	/*
		System.out.println("Shortest chain ex1:" + " " + Friends.shortestChain(g,"jane","aparna"));
		System.out.println("Cliques ex1:" + " " +  Friends.cliques(g,"rutgers"));
		System.out.println("Connectors in first Graph" + " " +  Friends.connectors(g));
		
		System.out.println();
		
		Scanner sc2 = new Scanner(new File("exampleGraph2"));
		Graph g2 = new Graph(sc2);
		System.out.println("Shortest chain ex2:" + " " + Friends.shortestChain(g2, "priya", "naati"));
		System.out.println("Cliques ex2:" + " " +  Friends.cliques(g2,"rutgers"));
		System.out.println("Connectors in second Graph" + " " + Friends.connectors(g2));
		*/
		
		System.out.println(Friends.shortestChain(g, "sharkeisha", "stewart"));
		System.out.println(Friends.cliques(g, "rutgers"));
		System.out.println(Friends.connectors(g));
	}

}
