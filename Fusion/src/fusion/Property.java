/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fusion;

import java.util.ArrayList;

/**
 *
 * @author ioanna
 */
public class Property {
	
	private String name;
	private ArrayList<Value> values;

	public Property(String name) {
		this.name = name;
		this.values = new ArrayList<Value>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Value> getValues() {
		return values;
	}
	
	public boolean containsValue(String name){
		for(Value v : values){
			if (v.getValue().equals(name))
				return true;
		}
		return false;
	}

	public String getValuesToString() {
		String valStr = "";
		for (Value val : this.values){
			valStr = valStr.concat(val.getValue()+", ");
		}
		if (this.values.isEmpty())
			return "";
		return valStr.substring(0, valStr.lastIndexOf(","));
	}

	public void addToValues(Value value) {
		values.add(value);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Property other = (Property) obj;
		return (this.name.compareTo(other.name) == 0);
	}
	
}
