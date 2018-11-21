package lse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Driver1 {




	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		LittleSearchEngine engine=new LittleSearchEngine();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter document file name => ");
		String docsFile=br.readLine();
		System.out.print("Enter noise words file name => ");
		String noiseWordsFile=br.readLine();
		engine.makeIndex(docsFile, noiseWordsFile);
		//tests getKeyWord
		//System.out.println(engine.getKeyword("can")); 
		// tests top5
		System.out.println(engine.top5search("spider","little"));
		
	}

}