import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import friends.Friends;
import friends.Graph;

public class unitTest {

	@Test
	public void testConnectors()throws FileNotFoundException {
		Scanner sc = new Scanner(new File("exampleGraph"));
		Graph g = new Graph(sc);
		System.out.println(Friends.connectors(g));
		

		
	}
	


}
