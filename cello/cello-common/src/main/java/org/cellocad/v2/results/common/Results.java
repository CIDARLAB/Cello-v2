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
package org.cellocad.v2.results.common;

import java.io.File;
import java.io.IOException;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CelloException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The results of an application. Writes a metadata file.
 *
 * @author Timothy Jones
 *
 * @date 2020-03-30
 *
 */
public class Results extends CObject {

	private ObjectMapper mapper;
	private ArrayNode array;
	private ObjectWriter writer;
	private File file;

	private void init() {
		this.mapper = new ObjectMapper();
		this.array = this.mapper.createArrayNode();
		this.writer = this.mapper.writer(new DefaultPrettyPrinter());
	}

	/**
	 * Create a new results object.
	 *
	 * @param directory The directory of the results metadata file.
	 * @throws CelloException
	 */
	public Results(File directory) {
		super();
		this.init();
		this.file = new File(directory, "results.json");
	}

	/**
	 * Add a result object.
	 *
	 * @param result The result.
	 * @throws JsonGenerationException Unable to generate JSON.
	 * @throws JsonMappingException    Unable to map JSON.
	 * @throws IOException             Unable to write to results file.
	 */
	public void addResult(Result result) throws JsonGenerationException, JsonMappingException, IOException {
		JsonNode node = this.mapper.valueToTree(result);
		this.array.add(node);
		this.writer.writeValue(this.file, node);
	}

}
