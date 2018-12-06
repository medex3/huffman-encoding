import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FileFreqWordsIterator implements Iterator<String> {
	private int n;
	private Iterator<String> bin;
	private FileCharIterator it;
	
	public FileFreqWordsIterator(String inputFileName, int _n) {
		it = new FileCharIterator(inputFileName);
		n = _n < 0 ? 0 : _n;
		bin = parseWords();
	}

// La Recherche des mots qui ont la plupart de fréquence	
// Et enlèvement d'eux du hashmap pour compression plus facile.
	public static Map.Entry<String, Integer> delMaxFrequency(HashMap<String, Integer> words) {		
		Integer max = Collections.max(words.values());
		
		for (Map.Entry<String, Integer> entry : words.entrySet()) {
			if (entry.getValue() == max) {
				words.remove(entry.getKey());
				return entry;
			}
		}
		return null;
	}
	
//Chercher les mots les Plus fréquents dans le fichier.
	private HashMap<String, Integer> mostFrequentWords(HashMap<String, Integer> words) {
		HashMap<String, Integer> frequentWords = new HashMap<String, Integer>();
		
		for (int i = 0; i < this.n && words.size() > 0; i++) {
				Map.Entry<String, Integer> word = delMaxFrequency(words);
				frequentWords.put(word.getKey(), word.getValue());
		}
		return frequentWords;
	}
	
// Chargement de la File d'attente Binaire.
	private Queue<String> loadBinQueue(String binChar, HashMap<String, Integer> fchar) {
		Queue<String> bin = new LinkedList<String>();
		String word = "";

		for (int x = 8; x <= binChar.length(); x += 8) {
			String character = binChar.substring(x - 8, x);

			if ((char) Integer.parseInt(character, 2)  != '\n' && (char) Integer.parseInt(character, 2)  != ' ')
				word += character;

			if ((char) Integer.parseInt(character, 2)  == ' ' || 
				(char) Integer.parseInt(character, 2)  == '\n' || x == binChar.length()) {
				if (!fchar.containsKey(word)) {
					for (int i = 0; i < word.length(); i += 8)
						bin.add(word.substring(0 + i, 8 + i));
				} else
					bin.add(word);
				word = "";
			}

			if ((char) Integer.parseInt(character, 2)  == '\n' || (char) Integer.parseInt(character, 2)  == ' ')
				bin.add(character);
		}
		return bin;
	}
	
	
// Analyse syntaxique de mots.
	private Iterator<String> parseWords() {
		HashMap<String, Integer> fwords = new HashMap<String, Integer>();
		StringBuilder binChar = new StringBuilder("");
		String word = new String();
		
		while (it.hasNext()) {
			String character = it.next();

			if ((char) Integer.parseInt(character, 2)  != '\n' && (char) Integer.parseInt(character, 2)  != ' ')
				word += character;
			
			if ((char) Integer.parseInt(character, 2)  == ' ' || !it.hasNext()) {
				if (word.length() >= 16) {
					if (fwords.containsKey(word))
						fwords.put(word, fwords.get(word) + 1);
					else
						fwords.put(word, 1);
				}
				word = "";
			}
			binChar.append(character);
		}
		fwords = mostFrequentWords(fwords);
		return loadBinQueue(binChar.toString(), fwords).iterator();
	}
	
	
	// Overrides. 
	@Override
	public boolean hasNext() {
		return bin.hasNext();
	}

	@Override
	public String next() {
		return bin.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
                "FileCharIterator does not delete from files.");
	}
}
