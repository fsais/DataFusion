/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fusion;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.Cell;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

import fr.inrialpes.exmo.align.impl.method.StringDistAlignment;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * @author ioanna
 */
public class Fusion {
	static float homogeneityThreshold;
	static float occurenceFrequencyThreshold;
	static ArrayList<SameAsLink> links = new ArrayList<>();
	static ArrayList<ArrayList<URI>> reconciled = new ArrayList<>(); // Equivalence
	// class
	static HashMap<String, Source> sources = new HashMap<>();
	static HashMap<Integer, URI> distinctURLs = new HashMap<>();
	static HashMap<URI, Instance> instances = new HashMap<>();

	private static final JFrame guiFrame = new JFrame();
	private static final JFrame fusionFrame = new JFrame();
	//private static final JPanel container = new JPanel();
	private static JSplitPane split;
	private static boolean treeDisplayed = false;

	private static Model graph; // le graphe RDF
	private static Model fusionGraph; // le graphe RDF apres la fusion

	// fichiers
	// utilisateurs
//	private static String datasetFile1 = "dataINA/dataset_novideoperson-clean.ttl"; 
//	private static String datasetFile2 = "dataINA/dataset_novideoperson-clean.ttl";
//	private static String sameAsLinksFile = "dataINA/res.n3";
	
	private static String datasetFile1 = "PR/restaurants/restaurant1.rdf"; 
	private static String datasetFile2 = "PR/restaurants/restaurant2.rdf";
	
	private static String mappingFile = "PR/rest-mappings.rdf";
	private static String sameAsLinksFile = "PR/restaurants/restaurant1_restaurant2_goldstandard.rdf";
	
	
	// fixes
	private static String sourceInfoFile = "Fusion/sourceInfo.txt"; //syntaxe windows
	private static String logicRulesFile = "Fusion/logicRules.txt";
	private static String outputFile = "qualityAnnotation.rdf";

	public static void main(String[] args) {

//		try{
//			
//		final StringBuilder sb = new StringBuilder();
//		sb.append("LOG-");
//		SimpleDateFormat str = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
//		
//		sb.append(str.format(new Date()));
//		final File file = File.createTempFile(sb.toString(), ".log");
//		final PrintStream printStream = new PrintStream(file);
//		System.setOut(printStream);
//		//System.setErr(printStream);
		
		initGui();
		initListPanel();
		
//		}catch(Exception e){
//		System.out.println(e.toString());
//	  }
	}

	// init JFrame
	private static void initGui() {
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Parameters, RDFTree, Data fusion");
		guiFrame.setSize(1200, 800);
		guiFrame.setLocationRelativeTo(null);
		//container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		//guiFrame.add(container);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, null);
		guiFrame.add(split);
	}

	// GUI to submit thresholds and start the process
	private static void initListPanel() {

		final JPanel listPanel = new JPanel();
		listPanel.setVisible(true);

		// Buttons
		JButton setSameAsLinksFile = new JButton("Set SameAsLinks file");
		JButton setDatasetFile1 = new JButton("Set Dataset1 file");
		JButton setDatasetFile2 = new JButton("Set Dataset2 file");

		JButton sourceButton = new JButton(" Edit Source Information ");
		JButton rulesButton = new JButton("        Edit Logic Rules         ");

		JLabel homogeneityLabel = new JLabel("Threshold for Homogeneity");
		final JTextField homogeneityTextfield = new JTextField("0.10");
		JLabel frequencyLabel = new JLabel("Threshold for Occurence Frequency");
		final JTextField frequencyTextfield = new JTextField("0.01");

		homogeneityTextfield.setInputVerifier(new MyInputVerifier());
		frequencyTextfield.setInputVerifier(new MyInputVerifier());

		JButton constructINAButton = new JButton("     Construct INA graph     ");
		JButton constructButton = new JButton("Generic construct graph");
		JButton loadButton = new JButton("   Load previous graph   ");
		JButton fusionButton = new JButton("           Data fusion            ");


		JButton displayFusionButton = new JButton("     Display fused data     ");

		setSameAsLinksFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					setSameAsLinksFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		setDatasetFile1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					setDatasetFile1();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		setDatasetFile2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					setDatasetFile2();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		sourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					java.awt.Desktop.getDesktop().open(new File(sourceInfoFile));
				} catch (IOException ex) {
					Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		rulesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					java.awt.Desktop.getDesktop().open(new File(logicRulesFile));
				} catch (IOException ex) {
					Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		constructINAButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if (homogeneityTextfield.getText().length() != 0 && frequencyTextfield.getText().length() != 0) {

					Fusion.homogeneityThreshold = Float.parseFloat(homogeneityTextfield.getText());
					Fusion.occurenceFrequencyThreshold = Float.parseFloat(frequencyTextfield.getText());
					try {

						constructINAGraph();
						//genericConstructGraph("", "", "", "", "", "", "");

					} catch (FileNotFoundException ex) {
						Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
					} catch (IOException ex) {
						Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(guiFrame, "Please fill in all the fields!");
				}

			}
		});

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				loadGraph();
			}
		});

		fusionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				executeFusion();
			}
		});

		constructButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				try {
					genericConstructGraph();
				} catch (IOException | AlignmentException | URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});

		displayFusionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				displayFusedGraph();
			}
		});

		listPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		listPanel.add(setSameAsLinksFile, c);
		c.gridx = 1;
		c.gridy = 0;
		listPanel.add(setDatasetFile1, c);
		c.gridx = 2;
		c.gridy = 0;
		listPanel.add(setDatasetFile2, c);
		c.gridx = 1;
		c.gridy = 1;
		listPanel.add(sourceButton, c);
		c.gridx = 1;
		c.gridy = 2;
		listPanel.add(rulesButton, c);
		c.gridx = 1;
		c.gridy = 3;
		listPanel.add(homogeneityLabel, c);
		c.gridx = 2;
		c.gridy = 4;
		listPanel.add(homogeneityTextfield, c);
		c.gridx = 1;
		c.gridy = 4;
		listPanel.add(frequencyLabel, c);
		c.gridx = 2;
		c.gridy = 5;
		listPanel.add(frequencyTextfield, c);
		c.gridx = 1;
		c.gridy = 5;
		listPanel.add(constructINAButton, c);
		c.gridx = 1;
		c.gridy = 6;
		listPanel.add(constructButton, c);
		c.gridx = 1;
		c.gridy = 7;
		listPanel.add(loadButton, c);
		c.gridx = 1;
		c.gridy = 8;
		listPanel.add(fusionButton, c);
		c.gridx = 1;
		c.gridy = 9;
		listPanel.add(displayFusionButton, c);

		//container.add(listPanel, BorderLayout.WEST);
		split.add(listPanel);
		guiFrame.setVisible(true);
	}


	/*
	Les trois fonctions suivantes permettent de choisir un fichier dans l'arborescence
	Il y en a trois mais il existe sans doute une technique pour n'en faire qu'une avec un parametre
	*/
	private static void setSameAsLinksFile() throws IOException{

		JFileChooser dialogue = new JFileChooser(new File("."));
		File fichier;

		if (dialogue.showOpenDialog(null)== 
				JFileChooser.APPROVE_OPTION) {
			fichier = dialogue.getSelectedFile();
			sameAsLinksFile = fichier.getPath();
		}
	}

	private static void setDatasetFile1() throws IOException{

		JFileChooser dialogue = new JFileChooser(new File("."));
		File fichier;

		if (dialogue.showOpenDialog(null)== 
				JFileChooser.APPROVE_OPTION) {
			fichier = dialogue.getSelectedFile();
			datasetFile1 = fichier.getPath();
		}
	}

	private static void setDatasetFile2() throws IOException{

		JFileChooser dialogue = new JFileChooser(new File("."));
		File fichier;

		if (dialogue.showOpenDialog(null)== 
				JFileChooser.APPROVE_OPTION) {
			fichier = dialogue.getSelectedFile();
			datasetFile2 = fichier.getPath();
		}
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private static void constructINAGraph() throws FileNotFoundException, IOException, URISyntaxException {

		getSourcesInfo(new File("Fusion/sourceInfo.txt"));
		getINASameAsLinks(new File("dataINA/res.n3")); // fichier en parametre ...
		// "Fusion\\logicRules.txt"
		System.out.print("Creation des classes d'equivalences... ");
		createReconciledReferences();
		System.out.println("fait.");

		System.out.print("Chargement des donnees... "); // fichier pas en parametre ...
		parseINAData();
		System.out.println("fait.");

		System.out.print("Calcul des scores de qualites... ");
		calculateScore();
		System.out.println("fait");

		System.out.print("Ecriture avec annotations dans le fichier " + outputFile + "... ");
		writeINAData(outputFile);
		System.out.println("fait.");

		System.out.println("Fin de l'éxécution.");
	}

	private static void genericConstructGraph() throws FileNotFoundException, IOException, AlignmentException, URISyntaxException {
		//pas besoin des ontologies...
		// String sourceInfoFile, String logicRulesFile, String dataFile1, String dataFile2, String sameAsLinksFile

		//getSameAsLinks(new File(mappingFile)); 
		getSourcesInfo(new File(sourceInfoFile));
	
		getSameAsLinks(new File(sameAsLinksFile)); 
		//getINASameAsLinks(new File(sameAsLinksFile)); // fichier en parametre ...

		// "Fusion\\logicRules.txt"
		System.out.print("Creation des classes d'equivalences... ");
		createReconciledReferences();
		System.out.println("fait.");
		
		
		System.out.print("Creation des classes d'equivalences... ");
		createReconciledReferences(); 
		System.out.println("fait.");

		// rewrite - mapping (fait de facon externe)

		System.out.print("Chargement des donnees... ");
		parseData(datasetFile1, datasetFile2);
		System.out.println("fait.");

		System.out.print("Calcul des scores de qualites... ");
		calculateScore();
		System.out.println("fait");

		System.out.print("Ecriture avec annotations dans le fichier " + outputFile + "... ");
		writeData(outputFile);
		System.out.println("fait.");

		System.out.println("Fin de l'éxécution.");
	}

	// data fusion avec la requete
	private static void executeFusion() {
		System.out.print("Extraction de resultats significatifs... ");
		try {
			Extraction extract = new Extraction(graph);
			// extract.testStatement(); ////TEST//////
			fusionGraph = extract.fusionModel();
			System.out.println("fait.");

		} catch (Exception e) {
			System.out.println("Erreur: " + e);
		}
		System.out.println("Fin de l'exécution.");
	}

	// open outputFile and load it into a Model
	private static void loadGraph() {
		System.out.print("Chargement du graphe... ");
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(outputFile);
			graph = model;
			System.out.println("fait.");
		} catch (Exception e) {
			System.out.println("Erreur: " + e);
		}
		System.out.println("Fin de l'exécution.");

		//////////////// TEST// Pour RDF validator
		/*
		 * Model test = ModelFactory.createDefaultModel();
		 * test.read("fusionResult.rdf"); Extraction ex = new Extraction(test);
		 * String query = "CONSTRUCT {<http://locahost/rdf/saSet1> ?prop ?val ."
		 * + "		   ?val ?p ?v}" + //<http://localhost/rdf/hasValue> ou ?p
		 * "WHERE {<http://localhost/rdf/saSet1> ?prop ?val ." +
		 * " 	   ?val ?p ?v}"; //pareil ex.constructQuery(query,
		 * "queryResult.rdf");
		 */
		///////// FIN TEST//
	}

	private static void displayFusedGraph() {
		if(! treeDisplayed){
			System.out.print("Affichage des resultats... ");
			try {
				RDFTree displayer = new RDFTree(fusionGraph, graph);
				JScrollPane treeDisplay = displayer.display();
				//container.add(treeDisplay, BorderLayout.CENTER);
				split.add(treeDisplay);
				guiFrame.setVisible(true);
				treeDisplayed = true;
				System.out.println("fait.");
			} catch (Exception e) {
				System.out.println("Erreur: " + e);
			}
			System.out.println("Fin de l'exécution.");
		}
		else
			System.out.println("Les donnees sont deja affichees.");
	}

	// parse sourceInfo.txt file and store freshness and reliability of sources
	private static void getSourcesInfo(File file) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			String name;
			float reliability;
			float freshness;
			while (line != null) {
				if (line.contains("source")) {
					name = line.substring(line.indexOf(":") + 2);
					line = br.readLine();
					reliability = Float.parseFloat(line.substring(line.indexOf("reliability") + 12));
					line = br.readLine();
					freshness = Float.parseFloat(line.substring(line.indexOf("freshness") + 10));
					Source source = new Source(name, reliability, freshness);
					sources.put(source.getName(), source);
				}
				line = br.readLine();
			}
		}
	}

	//parse logicRules.txt file and store compaptibility rules
	private static void getLogicRules(File file) throws FileNotFoundException, IOException{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			String[] parts;
			while (line != null) {
				parts = line.split(" ");
				//stocker parts (prop1, comparateur, prop2) dans une structure de donnée static adéquate
				line = br.readLine();
			}
		}
	}

	// parse sameAs links for INA dataset. Create SameAsLink objects and
	// distinctURLs table

	private static void getINASameAsLinks(File file) throws FileNotFoundException, IOException, URISyntaxException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				if (line.contains("owl:sameAs")) {
					sb.append(line);
					sb.append(System.lineSeparator());
				}
				line = br.readLine();

			}
			String linksString = sb.toString();
			String[] linksArray = linksString.split("\n");

			for (int i = 0; i < linksArray.length; i++) {

				String value1 = linksArray[i].substring(0, linksArray[i].indexOf(" owl:sameAs"));
				String value2 = linksArray[i].substring(linksArray[i].lastIndexOf("owl:sameAs ") + 11, linksArray[i].length() - 1);

				value1 = StringUtils.removeStart(value1, "<");
				value2 = StringUtils.removeStart(value2, "<");
				value1 = StringUtils.removeStart(value1, "http://www.ina.fr/thesaurus/pp/");
				value2 = StringUtils.removeStart(value2, "http://www.ina.fr/thesaurus/pp/");
				value1 = StringUtils.removeStart(value1, "http://fr.dbpedia.org/resource/"); //
				value2 = StringUtils.removeStart(value2, "http://fr.dbpedia.org/resource/"); //
				value1 = StringUtils.removeEnd(value1, ".");
				value2 = StringUtils.removeEnd(value2, ".");
				value1 = StringUtils.removeEnd(value1, ">");
				value2 = StringUtils.removeEnd(value2, ">");

				if (!distinctURLs.containsKey(value1.hashCode()))
					distinctURLs.put(value1.hashCode(), new URI(value1));
				if (!distinctURLs.containsKey(value2.hashCode()))
					distinctURLs.put(value2.hashCode(), new URI(value2));

				links.add(new SameAsLink(distinctURLs.get(value1.hashCode()), distinctURLs.get(value2.hashCode()))); // adding same as links
			}

			for (Integer name : distinctURLs.keySet()) {
				Instance instance = new Instance(distinctURLs.get(name), null);
				if (distinctURLs.get(name).toString().contains("dbpedia"))
					instance.setSource(sources.get("fr.dbpedia.org"));

				else
					instance.setSource(sources.get("www.ina.fr"));
				instances.put(instance.getUri(), instance);
			}
		}
	}

	// ces trois methodes seront supprimés lorsque l'on utilisera des URI plutot que des Strings ?

	public static String getLocalFromUri(final String uri) throws URISyntaxException{
		// par exemple http://localhost/rdf/val1234 -> val1234
		return uri.replaceFirst(".*/([^/?]+).*", "$1");
		//URI u = new URI(uri);
		//return u.getPath();
	}

	public static String getFragmentFromUri(final String uri) throws URISyntaxException{
		// par exemple http://localhost/rdf/val1234 -> localhost
		URI u = new URI(uri);
		return u.getFragment();
	}

	public static String getHostFromUri(final String uri) throws URISyntaxException{
		// par exemple http://localhost/rdf/val1234 -> localhost
		URI u = new URI(uri);
		return u.getHost();
	}

	private static void getMappings(File file) throws FileNotFoundException, IOException, AlignmentException {


		AlignmentParser a = new AlignmentParser();
		Alignment o = a.parse(file.toURI());
		for(Cell c : o){

			java.net.URI value1 = c.getObject1AsURI(); // getLocalFromUri(
			java.net.URI value2 = c.getObject2AsURI();
			
		//	System.out.println("..."+value1);
		//	System.out.println("---"+value2);
			
			// keeps only the end of URI ?

//			if (!distinctURLs.containsKey(value1.hashCode()))
//				distinctURLs.put(value1.hashCode(), value1);
//			if (!distinctURLs.containsKey(value2.hashCode()))
//				distinctURLs.put(value2.hashCode(), value2);
//
//			links.add(new SameAsLink(distinctURLs.get(value1.hashCode()), distinctURLs.get(value2.hashCode()))); // adding same as links
		
		}

		// ADD SOURCES
		//detecter sources (et demander infos a l'utilisateur ?)

//		for (Integer intName : distinctURLs.keySet()) {
//			URI name = distinctURLs.get(intName);
//			Source source = sources.get(name.getHost()); // detecter la source
//			Instance instance = new Instance(name, source);
//
//			instances.put(instance.getUri(), instance);
//		}

	}

	
	// parse sameAs links for dataset. Create SameAsLink objects and distinctURLs table
	private static void getSameAsLinks(File file) throws FileNotFoundException, IOException, AlignmentException {


		AlignmentParser a = new AlignmentParser();
		Alignment o = a.parse(file.toURI());
		for(Cell c : o){

			java.net.URI value1 = c.getObject1AsURI(); // getLocalFromUri(
			java.net.URI value2 = c.getObject2AsURI();
			// keeps only the end of URI ?

			if (!distinctURLs.containsKey(value1.hashCode()))
				distinctURLs.put(value1.hashCode(), value1);
			if (!distinctURLs.containsKey(value2.hashCode()))
				distinctURLs.put(value2.hashCode(), value2);

			links.add(new SameAsLink(distinctURLs.get(value1.hashCode()), distinctURLs.get(value2.hashCode()))); // adding same as links
		}

		// ADD SOURCES
		//detecter sources (et demander infos a l'utilisateur ?)

		for (Integer intName : distinctURLs.keySet()) {
			URI name = distinctURLs.get(intName);
			Source source = sources.get(name.getHost()); // detecter la source
			Instance instance = new Instance(name, source);

			instances.put(instance.getUri(), instance);
		}

	}

	// create a new Array of reconciled references
	private static void newReconciledReference(URI link1, URI link2) {
		ArrayList<URI> al = new ArrayList<>();
		al.add(link1);
		al.add(link2);
		reconciled.add(al);
	}

	// create an array of arrays of reconciled references
	private static void createReconciledReferences() {
		for (int i = 0; i < links.size(); i++) {
			URI link1 = links.get(i).getValue1();
			URI link2 = links.get(i).getValue2();

			if (reconciled.isEmpty()) {
				newReconciledReference(link1, link2);
			} else {
				boolean first = false;
				boolean second = false;
				for (int k = 0; k < reconciled.size(); k++) {

					if (reconciled.get(k).contains(link1)) {
						first = true;
					}
					if (reconciled.get(k).contains(link2)) {
						second = true;
					}
					if (first && !second) {
						reconciled.get(k).add(link2);
						break;
					} else if (!first && second) {
						reconciled.get(k).add(link1);
						break;
					}
				}
				// if neither link exists, create a new reconciled reference
				// array
				if (!first && !second) {
					newReconciledReference(link1, link2);
				}
			}
		}
	}

	// parse INA dataset, create all objects for properties, instances, triples
	// and values

	private static void parseINAData() throws FileNotFoundException, IOException {
		try (Scanner scanner = new Scanner(new File("dataINA/dataset_novideoperson.ttl"))
				.useDelimiter(Pattern.compile("^\\s*$", Pattern.MULTILINE))) {
			// On parcourt le fichier avec les donnees
			while (scanner.hasNext()) {
				String token = scanner.next();
				String propName = "";
				String propValue = "";
				token = new String(token.trim().getBytes(), Charset.forName("UTF-8"));

				// for (Integer name: distinctURLs.keySet()){
				// String value = new String(distinctURLs.get(name).getBytes(),
				// Charset.forName("UTF-8"));
				for (URI name : instances.keySet()) {
					Instance inst = instances.get(name); // Moins optimise que
					// de parcourir les
					// distinctURLs ?
					URI value = inst.getUri();
					if (token.contains(value.toString())) { // le scanner lit l'URL
						try (Scanner scannerToken = new Scanner(token)) {
							while (scannerToken.hasNextLine()) {
								String line = scannerToken.nextLine();
								line = line.trim();
								// properties start with "notice:"
								if (line.startsWith("notice")) {
									if (line.contains(" ")) {
										propName = line.substring(7, line.indexOf(" "));
										propValue = line.substring(line.indexOf(" ")).trim().replaceAll("\\s+", " ");

										// Instance inst = instances.get(value);

										// add property in map if it doesn't
										// exist
										Property prop;
										if (!inst.containsProperty(propName)) {
											prop = new Property(propName);
											inst.addToProperties(prop);
										} else
											prop = inst.getProperty(propName);

										// get datatype values
										if (propValue.startsWith("\"")) {
											propValue = StringUtils.removeStart(propValue, "\"");
											propValue = StringUtils.substringBeforeLast(propValue, "\"");
											Value val = new Value(propValue);
											if (value.toString().contains("dbpedia")) {
												val.setSource(sources.get("fr.dbpedia.org"));
											} else {
												val.setSource(sources.get("www.ina.fr"));
											}

											if (!prop.containsValue(propValue)) //
												prop.addToValues(val);

										}
									}
								
								//	System.out.println("=== "+line);
								}// LineStarts(notice)
							 // System.out.println("*** "+line);
							}
						}
					}
				}
			}
		}
	}
//
//	// parse dataset, create all objects for properties, instances, and values
//		private static void rewriteDataFileWithMappings(String datasetFile, String mappings) throws FileNotFoundException, IOException, URISyntaxException {
//			// un fichier suffit, chaque fichier en plus sera ajoute au model avec un Union
//			
//			
//			Model model = ModelFactory.createDefaultModel();
//			try{
//				
//				Model model1 = ModelFactory.createDefaultModel();
//				model1.read(datasetFile);
//				
//				MappingSet ms = MappingSet.createModelMappingSet();
//				
//				
//			// read RDF and fill data (instance, property, value ...) 
//
//			for (URI name : instances.keySet()) {
//				Instance inst = instances.get(name);
//			//	System.out.println("++++ "+name);
//				
//				//String aModif = "http://www.okkam.org/oaie/"; // temporaire
//				Resource o = model.getResource(name.toString());
//				StmtIterator iter = model.listStatements(o, null, (RDFNode) null);
//				
//				//System.out.println("*** "+iter);
//				while(iter.hasNext() ==true){
//						
//					Statement stmt = iter.nextStatement();
//					// System.out.println("=== "+stmt);
//					//System.out.println("*** "+stmt);
//					com.hp.hpl.jena.rdf.model.Property p = stmt.getPredicate();
//
//					String propName = new URI(p.getURI()).getFragment();
//
//					Property prop;
//					
//					if (!inst.containsProperty(propName)) {
//						System.out.println(".... "+propName);
//					
//						prop = new Property(propName);
//						inst.addToProperties(prop);
//					} else {
//						prop = inst.getProperty(propName);
//						inst.addToProperties(prop);
//					}
//
//					String propValue;
//
//					RDFNode node = stmt.getObject();
//					if(node.isLiteral()){ // Literal
//						Literal nodeL = (Literal) node;
//						propValue = nodeL.getString();
//					}
//					else{ // Resource
//						Resource nodeR = (Resource) node;
//						propValue = nodeR.getURI();
//					}
//
//					Value val = new Value(propValue);
//					val.setSource(inst.getSource()); 
//
//					if (!prop.containsValue(propValue)) 
//						prop.addToValues(val);
//
//				}
//			}
//			}catch(Exception e){
//				System.out.println(e.toString());
//			}
//		}
//
//	
	
	// parse dataset, create all objects for properties, instances, and values
	private static void parseData(String datasetFile1, String datasetFile2) throws FileNotFoundException, IOException, URISyntaxException {
		// un fichier suffit, chaque fichier en plus sera ajoute au model avec un Union
		
		
		Model model = ModelFactory.createDefaultModel();
		try{

		if(!datasetFile1.equals(datasetFile2)){// two different files 
			
			Model model1 = ModelFactory.createDefaultModel();
			model1.read(datasetFile1);
			Model model2 = ModelFactory.createDefaultModel();
			model2.read(datasetFile2);
		
			model = ModelFactory.createUnion(model1, model2); // union des deux models
		
		}else{//one file
			
			model.read(datasetFile1);
		}

		// read RDF and fill data (instance, property, value ...) 

		for (URI name : instances.keySet()) {
			Instance inst = instances.get(name);
		//	System.out.println("++++ "+name);
			
			//String aModif = "http://www.okkam.org/oaie/"; // temporaire
			Resource o = model.getResource(name.toString());
			StmtIterator iter = model.listStatements(o, null, (RDFNode) null);
			
			//System.out.println("*** "+iter);
			while(iter.hasNext() ==true){
					
				Statement stmt = iter.nextStatement();
				// System.out.println("=== "+stmt);
				//System.out.println("*** "+stmt);
				com.hp.hpl.jena.rdf.model.Property p = stmt.getPredicate();

				String propName = new URI(p.getURI()).getFragment();

				Property prop;
				
				if (!inst.containsProperty(propName)) {
					System.out.println(".... "+propName);
				
					prop = new Property(propName);
					inst.addToProperties(prop);
				} else {
					prop = inst.getProperty(propName);
					inst.addToProperties(prop);
				}

				String propValue;

				RDFNode node = stmt.getObject();
				if(node.isLiteral()){ // Literal
					Literal nodeL = (Literal) node;
					propValue = nodeL.getString();
				}
				else{ // Resource
					Resource nodeR = (Resource) node;
					propValue = nodeR.getURI();
				}

				Value val = new Value(propValue);
				val.setSource(inst.getSource()); 

				if (!prop.containsValue(propValue)) 
					prop.addToValues(val);

			}
		}
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}

	// calculate all quality criteria for a value
	private static void calculateScore() {

		for (URI name : instances.keySet()) {
			Instance inst = instances.get(name);
			ArrayList<URI> rec = new ArrayList<>();
			
			for (int k = 0; k < reconciled.size(); k++) {
				if (reconciled.get(k).contains(inst.getUri())) {
					rec = reconciled.get(k);
				}
			}
		
			

			ArrayList<Instance> references = new ArrayList<>();
			for (int g = 0; g < rec.size(); g++) {
				references.add(instances.get(rec.get(g)));
			}

			
			for (int i = 0; i < inst.getProperties().size(); i++) {
				
				Property prop = inst.getProperties().get(i);
				
				ArrayList<Value> homog = new ArrayList<>();
				
				for (int a = 0; a < references.size(); a++) {
					
					for (int b = 0; b < references.get(a).getProperties().size(); b++) {
						
						if (!(references.get(a).getProperties().isEmpty())){
							
						//System.out.println("----"+references.get(a).getProperties());//.get(b).getName());
						
						if (references.get(a).getProperties().get(b).getName().equals(prop.getName())) {
							homog.addAll(references.get(a).getProperties().get(b).getValues());
						}
					 }
					}
					//System.out.println("---- homog: "+homog);
				}
//				

//				

				// discover implausible values and calculate quality score for
				// plausible values
				for (int j = 0; j < prop.getValues().size(); j++) {
					Value value = prop.getValues().get(j);
					String valueStr = value.getValue();

					Integer freq = 0;

					// calculate homogeneity
					for (int o = 0; o < homog.size(); o++) {
						if (homog.get(o).getValue().equals(valueStr)) {
							++freq;
						}
					}
					
//					
					Integer mpBonus = 0;
					// calculate morePrecise
					for (int o = 0; o < homog.size(); o++) {
						boolean ret= isMorePrecise(value,(Value)homog.get(o)); 
						if (ret == true)
						++mpBonus;
						
					}
					
				
				//System.out.println(" *** : "+value.getValue()+ " MP "+value.dispalyIsMorePreciseThan());
			//   System.out.println(" *** : "+value.dispalyIsMorePreciseThanStr());
				
				
//					
//					try{
//					// calculate isSynonym
//					for (int o = 0; o < homog.size(); o++) {
//						if((isSynonym(value,(Value)homog.get(o))==true)); 							
//					}
//					}catch(Exception e){System.out.println(e.toString());}
//					
//					System.out.println(" +++ : isSyn "+value.dispalyIsSynonym());
//					
					
					
					Float homogeneity = ((float) freq / homog.size());
					value.setHomogeneity(homogeneity);

					// calculate occurrence frequency
					freq = 0;
					for (int o = 0; o < prop.getValues().size(); o++) {
						if (prop.getValues().get(o).getValue().equals(valueStr)) {
							++freq;
						}
					}
					Float occurrenceFrequency = ((float) freq / prop.getValues().size());
					value.setOccurrenceFrequency(occurrenceFrequency);

					if (/* violatesRules(value) || */ (value.getHomogeneity() < Fusion.homogeneityThreshold || value.getOccurrenceFrequency() < Fusion.occurenceFrequencyThreshold)) {
						value.setImplausible(true);
					} else // apparemment inutile mais pourquoi ?
						value.setImplausible(false);

					if (!value.getImplausible()) { // si plausible
						/*value.setQualityScore((value.getHomogeneity() + value.getOccurrenceFrequency()
						) / 2); // test sans source
						value.setQualityValue(Fusion.getQualityValue(value.getQualityScore()));*/

						value.setQualityScore((value.getHomogeneity() + value.getOccurrenceFrequency()
						+ value.getSource().getFreshness() + value.getSource().getReliability()) / 4);
						value.setQualityValue(Fusion.getQualityValue(value.getQualityScore())); 
					}
					
				}//Exploration of the set of values a given P.
			}//Exploration of the set of properties
		}
	}

	private static String getQualityValue(float qualityScore) {
		if (qualityScore <= 0.34)
			return "poor";
		else if (qualityScore <= 0.67)
			return "average";
		else
			return "excellent";
	}

	/*
	 * FOR each val in attribute.listOfValues: IF value.contains(val) OR
	 * knowledgeBaseAbout(value, val): value.morePreciseThan.add(val)
	 * 
	 * IF value.isSynonym(val): value.synonyms.add(val)
	 * 
	 * IF value.violatesExpertRule(): value.incompatibilities.add(Rule,
	 * property) }
	 * 
	 * //check expert rules violations -- not implemented private static boolean
	 * violatesRules(Value value){ value.setViolatedRules(""); return true;
	 * 
	 * }
	 */

	// check if values are more precise according to specific ontologies -- not
	// implemented
	private static boolean checkKnowledgeBase(String val1, String val2) {
		return false;
	}
	
	// define if a value is more precise than another
	private static boolean isMorePrecise(Value val1, Value val2) {
	
		String v11 = val1.getValue();
		String v21 = val2.getValue();
		String v1 = Utils.removePunctuations(v11).toLowerCase(); 
		
		String v2 = Utils.removePunctuations(v21).toLowerCase(); 
		
				
		//if ((v1.contains(v2)))// && (!v1.equals(v2)))
			// || checkKnowledgeBase(val1.getValue(), val2.getValue()))
		if (!(v1.equals(v2)) && (v1.indexOf(v2)>-1)){
			//System.out.println("("+v1+","+v2+")");
			if(!(val1.getIsMorePreciseThanStr()).contains(v21))
				//val1.addToMorePrecise(val2);
				val1.addToMorePreciseStr(v21);
			 return true;
			}
		else 
			return false;
		

//		if (val2.getValue().contains(val1.getValue()) || checkKnowledgeBase(val2.getValue(), val1.getValue()))
//			// val1.addToMorePrecise(val2);
//			return val2;
//
//		if (val1.getValue().length() > val2.getValue().length()) // comparer la longueur
//			return val1;
//		else
//			return val2;

	}

	// define if a value is more precise than another
//	private static Value isMorePrecise(Value val1, Value val2) {
//		
//		if (val1.getValue().contains(val2.getValue()) || checkKnowledgeBase(val1.getValue(), val2.getValue()))
//			// val1.addToMorePrecise(val2);
//			return val1;
//
//		if (val2.getValue().contains(val1.getValue()) || checkKnowledgeBase(val2.getValue(), val1.getValue()))
//			// val1.addToMorePrecise(val2);
//			return val2;
//
//		if (val1.getValue().length() > val2.getValue().length()) // comparer la longueur
//			return val1;
//		else
//			return val2;
//
//	}

	// define if two values are synonyms
	private static boolean isSynonym(Value val1, Value val2) throws IOException {
		boolean isSyn =false;
		String thesaurusUrl = "http://words.bighugelabs.com/api/2/92eae7f933f0f63404b3438ca46861e5/" + val1.getValue()
		+ "/xml";

		Document doc = Jsoup.connect(thesaurusUrl).get();
		Elements synonyms = doc.select("w");

		String syn = synonyms.html();
		String[] synonymsArray = syn.split("\n");
		ArrayList<String> synonymsList = new ArrayList<String>(Arrays.asList(synonymsArray));
		if (synonymsList.contains(val2.getValue())) {
			val1.addToSynonyms(val2);
			val2.addToSynonyms(val1);
			isSyn = true;

		}
		return isSyn;
	}


	private static void writeINAData(String outputFile) {

		// URI
		String ns = "http://localhost/rdf/";
		String saSetURI = ns + "saSet";
		String valURI = ns + "val";
		int lURI = 0; // pour ne pas remettre a zero les compteurs a chaque
		// parcours (URI pour les values)

		try {
			// create an empty model
			Model model = ModelFactory.createDefaultModel();

			Resource saSets = model.createResource(ns + "saSets"); // racine qui
			// relit les
			// classes
			// d'equivalences
			// Ainsi on a un graphe plutot que plusieurs composantes connexes

			for (int i = 0; i < reconciled.size(); i++) { // on parcourt les
				// classes
				// d'equivalence
				String aSASetURI = new String(saSetURI + i);
				Resource aSaSet = model.createResource(aSASetURI);
				HashMap<String, Value> bestValues = new HashMap<String, Value>(); // ici,
				// le
				// choix
				// est
				// fait
				// de
				// ne
				// garder
				// qu'UNE
				// valeur
				// par
				// propriete

				saSets.addProperty(model.getProperty(ns + "hasSameAsSet"), aSaSet);

				for (int j = 0; j < reconciled.get(i).size(); j++) { // on
					// parcourt
					// les
					// instances
					// au
					// sein
					// d'une
					// classe
					Instance inst = instances.get(reconciled.get(i).get(j));

					for (int k = 0; k < inst.getProperties().size(); k++) { // on
						// parcout
						// les
						// proprietes
						// d'une
						// instance
						Property prop = inst.getProperties().get(k);
						// il faut que l'unicite des proprietes soit deja
						// etablie

						for (int l = 0; l < prop.getValues().size(); l++) { // on
							// parcourt
							// les
							// valeurs
							// d'une
							// propriete
							// peut etre a enlever en ameliorant la structure de
							// donnees ?
							String aValURI = valURI + lURI;
							lURI++;
							Value value = prop.getValues().get(l);
							value.setUri(aValURI);
							Resource val = model.createResource(aValURI);
							aSaSet.addProperty(model.getProperty(ns + prop.getName()), val); // PAF

							// annotations
							val.addProperty(model.getProperty(ns + "hasValue"), value.getValue()); // la
							// valeur
							val.addProperty(model.getProperty(ns + "isFrom"), inst.getUri().toString()); // l'instance
							// de
							// provenance
							// (chaque
							// instance
							// possede
							// une
							// URL
							// unique)
							val.addProperty(model.getProperty(ns + "isImplausible"),
									String.valueOf(value.getImplausible()));
							val.addProperty(model.getProperty(ns + "hasOccurenceFrequency"),
									String.valueOf(value.getOccurrenceFrequency()));
							val.addProperty(model.getProperty(ns + "hasHomogeneity"),
									String.valueOf(value.getHomogeneity()));

							if (!value.getImplausible()) { // si la valeur est
								// plausible
								val.addProperty(model.getProperty(ns + "hasReliability"),
										String.valueOf(value.getSource().getReliability()));
								val.addProperty(model.getProperty(ns + "hasFreshness"),
										String.valueOf(value.getSource().getFreshness()));
								val.addProperty(model.getProperty(ns + "hasQualityScore"),
										String.valueOf(value.getQualityScore()));
								val.addProperty(model.getProperty(ns + "hasQualityValue"), value.getQualityValue());
								Value oldValue = bestValues.get(prop.getName()); // pas
								// reussi
								// a
								// faire
								// plus
								// propre...
								if (oldValue == null)
									bestValues.put(prop.getName(), value);
								else {
									if (value.getQualityScore() > oldValue.getQualityScore())
										bestValues.put(prop.getName(), value);
								}
							}
						}
					}
				}
				for (Entry<String, Value> e : bestValues.entrySet()) { // annotations
					// meilleurs
					// valeurs
					model.getResource(e.getValue().getUri()).addProperty(model.getProperty(ns + "hasMaximumScore"),
							"true"); // vrai si la valeur a le meilleur score
					// pour sa propriete et sa classe
					// d'equivalence (la valeur sera gardee
					// lors de la fusion)
					// aSaSet.addProperty(model.getProperty(ns + e.getKey()),
					// model.getResource(e.getValue().getUri())); // real fusion
				}
			}

			// Ecriture dans le fichier
			FileOutputStream fout = new FileOutputStream(outputFile);
			model.write(fout);

			graph = model; // a voir
		} catch (Exception e) {
			System.out.println("Failed: " + e);
		}
	}


	private static void writeData(String outputFile) {

		// URI
		String ns = "http://localhost/rdf/"; // /!\ Structure à modifier: on se sert de ns pour combler l'information perdu sur l'URI. L'utilisation de Resource ou d'URI plutot que des Strings permetterait de ne jamais perde l'information !
		String saSetURI = ns + "saSet";
		String valURI = ns + "val";
		int lURI = 0; // pour ne pas remettre a zero les compteurs a chaque
		// parcours (URI pour les values)

		try {
			// create an empty model
			Model model = ModelFactory.createDefaultModel();

			Resource saSets = model.createResource(ns + "saSets"); // racine qui relit les classes d'equivalences
			// Ainsi on a un graphe plutot que plusieurs composantes connexes

			for (int i = 0; i < reconciled.size(); i++) { // on parcourt les classes d'equivalence
				String aSASetURI = new String(saSetURI + i);
				Resource aSaSet = model.createResource(aSASetURI);
				HashMap<String, Value> bestValues = new HashMap<String, Value>(); // ici, le choix est fait de ne garder qu'UNE valeur par propriete

				saSets.addProperty(model.getProperty(ns + "hasSameAsSet"), aSaSet);

				for (int j = 0; j < reconciled.get(i).size(); j++) { // on parcourt les instances au sein d'une classe

					Instance inst = instances.get(reconciled.get(i).get(j));

					for (int k = 0; k < inst.getProperties().size(); k++) { // on parcourt les proprietes d'une instance

						Property prop = inst.getProperties().get(k);
						// il faut que l'unicite des proprietes soit deja etablie

						for (int l = 0; l < prop.getValues().size(); l++) { // on parcourt les valeurs d'une propriete (peut etre a enlever en ameliorant la structure de donnees ?)
							String aValURI = valURI + lURI;
							lURI++;
							Value value = prop.getValues().get(l);
							value.setUri(aValURI);
							Resource val = model.createResource(aValURI);
							aSaSet.addProperty(model.getProperty(ns + prop.getName()), val); // PAF (ns + ...) ?

							// annotations
							val.addProperty(model.getProperty(ns + "hasValue"), getLocalFromUri(value.getValue())); // la valeur
							//getLocalFromUri() a faire mieux
							val.addProperty(model.getProperty(ns + "isFrom"), inst.getUri().toString().replaceFirst(".*/([^/?]+).*", "$1")); // l'instance de provenance (chaque instance possede une URI unique)
							val.addProperty(model.getProperty(ns + "isImplausible"), String.valueOf(value.getImplausible()));
							val.addProperty(model.getProperty(ns + "hasOccurenceFrequency"), String.valueOf(value.getOccurrenceFrequency()));
							val.addProperty(model.getProperty(ns + "hasHomogeneity"), String.valueOf(value.getHomogeneity()));
							
							if (!value.getImplausible()) { // si la valeur est plausible
								val.addProperty(model.getProperty(ns + "hasReliability"), String.valueOf(value.getSource().getReliability()));
								val.addProperty(model.getProperty(ns + "hasFreshness"), String.valueOf(value.getSource().getFreshness())); // 
								val.addProperty(model.getProperty(ns + "hasQualityScore"), String.valueOf(value.getQualityScore()));
								val.addProperty(model.getProperty(ns + "hasQualityValue"), value.getQualityValue());
								
								for (int z=0; z < value.getIsMorePreciseThanStr().size(); z++){
									val.addProperty(model.getProperty(ns + "morePreciseThan"), value.getIsMorePreciseThanStr().get(z)); //.getValue());
										
								}
								
								Value oldValue = bestValues.get(prop.getName()); // pas reussi a faire plus propre
								if (oldValue == null)
									bestValues.put(prop.getName(), value);
								else {
									if (value.getQualityScore() > oldValue.getQualityScore())
										bestValues.put(prop.getName(), value);
								}
							}
						}
					}
				}
				for (Entry<String, Value> e : bestValues.entrySet()) { // annotations
					// meilleurs
					// valeurs
					model.getResource(e.getValue().getUri()).addProperty(model.getProperty(ns + "hasMaximumScore"),
							"true"); // vrai si la valeur a le meilleur score
					// pour sa propriete et sa classe
					// d'equivalence (la valeur sera gardee
					// lors de la fusion)
					// aSaSet.addProperty(model.getProperty(ns + e.getKey()),
					// model.getResource(e.getValue().getUri())); // real fusion
				}
			}

			// Ecriture dans le fichier
			FileOutputStream fout = new FileOutputStream(outputFile);
			model.write(fout);

			graph = model; // a voir
		} catch (Exception e) {
			System.out.println("Failed: " + e);
		}
	}

}

class MyInputVerifier extends InputVerifier {
	@Override
	public boolean verify(JComponent input) {
		String text = ((JTextField) input).getText();
		Float test = null;
		try {
			test = Float.parseFloat(text);
		} catch (NumberFormatException numberFormatException) {
		}
		if (test instanceof Float && test <= 1 && test >= 0) {
			return true;
		}
		// JOptionPane.showMessageDialog(Fusion.guiFrame, "Please enter a real
		// number between 0 and 1!");
		return false;
	}
}