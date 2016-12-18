/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fusion;

/**
 *
 * @author ioanna
 */
public class Source {
	private String name;
	private float reliability;
	private float freshness;

	public Source(String name, float reliability, float freshness) {
		this.name = name;
		this.reliability = reliability;
		this.freshness = freshness;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public float getReliability() {
		return reliability;
	}

	public void setReliability(float reliability) {
		this.reliability = reliability;
	}

	public float getFreshness() {
		return freshness;
	}

	public void setFreshness(float freshness) {
		this.freshness = freshness;
	}

}
