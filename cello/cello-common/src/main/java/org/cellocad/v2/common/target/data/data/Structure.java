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

package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The {@code Structure} class represents a gate's structure.
 *
 * @author Timothy Jones
 * @date 2020-02-13
 */
public class Structure extends CObject {

  private void init() {
    inputs = new CObjectCollection<>();
    outputs = new ArrayList<>();
    devices = new CObjectCollection<>();
  }

  private void parseName(final JSONObject jObj) {
    final String value = ProfileUtils.getString(jObj, Structure.S_NAME);
    setName(value);
  }

  private void parseInputs(final JSONObject jObj) {
    final JSONArray jArr = (JSONArray) jObj.get(Structure.S_INPUTS);
    if (jArr == null) {
      return;
    }
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject o = (JSONObject) jArr.get(i);
      final Input input = new Input(o);
      getInputs().add(input);
    }
  }

  private void parseOutputs(final JSONObject jObj) {
    final JSONArray jArr = (JSONArray) jObj.get(Structure.S_OUTPUTS);
    if (jArr == null) {
      return;
    }
    for (int i = 0; i < jArr.size(); i++) {
      final String value = (String) jArr.get(i);
      getOutputs().add(value);
    }
  }

  // TODO not static
  private static CObjectCollection<StructureDevice> nestDevices(
      final CObjectCollection<StructureDevice> devices) {
    final CObjectCollection<StructureDevice> rtn = new CObjectCollection<>();
    rtn.addAll(devices);
    final Iterator<StructureDevice> it = devices.iterator();
    while (it.hasNext()) {
      final StructureDevice d = it.next();
      for (int i = 0; i < d.getComponents().size(); i++) {
        final StructureObject o = d.getComponents().get(i);
        if (o instanceof StructureTemplate) {
          continue;
        }
        final Iterator<StructureDevice> jt = devices.iterator();
        while (jt.hasNext()) {
          final StructureDevice e = jt.next();
          if (e.equals(d)) {
            continue;
          }
          if (e.getName().equals(o.getName())) {
            d.getComponents().set(i, e);
            rtn.remove(e);
            break;
          }
        }
      }
    }
    return rtn;
  }

  private void linkTemplatesToInputs() throws CelloException {
    final Iterator<StructureDevice> it = getDevices().iterator();
    while (it.hasNext()) {
      final StructureDevice d = it.next();
      for (int i = 0; i < d.getComponents().size(); i++) {
        final StructureObject o = d.getComponents().get(i);
        if (o instanceof StructureTemplate) {
          final StructureTemplate t = (StructureTemplate) o;
          final Input input = getInputs().findCObjectByName(t.getName());
          if (input == null) {
            final String fmt = "Input %s not found in device %s.";
            throw new CelloException(String.format(fmt, t.getName(), d.getName()));
          }
          t.setInput(input);
        } else {
          continue;
        }
      }
    }
  }

  private void parseDevices(final JSONObject jObj) throws CelloException {
    final JSONArray jArr = (JSONArray) jObj.get(Structure.S_DEVICES);
    if (jArr == null) {
      return;
    }
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject o = (JSONObject) jArr.get(i);
      final StructureDevice d = new StructureDevice(o);
      getDevices().add(d);
    }
    linkTemplatesToInputs();
    devices = Structure.nestDevices(getDevices());
  }

  private void parseStructure(final JSONObject jObj) throws CelloException {
    parseName(jObj);
    parseInputs(jObj);
    parseOutputs(jObj);
    parseDevices(jObj);
  }

  public Structure(final JSONObject jObj) throws CelloException {
    init();
    parseStructure(jObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getName() != null;
    return rtn;
  }

  private StructureDevice getStructureDeviceByName(
      final String name, final StructureDevice device) {
    StructureDevice rtn = null;
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructureDevice) {
        if (o.getName().equals(name)) {
          rtn = (StructureDevice) o;
          break;
        }
        rtn = getStructureDeviceByName(name, (StructureDevice) o);
      }
    }
    return rtn;
  }

  /**
   * Get a device by name.
   *
   * @param name The name.
   * @return The device with the given name.
   */
  public StructureDevice getDeviceByName(final String name) {
    StructureDevice rtn = null;
    for (final StructureDevice device : getDevices()) {
      rtn = getStructureDeviceByName(name, device);
      if (rtn != null) {
        break;
      }
    }
    return rtn;
  }

  /*
   * Input
   */

  /**
   * Getter for {@code inputs}.
   *
   * @return The value of {@code inputs}.
   */
  public CObjectCollection<Input> getInputs() {
    return inputs;
  }

  private CObjectCollection<Input> inputs;

  /*
   * Output
   */

  /**
   * Getter for {@code outputs}.
   *
   * @return The value of {@code outputs}.
   */
  public List<String> getOutputs() {
    return outputs;
  }

  private List<String> outputs;

  /*
   * Device
   */

  /**
   * Getter for {@code devices}.
   *
   * @return The value of {@code devices}.
   */
  public CObjectCollection<StructureDevice> getDevices() {
    return devices;
  }

  private CObjectCollection<StructureDevice> devices;

  static final String S_NAME = "name";
  static final String S_INPUTS = "inputs";
  static final String S_OUTPUTS = "outputs";
  static final String S_DEVICES = "devices";
}
