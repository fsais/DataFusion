/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fusion;

import java.net.URI;
import java.util.ArrayList;

/**
 *
 * @author ioanna
 */
public class Value {
	private Source source;
	private String value;
	private String uri;
	private float homogeneity;
	private float occurrenceFrequency;
	private boolean implausible; 
	private String violatedRules;
	private float qualityScore;
	private String qualityValue;
	private ArrayList<Value>isMorePreciseThan = new ArrayList<Value>();
	private ArrayList<String>isMorePreciseThanStr = new ArrayList<String>();
	private ArrayList<Value>isSynonym = new ArrayList<Value>();

	public Value(String value) {
		this.value = value;
		/*if (value.contains("dbpedia")){ //fait dans getINASameAsLink
			this.setSource(sources.get("fr.dbpedia.org"));
		}
		else{
			this.setSource(sources.get("www.ina.fr"));
		}*/
	}
	
	public Value(Source source, String value) {
		this.source = source;
		this.value = value;
	}

	/*public String toString(){
		String s = new String();
		s = "value : "+value+"\nString : "+triple.getInstance().getUrl()+"\nProperty : "+triple.getProperty().getName();
		return s;
	}*/

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public float getHomogeneity() {
		return homogeneity;
	}

	public void setHomogeneity(float homogeneity) {
		this.homogeneity = homogeneity;
	}

	public float getOccurrenceFrequency() {
		return occurrenceFrequency;
	}

	public void setOccurrenceFrequency(float occurrenceFrequency) {
		this.occurrenceFrequency = occurrenceFrequency;
	}

	public boolean getImplausible() {
		return implausible;
	}

	public void setImplausible(boolean implausible) {
		this.implausible = implausible;
	}

	public String getViolatedRules() {
		return violatedRules;
	}

	public void setViolatedRules(String violatedRules) {
		this.violatedRules = violatedRules;
	}

	public float getQualityScore() {
		return qualityScore;
	}

	public void setQualityScore(float qualityScore) {
		this.qualityScore = qualityScore;
	}

	public String getQualityValue() {
		return qualityValue;
	}

	public void setQualityValue(String qualityValue) {
		this.qualityValue = qualityValue;
	}

	public ArrayList<Value> getIsMorePreciseThan() {
		return isMorePreciseThan;
	}

	public ArrayList<String> getIsMorePreciseThanStr() {
		return isMorePreciseThanStr;
	}

	public void setIsMorePreciseThan(ArrayList<Value> isMorePreciseThan) {
		this.isMorePreciseThan = isMorePreciseThan;
	}

	public void addToMorePrecise(Value value) {
		this.isMorePreciseThan.add(value);
	}
	
	public void addToMorePreciseStr(String value) {
		this.isMorePreciseThanStr.add(value);
	}
	
	public ArrayList<Value> getIsSynonym() {
		return isSynonym;
	}    
	public void addToSynonyms(Value value) {
		this.isSynonym.add(value);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	public String dispalyIsMorePreciseThan(){
		
		if(isMorePreciseThan.isEmpty())
			return ""; 
		else {
			String s="";
			for (int i=0; i < isMorePreciseThan.size(); i++) {
				s=s+(String)isMorePreciseThan.get(i).getValue()+", ";
			}
			return s;
		}
	}
	
	
	public String dispalyIsMorePreciseThanStr(){
		
		if(isMorePreciseThanStr.isEmpty())
			return ""; 
		else {
			String s="";
			for (int i=0; i < isMorePreciseThanStr.size(); i++) {
				s=s+(String)isMorePreciseThanStr.get(i)+", ";
			}
			return s;
		}
	}
	
public String dispalyIsSynonym(){
		
		if(isSynonym.isEmpty())
			return ""; 
		else {
			String s="";
			for (int i=0; i < isSynonym.size(); i++) {
				s+=(String)isSynonym.get(i).getValue();
			}
			return s;
		}
	}
//	
//	// define if a value is more precise than another
//	private static boolean isMorePrecise(Value val1, Value val2) {
//		
//		if (val1.getValue().contains(val2.getValue()))// || checkKnowledgeBase(val1.getValue(), val2.getValue()))
//			{
//			val1.addToMorePrecise(val2);
//			return true;
//			}
//		else 
//			return false;
//		
//
////		if (val2.getValue().contains(val1.getValue()) || checkKnowledgeBase(val2.getValue(), val1.getValue()))
////			// val1.addToMorePrecise(val2);
////			return val2;
////
////		if (val1.getValue().length() > val2.getValue().length()) // comparer la longueur
////			return val1;
////		else
////			return val2;
//
//	}



}
