import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanEncoding  {
	
	private static class TreeNode {
		private TreeNode myLeft;
		private TreeNode myRight;
		private String character;
        private int frequency;
       
		public TreeNode(TreeNode left, TreeNode right, String ch, int freq) {
			myLeft = left;
			myRight = right;
			character = ch;
			frequency = freq;
		}
		
//La méthode qui vérifie si certain treenode est une feuille c'est-à-dire un caractère simple.
		public boolean isLeaf() {
			return myLeft == null && myright == null;
		}
	}
	
	
// Hashmap l'objet qui stocke la fréquence de chaque caractère(personnage) de lettre dans le fichier(dossier).
	public static HashMap<String, Integer> frequencyCount(Iterator<String> iterator) {
		HashMap<String, Integer> binaryFrequency = new HashMap<String, Integer>();
		
		while (iterator.hasNext()) { 
			String binary = iterator.next();
			
			if (binaryFrequency.containsKey(binary))
				binaryFrequency.put(binary, binaryFrequency.get(binary) + 1);
			else
				binaryFrequency.put(binary, 1);
		}
		return binaryFrequency;
	}
	
	
// Une méthode qui montre la fréquence de chaque caractère(personnage).
//Utilisé aux fins de test.
	public static void showFrequency(HashMap<String, Integer> binaryFrequency) {
		for (Map.Entry<String, Integer> entry : binaryFrequency.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    String str = new Character((char)Integer.parseInt(key, 2)).toString();
		    
		    System.out.println(key + " : " + str + " = " + value);
		}
	}
	
	
// Méthodes les regards(apparences) pour les caractères(personnages) avec la fréquence minimale
//Et les enlève ensuite du hashmap pour créer l'arbre huffman.
	public static Map.Entry<String, Integer> delMinFrequency(HashMap<String, Integer> binaryFrequency) {		
		Integer min = Collections.min(binaryFrequency.values());
		
		for (Map.Entry<String, Integer> entry : binaryFrequency.entrySet()) {
			if (entry.getValue() == min) {
				binaryFrequency.remove(entry.getKey());
				return entry;
			}
		}
		return null;
	}
	
// Comparateur TreeNode mise en œuvre de classe
	public static Comparator<TreeNode> idComparator = new Comparator<TreeNode>(){
         
        @Override
        public int compare(TreeNode c1, TreeNode c2) {
            return (c1.frequency - c2.frequency);
        }
    };
	
//Construire le Huffman trie selon les fréquences
	public static HashMap<String, String> buildTrie(HashMap<String, Integer> binaryFrequency) {
		PriorityQueue<TreeNode> leafNode = new PriorityQueue<TreeNode>(binaryFrequency.size(), idComparator);
		
// Crée les feuilles
		while (binaryFrequency.size() > 0) {
			Map.Entry<String, Integer> leaf = delMinFrequency(binaryFrequency);
		    leafNode.add(new TreeNode(null, null, leaf.getKey(), leaf.getValue()));
		}
	
// Fusionne les feuilles
		while (leafNode.size() > 1) {	
			TreeNode left = leafNode.remove();
			TreeNode right = leafNode.remove();
			leafNode.add(new TreeNode(left, right, null, left.frequency + right.frequency));
		}
		
// Construit une table dressant la carte de caractères(personnages)
		return codewords(new HashMap<String, String>(), leafNode.peek(), "");
	}
	
	
// Méthode créant un hashmap de mots de passe codés touchant à chaque terme d'ASCII.
	public static HashMap<String, String> codewords(HashMap<String, String> cws, TreeNode x, String s) {
    	if (x.isLeaf())
    		cws.put(x.character, s);
    	else {
    		codewords(cws, x.myLeft, s + "0");
    		codewords(cws, x.myRight, s + "1");
    	}
    	return cws;
    }
    
	
// La Méthode qui crée un en-tête(une tête) au début du fichier(dossier).
	public static String formatHeader(HashMap<String, String> codewords) {
		String header = "";

		for (Map.Entry<String, String> entry : codewords.entrySet())
		    header += entry.getKey() + "," + entry.getValue() + "\n";
		return header + "\n";
	}
	
//La méthode qui convertit les cordes binaires codées dans un format de 8 morceaux chacun.
	public static String convertTo8bits(String binary) {
		String zeros = "";
		
		for (int i = 0; i != (8 - binary.length()); i++)
			zeros += "0";
		return zeros + binary;
	}
	
	
// La Méthode qui montre(affiche) l'en-tête(la tête) dans le format de fichier(dossier).
	public static void writeHeader(String header, String outputFileName) {
    	for (int i = 0; i < header.length(); i++) {
    		String section = convertTo8bits(Integer.toBinaryString(header.charAt(i)));
    		FileOutputHelper.writeBinStrToFile(section, outputFileName);
    	}
    }
    
    
// Codage chaque caractère(personnage) individuel.
	public static StringBuilder  encodeCharacters(HashMap<String, String> codes, Iterator<String> iterator) {
    	StringBuilder binChar = new StringBuilder("");
  
    	while (iterator.hasNext())
    		binChar.append(codes.get(iterator.next()));
    	return binChar.append(codes.get("EOF"));
    }
    
// La méthode qui crée le corps(l'organisme) du fichier(dossier) codé.
	public static void writeBody(StringBuilder binChar, String outputFileName) {
//Écrire les octets au fichier
		while (binChar.length() > 8) {
    		int readMax = 8 * (binChar.length() / 8);
    		FileOutputHelper.writeBinStrToFile(binChar.substring(0, readMax), outputFileName);
    		binChar.delete(0, readMax);
    	}
    	
    	if (binChar.length() != 0) {
    		for (int i = binChar.length(); i != 8; i++)
    			binChar.append("0");
    		FileOutputHelper.writeBinStrToFile(binChar.toString(), outputFileName);
    	}
    }
    
    
// La méthode qui charge le fichier(dossier) codé.
	public static Queue<String> loadFile(Iterator<String> it) {
    	Queue<String> bin = new LinkedList<String>();
    	
    	while (it.hasNext())
    		bin.add(it.next());
    	return bin;
    }
    
	public static void deleteIfExists(String path) {
	   File f = new File(path);
	   
	   try {
		   f.delete();
		   f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

// Méthode de codage globale.
	public static void encode(String target, String destination, int n) {
//Charger le fichier
		Queue<String> file = loadFile(n > 0 ? new FileFreqWordsIterator(target, n) : new FileCharIterator(target));

// Compte la fréquence de chaque caractère(personnage)
		HashMap<String, Integer> binaryFrequency = frequencyCount(file.iterator());

// Fin du code de fichier(dossier)
		binaryFrequency.put("EOF", 1);

//Construire l'arbre de Huffman
		HashMap<String, String> codes = buildTrie(binaryFrequency);

// Obtient l'en-tête(la tête) se codant
		String header = formatHeader(codes);
		
// Code tous les caractères(personnages) selon les mots de passe
		StringBuilder binChar = encodeCharacters(codes, file.iterator());

//Écrire le caractère codé
		deleteIfExists(destination);
		writeHeader(header, destination);
		writeBody(binChar, destination);
	}
	
//	   ------------------------------------------
//	   -										-
//	   -										-
//	   -			Decoding					-
//	   -										-
//	   -										-	
//	   ------------------------------------------
	
//Méthode recouvrant mots de passe dans le fichier codé.
	public static HashMap<String, String> retrieveCodewords(String header) {
		String[] lines = header.split("\n");
		HashMap<String, String> codewords = new HashMap<String, String>();
		
		for (int i = 0; i < lines.length; i++) {
			String[] codes = lines[i].split(",");

			if (codes.length > 1)
				codewords.put(codes[1], codes[0]);
		}
		return codewords;
	}
	
	
//La méthode qui rend l'en-tête codé d'un fichier
	public static String retrieveEncodedHeader(FileCharIterator it) {
		String endOfHeader = convertTo8bits(Integer.toBinaryString('\n'));
		StringBuilder header = new StringBuilder("");
		String doubleKey = "";
		String section = "";
		boolean error = true;
		
		endOfHeader += endOfHeader;
		while (it.hasNext()) {
			section = it.next();
			doubleKey += section;
			header.append((char) Integer.parseInt(section, 2));
			
			if (doubleKey.length() == 16) {
				if (doubleKey.equals(endOfHeader)) {
					error = false;
					break;
				}
				doubleKey = doubleKey.substring(8);
			}
		}

//Vérifier si l'en-tête est bien formaté
		if (error == true) {
			System.err.println("Error: bad header format");
			System.exit(0);
		}
		return header.toString();
	}
	
//La méthode qui rend le corps codé d'un fichier
	public static String retrieveEncodedBody(FileCharIterator it) {
		StringBuilder section = new StringBuilder("");

		while (it.hasNext())
			section.append(it.next());
		return section.toString();
	}
	
//La méthode qui fouille dans les mots de passe hashmap dans le nouveau fichier binaire compresed
	public static String searchForCode(String code, HashMap<String, String> codewords) {
		return codewords.containsKey(code) ? codewords.get(code) : null;
	}
	
// La Méthode qui écrit le fichier(dossier) de decoded
	public static void writeDecodedFile(StringBuilder section, String destination) {
		while (section.length() > 8) {
    		int readMax = 8 * (section.length() / 8);
    		FileOutputHelper.writeBinStrToFile(section.substring(0, readMax), destination);
    		section.delete(0, readMax);
    	}
    	
    	if (section.length() != 0) {
    		for (int x = section.length(); x != 8; x++)
    			section.append("0");
    		FileOutputHelper.writeBinStrToFile(section.toString(), destination);
    	}
	}
	
// Décode la méthode principale
	public static void decode(String target, String destination) {
		FileCharIterator it = new FileCharIterator(target);
		String header = retrieveEncodedHeader(it);
		String body = retrieveEncodedBody(it);
		HashMap<String, String> codewords = retrieveCodewords(header);		
		String bin = "";
		int i = 0;
		StringBuilder section = new StringBuilder("");
		
		while (body.length() > 1) {
			bin = body.substring(0, i);
			String code = searchForCode(bin, codewords);
			
			if (code != null) {
				if (code.equals("EOF"))
					break;
				section.append(code);
				body = body.substring(i);
				i = 0;
			}
			i++;
		}

//Écrire le fichier
		deleteIfExists(destination);
		writeDecodedFile(section, destination);	
	}

	public static void main (String [ ] args) {
		if (args.length < 3) {
				System.out.println("Usage: [encode/decode] target destination");
				return;
		}

		if (args[0].equals("encode"))
			encode(args[1], args[2], 0);
		else if (args[0].equals("encode2")) {
			if (args.length < 4) {
				System.out.println("Usage: encode2 target destination n");
				return;
			}
			encode(args[1], args[2], Integer.parseInt(args[3]));
		}
		else if (args[0].equals("decode"))
			decode(args[1], args[2]);
	}
}
