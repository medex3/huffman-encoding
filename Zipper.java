import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Zipper {

	private static int limit = 0;

	private static class FileContainer implements Comparable<FileContainer> {
		private String name;
		private int bof;
		private String header;
		private StringBuilder body;

		public FileContainer(String _name, String _header, StringBuilder _body) {
			header = _header;
			body = _body;
			bof = 0;
			name = _name;
		}

		public FileContainer(String _name, int _bof) {
			bof = _bof;
			name = _name;
			header = null;
			body = null;
		}

		public String getHeader() {
			return header;
		}

		public StringBuilder getBody() {
			return body;
		}

		public int getBof() {
			return bof;
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(FileContainer tmp) {
			return this.bof - tmp.bof;
		}
	}

	public static void showFileSizes(HashMap<String, Integer> files) {
		for (Map.Entry<String, Integer> entry : files.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			System.out.println(key + " : " + value);
		}
	}

	public static HashMap<String, Integer> listFiles(String path, HashMap<String, Integer> hdr) {
		File root = new File(path);
		File[] list = null;

		if (root.isDirectory())
			hdr.put(root.getPath(), -1);
		else {
			hdr.put(root.getPath(), 0);
			return hdr;
		}

		list = root.listFiles();
		for (File f : list) {
			if (f.isDirectory()) {
				hdr.put(f.getPath(), -1);
				listFiles(f.getPath(), hdr);
			} else
				hdr.put(f.getPath(), 0);
		}
		return hdr;
	}

	public static FileContainer encodeSingleFile(String path, int position) {
//Charger le fichier
		Queue<String> file = HuffmanEncoding
				.loadFile(new FileCharIterator(path));

// Compte la fréquence de chaque caractère(personnage)
		HashMap<String, Integer> binaryFrequency = HuffmanEncoding
				.frequencyCount(file.iterator());

//Fin du code de fichier
		binaryFrequency.put("EOF", 1);

// Construit l'arbre Huffman
		HashMap<String, String> codes = HuffmanEncoding
				.buildTrie(binaryFrequency);

// Obtient l'en-tête(la tête) se codant
		String header = HuffmanEncoding.formatHeader(codes);

// Code tous les caractères(personnages) selon les mots de passe
		StringBuilder body = HuffmanEncoding.encodeCharacters(codes,
				file.iterator());

		return new FileContainer(path, header, body);
	}

	public static int makeItMultipleOf(int value, int multiple) {
		int remainder = value % 8;

		return (remainder == 0) ? value / 8 : (value + 8 - remainder) / 8;
	}

	public static Queue<FileContainer> readFiles(HashMap<String, Integer> files) {
		Queue<FileContainer> fctn = new LinkedList<FileContainer>();
		int startPosition = 0;

		for (Map.Entry<String, Integer> entry : files.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			if (value != -1) {
//Produira l'en-tête du fichier
				FileContainer ctn = encodeSingleFile(key, startPosition);

// Commence la position(le poste) du fichier(dossier) compressé
				if (ctn != null && files.containsKey(key)) {
					files.put(key, startPosition);
					startPosition += 1 + ctn.getHeader().length()
							+ makeItMultipleOf(ctn.getBody().length(), 8);
				}
// Sauve(économise) l'en-tête(la tête) et l'itérateur pour l'utilisation postérieure
				fctn.add(ctn);
			}
		}
		return fctn;
	}

	public static String formatHeader(HashMap<String, Integer> files) {
		String header = "";

		for (Map.Entry<String, Integer> entry : files.entrySet())
			header += entry.getKey() + "," + entry.getValue() + "\n";
		return header + "\n";
	}

	public static void writeZipHeader(HashMap<String, Integer> files, String destination) {
		String header = formatHeader(files);
		HuffmanEncoding.writeHeader(header, destination);
	}

	public static void writeZipBody(Queue<FileContainer> fctn,
			String destination) {
		Iterator<FileContainer> it = fctn.iterator();

		while (it.hasNext()) {
			FileContainer ctn = it.next();
			HuffmanEncoding.writeHeader(ctn.getHeader(), destination);
			HuffmanEncoding.writeBody(ctn.getBody(), destination);

// Ajoute le retour chariot pour séparer chaque fichier(dossier)
			if (it.hasNext())
				FileOutputHelper.writeBinStrToFile("00001010", destination);
		}
	}

	public static void encodeZip(String target, String destination) {
// La Liste dépose(classe), des dossiers
		HashMap<String, Integer> files = listFiles(target,
				new HashMap<String, Integer>());
		Queue<FileContainer> fctn = readFiles(files);

		HuffmanEncoding.deleteIfExists(destination);
		writeZipHeader(files, destination);
		writeZipBody(fctn, destination);
	}

	public static Queue<FileContainer> retrieveZipHeader(String header) {
		Queue<FileContainer> ctn = new PriorityQueue<FileContainer>();
		String[] lines = header.split("\n");

		for (int i = 0; i < lines.length; i++) {
			String[] codes = lines[i].split(",");
			ctn.add(new FileContainer(codes[0], Integer.parseInt(codes[1])));
		}
		return ctn;
	}

	public static String retrieveZipBody(FileCharIterator it, int eof) {
		StringBuilder section = new StringBuilder("");

		while (it.hasNext()) {
			if (eof != -1 && limit >= eof)
				break;
			section.append(it.next());
			limit++;
		}
		return section.toString();
	}

	public static void writeDecodedZipFile(FileCharIterator it, String destination, int eof) {
		StringBuilder section = new StringBuilder("");
		HashMap<String, String> codewords;
		String header = "";
		String body = "";
		String bin = "";
		int i = 0;

		header = HuffmanEncoding.retrieveEncodedHeader(it);
//Ajouter que les octets ont écrit pour l'en-tête
		limit += header.length();
		body = retrieveZipBody(it, eof);
		codewords = HuffmanEncoding.retrieveCodewords(header);

		while (body.length() > 1) {
			bin = body.substring(0, i);
			String code = HuffmanEncoding.searchForCode(bin, codewords);

			if (code != null) {
				if (code.equals("EOF"))
					break;
				section.append(code);
				body = body.substring(i);
				i = 0;
			}
			i++;
		}
		HuffmanEncoding.writeDecodedFile(section, destination);
	}

	public static String getRootFolder(Queue<FileContainer> files) {
		Iterator<FileContainer> iterator = files.iterator();

		while (iterator.hasNext()) {
			FileContainer file = iterator.next();
			String name = file.getName();

			if (file.getBof() == -1) {
				if ((name.contains("/") && !name
						.substring(0, name.length() - 1).contains("/"))
						|| !name.contains("/")) {
					return name;
				}
			}
		}
		return "";
	}

	public static void createFolders(Queue<FileContainer> files, String rootFolder, String destination) {
		Iterator<FileContainer> iterator = files.iterator();

		while (iterator.hasNext()) {
			FileContainer file = iterator.next();
			String name = file.getName().replace(rootFolder, destination);

			if (file.getBof() == -1) {
				String[] folders = name.split("/");
				String fdr = "";

				for (int i = 0; i < folders.length; i++) {
					fdr += folders[i] + "/";
					File f = new File(fdr);

					if (!f.exists())
						f.mkdir();
				}
				iterator.remove();
			}
		}
	}

	public static void createFiles(FileCharIterator it, Queue<FileContainer> files, String rootFolder, String destination) {
		int eof = 0;
		File f;
		String name;

		while (files.size() > 0) {

			if (rootFolder.isEmpty())
				name = destination;
			else
				name = files.peek().name.replace(rootFolder, destination);

			files.remove();
			eof = files.size() > 0 ? files.peek().bof : -1;

			try {
				f = new File(name);
				f.createNewFile();
				writeDecodedZipFile(it, name, eof);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void decodeZip(String target, String destination) {
		Queue<FileContainer> files;
		FileCharIterator it;
		String rootFolder;
		String header;

// Init l'itérateur
		it = new FileCharIterator(target);
// Récupère l'en-tête(la tête) du fichier(dossier) codé et en fait l'analyse syntaxique
		header = HuffmanEncoding.retrieveEncodedHeader(it);
		files = retrieveZipHeader(header);
// Crée des dossiers si nécessaire
		rootFolder = getRootFolder(files);
		
		if (!rootFolder.isEmpty())
			createFolders(files, rootFolder, destination);
		createFiles(it, files, rootFolder, destination);
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: [zipper/unzipper] target destination");
			return;
		}

		if (args[0].equals("zipper"))
			encodeZip(args[1], args[2]);
		else if (args[0].equals("unzipper"))
			decodeZip(args[1], args[2]);
		else
			System.out.println("Usage: [zipper/unzipper] target destination");
	}
}
