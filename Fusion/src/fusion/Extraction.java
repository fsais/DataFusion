package fusion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.VCARD;

public class Extraction { // Nom de classe en francais ...

	private Model model;
	private String fusionQuery;

	private String fusionOutputFile = "fusionResult.rdf";
	private String outputFile = "queryResult.rdf";
	private String queryFile = "fusionQuery.rq";

	public Extraction(Model model){
		this.model = model;
		fusionQuery = readFile(queryFile);
	}

	
	//execute a Sparql query on model and return a ResultSet
	public ArrayList<String> executeQuery(String strQuery){

		ArrayList<String> resultsList = new ArrayList<String>();
		
		Query query = QueryFactory.create(strQuery);
		QueryExecution qe = QueryExecutionFactory.create(query, this.model);
		ResultSet results = qe.execSelect();
		List<String> l = results.getResultVars();
		//ResultSetFormatter.out(System.out, results, query); //
												//
		while(results.hasNext()) {
	        resultsList.add(results.toString());
	        results.next();
		}
		
		qe.close();	
		return resultsList;
	}

	// construct a Sparql query on model, write a rdf graph in a file and return
	public Model constructQuery(String strQuery, String fileName){

		Query query = QueryFactory.create(strQuery);
		QueryExecution qe = QueryExecutionFactory.create(query, this.model);
		Model results = qe.execConstruct();
		try{
			FileOutputStream fout = new FileOutputStream(fileName); 
			results.write(fout);
		}
		catch (Exception e){
			System.out.println("Failed: " + e);
		}
		qe.close();
		return results;
	}

	// specific query
	public Model fusionModel(){
		return this.constructQuery(this.fusionQuery, this.fusionOutputFile);
	}

	// load in outputFile all property of instance instName
	public Model getInstance(String instName){
		String query = "PREFIX p: <http://localhost/rdf/>" +
				"CONSTRUCT { <"+instName+"> ?prop ?val . " +
				"			?val ?p ?v }" +
				"WHERE {  ?saSet ?prop ?val . " + 
				"		 ?val p:isFrom '"+instName+"' ." +
				" 		 ?val ?p ?v	}";

		return constructQuery(query, this.outputFile);
	}

	// load in outputFile all property of instance valueName (work as well for a saSet)
	public Model getValue(String valueName){
		String query = "PREFIX p: <http://localhost/rdf/>" +
				"CONSTRUCT { p:"+valueName+" ?prop ?val } " +
				"WHERE { p:"+valueName+" ?prop ?val }";

		return constructQuery(query, this.outputFile);
	}

	// load in outputFile all values of property propName for a certain same as set
	public Model getProperty(String propName, String sasetName){
		String query = "PREFIX p: <http://localhost/rdf/>" +
				"CONSTRUCT { p:"+sasetName+" p:"+propName+" ?val . " +
				" 		    ?val ?p ?v } " +
				"WHERE 	  { p:"+sasetName+" p:"+propName+" ?val . " +
				" 		    ?val ?p ?v }";

		return constructQuery(query, this.outputFile);
	}

	public ArrayList<String> getSaSet(){
		String query = "PREFIX p: <http://localhost/rdf/>" +
				"SELECT ?x " +
				"WHERE { p:saSets p:hasSameAsSet ?x }";
		return executeQuery(query);
	}

	public void testStatement(){
		Resource r = model.getResource("http://localhost/rdf/val10"); //("http://localhost/rdf/saSets");
		StmtIterator iter = model.listStatements(
			new SimpleSelector(r, null,(RDFNode) null) {
				public boolean selects(Statement s)
				{return true;}
			});
		if (iter.hasNext()) {
		    while (iter.hasNext()) {
		        System.out.println("  " + iter.nextStatement().getString());
		    }
		}
	}


	// return a fused graph without sparql
	private Model dataFusion(){
		/*Resource root = model.getResource("http://localhost/rdf/saSets");
		Model fusedModel = ModelFactory.createDefaultModel();
		//fusedModel.*/
		String ns = "http://localhost/rdf/";
		ResIterator iterSets = model.listResourcesWithProperty(null, ns + "hasSameAsSet");
		while (iterSets.hasNext()) {
		    Resource saSet = iterSets.nextResource();
		    //ResIterator iterVals = model.list //model.listResourcesWithProperty(null, ns + "hasSameAsSet");
		}
		return null;
	}

	private String readFile(String fileName){	

		String line = null;
		String output = "";

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				output = output + line + "\n";
			}   
			bufferedReader.close();         
		}

		catch(FileNotFoundException ex) {
			System.out.println(
					"Fichier introuvable: '" + 
							fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println(
					"Erreur de lecture du fichier: '" 
							+ fileName + "'");
		}

		return output;
	}


}
