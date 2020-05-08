/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.placing.algorithm.Eugene.target.data.data;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.json.simple.JSONObject;

/**
 * A representation of a {@code Device} object in Eugene.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 */
public class EugeneDevice extends StructureDevice {

  public EugeneDevice(final JSONObject jObj) {
    super(jObj);
  }

  public EugeneDevice(final StructureDevice device) {
    super(device);
  }

  @Override
  public String toString() {
    String rtn = "";
    String root = "";
    final List<StructureDevice> devices = new ArrayList<>();
    root += "Device " + getName() + "(" + Utils.getNewLine();
    for (int i = 0; i < getComponents().size(); i++) {
      final StructureObject o = getComponents().get(i);
      if (o instanceof StructureDevice) {
        devices.add((StructureDevice) o);
      }
      if (o instanceof StructureTemplate) {
        final StructureTemplate t = (StructureTemplate) o;
        root += "    " + t.getInput().getPartType();
      } else {
        root += "    " + o.getName();
      }
      if (i < getComponents().size() - 1) {
        root += ",";
      }
      root += Utils.getNewLine();
    }
    root.substring(0, root.length() - 2);
    root += ");" + Utils.getNewLine();
    for (int i = devices.size() - 1; i >= 0; i--) {
      final StructureDevice d = devices.get(i);
      final EugeneDevice e = new EugeneDevice(d);
      // rtn += e.toString();
      // FIXME should not be hardcoded
      rtn += "cassette " + e.getName() + "();";
      rtn += Utils.getNewLine();
    }
    rtn += root;
    return rtn;
  }

}
