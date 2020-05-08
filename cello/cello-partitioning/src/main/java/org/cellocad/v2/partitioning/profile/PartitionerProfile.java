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
import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * A partitioner profile.
 *
 * @author Vincent Mirian
 * @date Oct 27, 2017
 */
public class PartitionerProfile extends ProfileObject {

  public PartitionerProfile(
      final CObjectCollection<PartitionProfile> pProfiles,
      final CObjectCollection<AlgorithmProfile> algProfiles,
      final JSONObject jsonObj) {
    super(jsonObj);
    parse(pProfiles, algProfiles, jsonObj);
  }

  /*
   * Parse
   */
  private void parsePartitionProfile(
      final CObjectCollection<PartitionProfile> pProfiles, final JSONObject jsonObj) {
    PartitionProfile ppObj;
    final String PartitionProfile = ProfileUtils.getString(jsonObj, "PartitionProfile");
    if (PartitionProfile == null) {
      throw new RuntimeException("PartitionProfile not specified for " + getName() + ".");
    }
    ppObj = pProfiles.findCObjectByName(PartitionProfile);
    if (ppObj == null) {
      throw new RuntimeException("PartitionProfile not found for " + getName() + ".");
    }
    setPProfile(ppObj);
  }

  private void parseAlgorithmProfile(
      final CObjectCollection<AlgorithmProfile> algProfiles, final JSONObject jsonObj) {
    AlgorithmProfile apObj;
    final String AlgorithmProfile = ProfileUtils.getString(jsonObj, "AlgorithmProfile");
    if (AlgorithmProfile == null) {
      throw new RuntimeException("AlgorithmProfile not specified for " + getName() + ".");
    }
    apObj = algProfiles.findCObjectByName(AlgorithmProfile);
    if (apObj == null) {
      throw new RuntimeException("AlgorithmProfile not found for " + getName() + ".");
    }
    setalgProfile(apObj);
  }

  private void parse(
      final CObjectCollection<PartitionProfile> pProfiles,
      final CObjectCollection<AlgorithmProfile> algProfiles,
      final JSONObject jsonObj) {
    // name
    // this.parseName(JObj);
    // PartitionProfile
    parsePartitionProfile(pProfiles, jsonObj);
    // AlgorithmProfile
    parseAlgorithmProfile(algProfiles, jsonObj);
  }

  private void setPProfile(final PartitionProfile pProfile) {
    this.pProfile = pProfile;
  }

  public PartitionProfile getPProfile() {
    return pProfile;
  }

  private void setalgProfile(final AlgorithmProfile algProfile) {
    this.algProfile = algProfile;
  }

  public AlgorithmProfile getalgProfile() {
    return algProfile;
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (algProfile == null ? 0 : algProfile.hashCode());
    result = prime * result + (pProfile == null ? 0 : pProfile.hashCode());
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
    final PartitionerProfile other = (PartitionerProfile) obj;
    if (algProfile == null) {
      if (other.algProfile != null) {
        return false;
      }
    } else if (!algProfile.equals(other.algProfile)) {
      return false;
    }
    if (pProfile == null) {
      if (other.pProfile != null) {
        return false;
      }
    } else if (!pProfile.equals(other.pProfile)) {
      return false;
    }
    return true;
  }

  private PartitionProfile pProfile;
  private AlgorithmProfile algProfile;
}
