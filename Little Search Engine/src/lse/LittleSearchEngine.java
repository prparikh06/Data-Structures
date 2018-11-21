package lse;



import java.io.*;

import java.util.*;



/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */

public class LittleSearchEngine {
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */

	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	/**

	 * The hash set of all noise words.

	 */

	HashSet<String> noiseWords;



	/**

	 * Creates the keyWordsIndex and noiseWords hash tables.

	 */

	public LittleSearchEngine() {

		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);

		noiseWords = new HashSet<String>(100,2.0f);

	}



	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */

	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
			throws FileNotFoundException {
		//initialize map and accompanying array list
		HashMap<String,Occurrence> wordMap = new HashMap<String,Occurrence>();

		//make file name
		File currFile = new File(docFile);
		//check existance
		if (!currFile.exists()) {
			throw new FileNotFoundException("file does not exist");
		}

		//make scanner to scan each token (a string) from the file
		Scanner sc = new Scanner(currFile);

		//while sc still has tokens 
		while(sc.hasNext()) {
			//get keyword
			String key = getKeyword(sc.next());

			//add to occurrences if already there
			if (wordMap.containsKey(key)) {
				wordMap.get(key).frequency++;
			}

			else { //add new
				wordMap.put(key, new Occurrence(docFile,1));
			}
		}
		sc.close();
		return wordMap;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */

	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		//for ea key in the hash map
		for(String key : kws.keySet()) {
			//create new array list for ea key
			ArrayList<Occurrence> o = new ArrayList<Occurrence>();

			//if keywordIndex already contains key, just make o equal to the existing array list
			if (keywordsIndex.containsKey(key)) {
				o = keywordsIndex.get(key);
				//add from hash map to occurrence array list
				o.add(kws.get(key));
				//update occurrence position
				insertLastOccurrence(o);
			}

			else { //create new array list entirely for key
				//add from hash map to occurrence array list
				o.add(kws.get(key));
				//update keywordIndex
				keywordsIndex.put(key, o);
			}
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */

	public String getKeyword(String word) {

		//initialize keyword and StringBuilder for easier manipulation
		String key = "";
		StringBuilder keyBuild = new StringBuilder(key);
		//trim word and replace trailing
		word = word.trim();
		word = word.replaceAll("([a-z]+)[?:!.,;]*", "$1");

		//for each letter, add to stringbuilder
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isLetter(c)) {
				keyBuild.append(c);
			}
			else //return null if not letter
				return null;
		}

		//convert back to string and make lowercase
		key = keyBuild.toString().toLowerCase();

		//if resulting keyword is empty or noise word, return null
		if (key == null || noiseWords.contains(key)) {
			return null;
		}

		return key;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */

	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

		if (occs.size() == 1) {
			return null;
		}
		//use binary search--initialize vars
		int lo = 0, hi = occs.size()-1, mid = 0;
		ArrayList<Integer> midIndexArray = new ArrayList<Integer>();
		Occurrence o = occs.get(hi);

		while(lo <= hi) {
			mid = (lo + hi)/2;
			midIndexArray.add(mid);
			//if mid frequency < last frequency, change hi
			if (occs.get(mid).frequency < o.frequency) {
				hi = mid-1;
			}
			//change lo
			else if (occs.get(mid).frequency > o.frequency) {
				lo = mid+1;
			}
			else { //they are same
				break;
			}			
		}

		//correct spot (mid) has been found, so add to correct spot
		occs.add(mid, occs.remove(occs.size()-1));



		return midIndexArray;

	}



	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */

	public void makeIndex(String docsFile, String noiseWordsFile) 
			throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */

	public ArrayList<String> top5search(String kw1, String kw2) {
		//initialize lists
		ArrayList<String> docNames = new ArrayList<String>();
		ArrayList<Occurrence> occ1 = new ArrayList<Occurrence>(), occ2 = new ArrayList<Occurrence>();
		//if neither keyword exists, return null
		if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) {
			return null;
		}
		//else initialize lists
		if(keywordsIndex.containsKey(kw1)) {
			occ1 = keywordsIndex.get(kw1); 
		}
		if(keywordsIndex.containsKey(kw2)) {
			occ2 = keywordsIndex.get(kw2);
		}



		// loop to traverse if either occurrence list exists
		int i = 0, j = 0;
		while (!(i>=occ1.size() && j>=occ2.size())) {
			//TODO REPLACE DOC WITH INSERT METHOD DIRECTLY
		
			//String doc;
			//if we've reached the end of occ1, add occ2
			if (i >= occ1.size()) {
				
				
				insertIntoDocNames(docNames,occ2.get(j).document);	
				j++;
			}

			//if we've reached the end of occ1, add occ2
			else if (j >= occ2.size()) {
				
				insertIntoDocNames(docNames,occ1.get(i).document);	
				i++;
			}
			
			//check precedence
			else if (checkPrec(occ1.get(i), occ2.get(j)) == true){
				
				insertIntoDocNames(docNames,occ1.get(i).document);
				i++;
			}
			else {
				
				insertIntoDocNames(docNames,occ2.get(j).document);
				j++;
				
				
			}
			
			
			//insertIntoDocNames(docNames,doc);
		}



		System.out.println(occ1);
		System.out.println(occ2);

		//if greater than 5, reduce to size
		if (docNames.size() > 5) {
			for (int k = docNames.size(); k > 5; k--) {
				docNames.remove(k);
			}
		}

		return docNames;	
	}

	private boolean checkPrec(Occurrence occ1, Occurrence occ2) {
		//check of first has higher precedent frequency
		if (occ1.frequency >= occ2.frequency) {
			return true;
		}
		return false;
	}

	private void insertIntoDocNames(ArrayList<String> docNames, String document) {
		if (!docNames.contains(document)) {
			docNames.add(document);
		}
	}
}

