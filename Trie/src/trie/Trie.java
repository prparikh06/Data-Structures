package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	// prevent instantiation
	private Trie() { }

	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */



	private static boolean doCharsMatch(String currWord, String ptrWord) {

		return (currWord.charAt(0) == ptrWord.charAt(0));

	}

	private static int longestPrefixLength(TrieNode ptr, String[]allWords, String currWord) {

		//initialize ints
		int wordIndex = ptr.substr.wordIndex, startIndex = ptr.substr.startIndex, pIndex = 0;

		//check string at ptr
		String ptrWord = allWords[wordIndex];

		//check if first char matches
		boolean match = doCharsMatch(currWord, ptrWord);
		if (match) {

			//use loop to increment prefix index; loop until the length of the shorter word
			for(int j = 1; j < Math.min(currWord.length(), ptrWord.length()); j++) {

				//if the char matches, then increment
				if (currWord.charAt(j) == ptrWord.charAt(j)) {
					pIndex++;
				}

				//break when chars don't match
				else {
					break;
				}
			}
		}

		return pIndex;
	}

	public static TrieNode buildTrie(String[] allWords) {

		//initialize root 
		TrieNode root = new TrieNode(null,null,null);

		//insert first item in array into tree as root's child
		Indexes first= new Indexes(0, (short) 0, (short)(allWords[0].length()-1)); 
		root.firstChild = new TrieNode(first,null,null);

		//initializes ptrs
		TrieNode prev = root;
		TrieNode ptr = root.firstChild;

		//start placing the rest of the words into the trie
		for(int i = 1; i < allWords.length; i++) {

			//set ptr to firstChild, prev to ptr
			prev = ptr;
			ptr = root.firstChild;

			//get string at ith element of array; 
			String currWord = allWords[i];

			//traverse
			while(ptr != null) {

				//get length of prefix and initialize vars
				
				int pIndex = longestPrefixLength(ptr, allWords, currWord) , wordIndex = ptr.substr.wordIndex, startIndex = ptr.substr.startIndex, endIndex = ptr.substr.endIndex;
				
				//there is a common prefix, so must create two new TrieNodes, update ptr's substr value 
				TrieNode child1 = new TrieNode (new Indexes (wordIndex, (short) (pIndex+1), (short) endIndex), null, null);
				TrieNode child2 = new TrieNode (new Indexes(i, (short) (pIndex+1),(short) (currWord.length()-1)), null, null);
				ptr.substr = new Indexes (wordIndex, (short) startIndex, (short) (pIndex));

				//make them siblings
				child1.sibling = child2;

				/*update ptr based on whether ptr has a child:
				 * 1. does ptr have a child? if not, make ptr's first child child1
				 * 2. if ptr has a child, then make that child's sibling child1
				 */
				if (ptr.firstChild == null) {
					ptr.firstChild = child1;				
				}

				else if (ptr.firstChild != null && pIndex <= startIndex){ //make sure the length of prefix is less/same as ptr's endIndex 
					child1.firstChild = ptr.firstChild; 
				}

				else {
					
					//update pointers
					prev = ptr;
					ptr = ptr.firstChild;
					continue;

				}
				
				prev = ptr;
				ptr = ptr.sibling;
			}
			

			//add new temp node each iteration of outer loop
			prev.sibling = new TrieNode (new Indexes(i, (short) 0, (short) (currWord.length()-1)), null, null);
		}

		return root;
	}



	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,	String[] allWords, String prefix) {

		//initialize resulting list
		ArrayList<TrieNode> result = new ArrayList<TrieNode>();

		//traverse using for loop, recursive
		for (TrieNode ptr = root; ptr != null; ptr = ptr.sibling) {

			//if ptr is happens to be a root when called recursively, make it child 
			if(ptr.substr == null) {
				ptr = ptr.firstChild;
			}

			//get word from ptr
			String ptrWord = allWords[ptr.substr.wordIndex];

			//does ptrWord start with prefix?
			if (ptrWord.startsWith(prefix)) {


				/*there are two cases from here:
				 * 
				 * case 1: ptr is not a prefix, so just add that node to result
				 * case 2: ptr is a prefix in itself, so must recursively check again until it is not
				 * 
				 */

				//case 1
				if (ptr.firstChild == null) {
					result.add(ptr);
					continue;
				}

				//case 2
				else
					result.addAll(completionList(ptr.firstChild,allWords,prefix));
				continue;

			}

		}

		return result;
	}

	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}

	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
					.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}

		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
}