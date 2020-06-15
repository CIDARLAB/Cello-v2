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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Timothy Jones
 * @date 2020-06-15
 */
public class RuleTreeNodeDeserializer extends StdDeserializer<RuleTreeNode> {

  private static final long serialVersionUID = -5158328919450287596L;

  public RuleTreeNodeDeserializer() {
    this(null);
  }

  protected RuleTreeNodeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public RuleTreeNode deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    final JsonNode node = p.getCodec().readTree(p);
    final ObjectMapper mapper = new ObjectMapper();
    if (node.get("rules").get(0).isTextual()) {
      TerminalRuleTreeNode rtn = new TerminalRuleTreeNode();
      RuleTreeFunction function = mapper.convertValue(node.get("function"), RuleTreeFunction.class);
      rtn.setFunction(function);
      JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, String.class);
      List<String> rules = mapper.convertValue(node.get("rules"), type);
      rtn.setRules(rules);
      return rtn;
    } else {
      ParentRuleTreeNode rtn = new ParentRuleTreeNode();
      RuleTreeFunction function = mapper.convertValue(node.get("function"), RuleTreeFunction.class);
      rtn.setFunction(function);
      JavaType type =
          mapper.getTypeFactory().constructCollectionType(Collection.class, RuleTreeNode.class);
      Collection<RuleTreeNode> rules = mapper.convertValue(node.get("rules"), type);
      rtn.setChildren(rules);
      return rtn;
    }
  }
}
