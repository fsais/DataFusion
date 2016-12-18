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
public class Instance {
	private URI uri;
	private Source source;
	private ArrayList<Property> properties = new ArrayList<Property>();
	//ArrayList<Triple> triples = new ArrayList<Triple>();
	//private boolean fused = false; //on ne fusionne actuellement pas les instances

	public Instance(URI uri, Source source) {
		this.uri = uri;
		this.source = source;
	}

	public void addToProperties(Property property) {
		properties.add(property);
	}

	public boolean containsProperty(String name){
		for(Property p : properties){
			if (p.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public Property getProperty(String name){
		//renvoi la propriete nommee name
		for(Property p : properties){
			if (p.getName().equals(name))
				return p;
		}
		//return null;
		//theoriquement, le programme ne rentre jamais ici puisqu'on a teste prealablement la presence de la propriete
		return new Property(name);
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	/*public void setTriples(ArrayList<Triple> triples) {
		this.triples = triples;
	}*/

	/*
	public boolean isFused() {
		return fused;
	}

	public void setFused(boolean fused) {
		this.fused = fused;
	}
*/
	public URI getUri() {
		return uri;
	}

	public Source getSource() {
		return source;
	}

	public ArrayList<Property> getProperties() {
		return properties;
	}

}
