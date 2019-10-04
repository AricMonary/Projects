import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

public class PA2{

	//PA #2 TODO: finds the smallest tree in a given forest, allowing for a single skip
	//Finds the smallest tree (by weight) in the supplied forest.  
	//Note that this function accepts a second optional parameter of an index to skip.  
	//Use this index to allow your function to also find the 2nd smallest tree in the 
	//forest.
	//DO NOT change the first findSmallestTree function. Only work in the second one!
	//DONE
	public static int findSmallestTree(List<HuffmanTree<Character>> forest)
	{
		return findSmallestTree(forest, -1); //find the real smallest 
	}
	public static int findSmallestTree(List<HuffmanTree<Character>> forest, int index_to_ignore) 
	{
		int weight = forest.get(0).getWeight();
		int index = 0;
		int iterator = 0;
		if(index_to_ignore == 0) {
			iterator = 1;
			index = 1;
			weight = forest.get(1).getWeight();
		}
		if(forest.size() >= 1) {
			while(iterator < forest.size())
			{
				if(forest.get(iterator).getWeight() < weight && iterator != index_to_ignore)
				{
					weight = forest.get(iterator).getWeight();
					index = iterator;
				}
				iterator++;
			}
		}
		return index; //find the smallest except the index to ignore.
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied text
	//Builds a Huffman Tree from the supplied list of strings.
	//This function implement's Huffman's Algorithm as specified in page 
	//435 of the book.	
	//DONE
	public static HuffmanTree<Character> huffmanTreeFromText(List<String> data) {
		//In order for your tree to be the same as mine, you must take care 
		//to do the following:
		//1.	When merging the two smallest subtrees, make sure to place the 
		//      smallest tree on the left side!
		//2.	Have the newly created tree take the spot of the smallest 
		//		tree in the forest(e.g. list.set(smallest_index, merged_tree) ).
		//3.	Use list.remove(second_smallest_index) to remove 
		//      the other tree from the forest.	
		//The lines below are just an example. They are NOT part of the code.
		//HuffmanTree<Character> some_tree = new HuffmanTree<Character>('a', 5);
		//HuffmanNode<Character> root = some_tree.getRoot();
		
		//note that root is a HuffmanNode instance. This type cast would only work 
		//if you are sure that root is not a leaf node.
		//Vice versa, for this assignment, you might need to force type cast a HuffmanNode
		//to a HuffmanLeafNode when you are sure that what you are getting is a HuffmanLeafNode.
		//The line below is just an example on how to do forced casting. It is NOT part of the code.
		//HuffmanInternalNode<Character> i_root = (HuffmanInternalNode<Character>)root; 
		//return null;
		
		List<HuffmanTree<Character>> forest = new ArrayList<HuffmanTree<Character>>();
		Hashtable<Character, Integer> characterWeights = new Hashtable<Character, Integer>();
		
		//reading and writing in text to a hashtable
		for(String word : data)
		{
			char[] wordInChars = word.toCharArray();
			for(char character : wordInChars)
			{
				if(characterWeights.containsKey(character))
					characterWeights.replace(character, characterWeights.get(character) + 1);
				characterWeights.putIfAbsent(character, 1);
			}
		}
		
		//writing character information from the hastable to huffman trees
		for(Object character : characterWeights.keySet().toArray())
		{
			HuffmanTree<Character> newLeaf = new HuffmanTree<Character>((char)character, (int) characterWeights.get(character));
			forest.add(newLeaf);
		}
		
		//creating the huffman tree
		while(forest.size() != 1)
		{
			int smallestTreeIndex = findSmallestTree(forest);
			int secondSmallestTreeIndex = findSmallestTree(forest, smallestTreeIndex);
			HuffmanTree<Character> newTree = new HuffmanTree<Character>(forest.get(smallestTreeIndex), forest.get(secondSmallestTreeIndex));
			forest.set(smallestTreeIndex, newTree);
			forest.remove(secondSmallestTreeIndex);
		}
		
		//the last tree in the forest should be the root of the huffman tree
		return forest.get(0);
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied encoding map
	//NOTE: I used a recursive helper function to solve this!
	public static HuffmanTree<Character> huffmanTreeFromMap(Map<Character, String> huffmanMap) {
		//Generates a Huffman Tree based on the supplied Huffman Map.Recall that a 
		//Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.
		HuffmanTree<Character> tree = new HuffmanTree<Character>(new HuffmanInternalNode<Character>(null, null));
		Character[] keySet = huffmanMap.keySet().toArray(new Character[huffmanMap.keySet().size()]);
		//goes through each character in the huffmanMap
		for(char character : keySet) {
			char[] route = huffmanMap.get(character).toCharArray();
			char booleanCharacter = ' ';
			HuffmanInternalNode<Character> current = (HuffmanInternalNode<Character>)tree.getRoot();
			//Goes through the route of the current character. 
			//Adding InternalNodes when missing and only adding LeafNodes when it is the final "move" in the route
			for(int i = 0; i < route.length; i++) {
				booleanCharacter = route[i];
				if(booleanCharacter == '0') {
					if(i == route.length - 1)
						current.setLeftChild(new HuffmanLeafNode<Character>(character, -1));
					else {
						if(current.getLeftChild() == null)
							current.setLeftChild(new HuffmanInternalNode<Character>(null, null));
						current = (HuffmanInternalNode<Character>)current.getLeftChild();
					}
				}
				else {
					if(i == route.length - 1)
						current.setRightChild(new HuffmanLeafNode<Character>(character, -1));
					else {
						if(current.getRightChild() == null)
							current.setRightChild(new HuffmanInternalNode<Character>(null, null));
						current = (HuffmanInternalNode<Character>)current.getRightChild();
					}
				}
			}
		}
		return tree;
	}

	//PA #2 TODO: Generates a Huffman encoding map from the supplied Huffman tree
	//NOTE: I used a recursive helper function to solve this!
	//DONE
	public static Map<Character, String> huffmanEncodingMapFromTree(HuffmanTree<Character> tree) {
		//Generates a Huffman Map based on the supplied Huffman Tree.  Again, recall 
		//that a Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.  
		//As such, a given code represents a pre-order traversal of that bit of the 
		//tree.  I used recursion to solve this problem.
		Map<Character, String> result = new HashMap<>();
		String path = "";
		//for empty tree
		if(tree.getRoot().isLeaf())
		{
			return result;
		}
		traversal(tree.getRoot(), path, result);
		return result;
	}
	
	//pre-order traversal for huffmanEncodingMapFromTree
	private static void traversal(HuffmanNode<Character> root, String path, Map<Character, String> result)
	{
		if(root.isLeaf())
		{
			HuffmanLeafNode<Character> leaf = (HuffmanLeafNode<Character>)root;
			result.put(leaf.getValue(), path);
			return;
		}
		
		HuffmanInternalNode<Character> next = (HuffmanInternalNode<Character>)root;
		
		path += "0";
		traversal((HuffmanNode<Character>)next.getLeftChild(), path, result);
		path = path.substring(0, path.length() - 1);
		path += "1";
		traversal((HuffmanNode<Character>)next.getRightChild(), path, result);
	}
	
	//PA #2 TODO: Writes an encoding map to file.  Needed for decompression.
	//DONE
	public static void writeEncodingMapToFile(Map<Character, String> huffmanMap, String file_name) {
		//Writes the supplied encoding map to a file.  My map file has one 
		//association per line (e.g. 'a' and 001).  Each association is separated by 
		//a sentinel value.  In my case, I went with a double pipe (||).
		try {
			BufferedWriter brw = new BufferedWriter(new FileWriter(file_name));
			for(char character : huffmanMap.keySet()) {
				brw.write(character + "||" + huffmanMap.get(character));
				brw.newLine();
			}
			brw.close();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	//PA #2 TODO: Reads an encoding map from a file.  Needed for decompression.
	//DONE
	public static Map<Character, String> readEncodingMapFromFile(String file_name) {
		//Creates a Huffman Map from the supplied file.Essentially, this is the 
		//inverse of writeEncodingMapToFile. Use String.split() function - note that
		//the split() function takes a Regular Expression as an input, not a "string" itself. 
		//To separate based on "||", the argument for the function should be: split("\\|\\|")
		
		Map<Character, String> result = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file_name));
			String line = br.readLine();
			while(line != null)
			{
				String[] twoStrings = line.split("\\|\\|");
				result.put(twoStrings[0].toCharArray()[0], twoStrings[1]);
				line = br.readLine();
			}
			br.close();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}

	//PA #2 TODO: Converts a list of bits (bool) back into readable text using the supplied Huffman map
	public static String decodeBits(List<Boolean> bits, Map<Character, String> huffmanMap) {
		//Uses the supplied Huffman Map to convert the list of bools (bits) back into text.
		//To solve this problem, I converted the Huffman Map into a Huffman Tree and used 
		//tree traversals to convert the bits back into text.
		
		//Use a StringBuilder to append results. 
		StringBuilder result = new StringBuilder();
		HuffmanTree<Character> tree = huffmanTreeFromMap(huffmanMap);
		HuffmanNode<Character> current = tree.getRoot();
		Object[] theBits = bits.toArray();
		for(int i = 0; i < theBits.length + 1; i++) {
			if(current.isLeaf()) {
				HuffmanLeafNode<Character> theLeaf = (HuffmanLeafNode<Character>)current;
				result.append(theLeaf.getValue());
				current = tree.getRoot();
				if(i == theBits.length)
				{
					return result.toString();
				}
			}
			HuffmanInternalNode<Character> intNodeTemp = (HuffmanInternalNode<Character>)current;
			if((Boolean)theBits[i] == false) {
				current = intNodeTemp.getLeftChild();
			}
			else {
				current = intNodeTemp.getRightChild();
			}
		}
		System.out.println("An error decoding bits has occured.");
		return result.toString();
	}

	//PA #2 TODO: Using the supplied Huffman map compression, converts the supplied text into a series of bits (boolean values)
	//DONE
	public static List<Boolean> toBinary(List<String> text, Map<Character, String> huffmanMap) {
		List<Boolean> result = new ArrayList<>();
		
		//reading the string from the list of strings
		for(String word : text)
		{
			//breaking the word into characters
			char[] wordInChars = word.toCharArray();
			//reading through the characters from the string
			for(char character : wordInChars)
			{
				//reading the encoder equivalency to the character from the huffman map
				for(char booleanCharacter : huffmanMap.get(character).toCharArray())
				{
					//converts the string to a boolean and adds it to the list of booleans
					if(booleanCharacter == '0')
						result.add(false);
					else
						result.add(true);
				}
			}
		}
		return result;
	}

}
