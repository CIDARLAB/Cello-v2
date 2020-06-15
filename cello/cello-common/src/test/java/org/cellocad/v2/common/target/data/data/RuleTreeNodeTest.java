/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.common.target.data.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * Tests for {@link ParentRuleTreeNode}.
 *
 * @author Timothy Jones
 * @date 2020-06-15
 */
public class RuleTreeNodeTest {

  @Test
  public void Deserialization_MockNode_ShouldDeserialize()
      throws JsonMappingException, JsonProcessingException {
    final String str =
        "{\n"
            + "  \"function\": \"AND\",\n"
            + "  \"rules\": [\n"
            + "  {\n"
            + "    \"function\": \"AND\",\n"
            + "    \"rules\": [\n"
            + "    \"[0] EQUALS L1\",\n"
            + "    \"[1] EQUALS Ascar\",\n"
            + "    \"[3] EQUALS Bscar\"\n"
            + "    ]\n"
            + "  },\n"
            + "  {\n"
            + "    \"function\": \"AND\",\n"
            + "    \"rules\": [\n"
            + "    \"[5] EQUALS Cscar\",\n"
            + "    \"[7] EQUALS Dscar\",\n"
            + "    \"[9] EQUALS Escar\"\n"
            + "    ]\n"
            + "  }\n"
            + "  ]\n"
            + "}";
    ObjectMapper mapper = new ObjectMapper();
    RuleTreeNode node = mapper.readValue(str, RuleTreeNode.class);
    assert (node instanceof ParentRuleTreeNode);
  }
}
