/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.cello2.common.graph.graph;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.AbstractEdge;

/**
 * The EdgeTemplate class is a class representing the edge(s) of a <i>GraphTemplate</i>.
 * @param <T> the type of the VertexTemplate
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 2, 2017
 *
 */
public class EdgeTemplate<T extends VertexTemplate<?>> extends AbstractEdge<T>{

	/**
	 *  Initializes a newly created EdgeTemplate 
	 */
	public EdgeTemplate(){
		super();
        this.setSrc(null);
        this.setDst(null);
	}

	/**
	 *  Initializes a newly created EdgeTemplate with its source node defined by parameter <i>Src</i>
	 *  @param Src the source node
	 */
	public EdgeTemplate(final T Src) {
		super(Src);
        this.setDst(null);
    }

	/**
	 *  Initializes a newly created EdgeTemplate with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other EdgeTemplate
	 */
	public EdgeTemplate(final EdgeTemplate<T> other) {
		super(other);
    }

	/**
	 *  Setter for the instance's destination node
	 *  @param Dst the destination node
	 */
	public void setDst(final T Dst){
		this.addDst(Dst);
	}

	/**
	 *  Getter for the instance's destination node
	 *  @return the destination node of this instance
	 */
	public T getDst(){
		return this.getDstAtIdx(0);
	}


	/**
	 *  Adds the destination node defined by parameter <i>Dst</i> to the destination node list, that is reduced to <u>a</u> destination node in this class, of this instance
	 *  @param Dst a non-null destination node
	 */
	@Override
	public void addDst(final T Dst){
		if (Dst != null) {
			if (this.getNumDst() == 0) {
				this.getMyDst().add(Dst);				
			}
			else {
				this.getMyDst().set(0, Dst);
			}
			assert(this.getNumDst() == 1);
		}
	}
	/*
	 * toString
	 */
	/**
	 *  Returns a string representation of the object.
	 *  @return a string representation of the object.
	 */
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// src
		rtn = rtn + this.getEntryToString("src", this.getSrc().getName());
		// dst
		rtn = rtn + this.getEntryToString("dst", this.getDst().getName());
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		superStr = super.toString();
		superStr = Utils.addIndent(1, superStr);
		rtn = rtn + superStr;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}
}
