/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.common.target.data.data;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 *
 */
public class StructureTemplate extends StructureObject {

	public StructureTemplate(final String template) {
		if (!template.startsWith(S_PREFIX)) {
			throw new RuntimeException("Not a template.");
		}
		String str = template.substring(1);
		this.setName(str);
	}

	/**
	 * Getter for <code>input</code>.
	 *
	 * @return The value of <code>input</code>.
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Setter for <code>input</code>.
	 *
	 * @param input The value to set <code>input</code>.
	 */
	public void setInput(Input input) {
		this.input = input;
	}

	private Input input;

	public static final String S_PREFIX = "#";

}
