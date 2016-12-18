/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fusion;

import java.net.URI;

/**
 *
 * @author ioanna
 */
public class SameAsLink {
	private URI value1; //Deux URIs (URI) qui referent au meme objet
	private URI value2;

	public SameAsLink(URI value1, URI value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	public URI getValue1() {
		return value1;
	}

	public void setValue1(URI value1) {
		this.value1 = value1;
	}

	public URI getValue2() {
		return value2;
	}

	public void setValue2(URI value2) {
		this.value2 = value2;
	}


}
