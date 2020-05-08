/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.partitioning.profile;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * An inter block profile.
 *
 * @author Vincent Mirian
 * @date Nov 6, 2017
 */
public class InterBlockProfile extends CapacityCollectionProfile {

  /**
   * Initializes a newly created {@link InterBlockProfile}.
   *
   * @param jsonObj The JSON data associated with the {@link InterBlockProfile}.
   * @param blocks A collection of {@link BlockProfile} objects.
   * @param capacity A collection of {@link CapacityProfile} objects.
   */
  public InterBlockProfile(
      final JSONObject jsonObj,
      final CObjectCollection<BlockProfile> blocks,
      final CObjectCollection<CapacityProfile> capacity) {
    super(jsonObj, capacity);
    // parse
    parse(jsonObj, blocks);
  }

  /*
   * Parse
   */
  private void parseSource(final JSONObject jsonObj, final CObjectCollection<BlockProfile> blocks) {
    final String sourceName = ProfileUtils.getString(jsonObj, "source");
    if (sourceName != null) {
      final BlockProfile source = blocks.findCObjectByName(sourceName);
      if (source == null) {
        throw new RuntimeException(sourceName + " not found.");
      }
      setSource(source);
    } else {
      throw new RuntimeException("Source not specified for " + getName() + ".");
    }
  }

  private void parseDestination(
      final JSONObject jsonObj, final CObjectCollection<BlockProfile> blocks) {
    final String destinationName = ProfileUtils.getString(jsonObj, "destination");
    if (destinationName != null) {
      final BlockProfile destination = blocks.findCObjectByName(destinationName);
      if (destination == null) {
        throw new RuntimeException(destinationName + " not found.");
      }
      setDestination(destination);
    } else {
      throw new RuntimeException("Destination not specified for " + getName() + ".");
    }
  }

  private void parse(final JSONObject jsonObj, final CObjectCollection<BlockProfile> blocks) {
    // source
    parseSource(jsonObj, blocks);
    // destination
    parseDestination(jsonObj, blocks);
  }

  /*
   * Getter and Setter
   */

  private void setSource(final BlockProfile bProfile) {
    source = bProfile;
  }

  public BlockProfile getSource() {
    return source;
  }

  private void setDestination(final BlockProfile bProfile) {
    destination = bProfile;
  }

  public BlockProfile getDestination() {
    return destination;
  }

  /*
   * isValid
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    rtn = super.isValid();
    rtn = rtn && getSource() != null;
    rtn = rtn && getDestination() != null;
    return rtn;
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (destination == null ? 0 : destination.hashCode());
    result = prime * result + (source == null ? 0 : source.hashCode());
    return result;
  }

  /*
   * Equals
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final InterBlockProfile other = (InterBlockProfile) obj;
    if (destination == null) {
      if (other.destination != null) {
        return false;
      }
    } else if (!destination.equals(other.destination)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    return true;
  }

  private BlockProfile source;
  private BlockProfile destination;
}
