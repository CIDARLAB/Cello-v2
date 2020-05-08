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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A partitioner profile parser.
 *
 * @author Vincent Mirian
 * @date Oct 27, 2017
 */
public class PartitionerProfileParser extends CObject {

  private void init() {
    profiles = new CObjectCollection<>();
    pProfiles = new CObjectCollection<>();
    aProfiles = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link PartitionerProfileParser}.
   *
   * @param filename The file name.
   */
  public PartitionerProfileParser(final String filename) {
    init();
    // parse
    parse(filename);
  }

  /**
   * Get the partitioner profile with the given name.
   *
   * @param name A partitioner profile name.
   * @return The partitioner profile with the given name.
   */
  public PartitionerProfile getPartionerProfile(final String name) {
    PartitionerProfile rtn = null;
    rtn = profiles.findCObjectByName(name);
    return rtn;
  }

  /*
   * Parse
   */
  private void parse(final String filename) {
    final JSONParser parser = new JSONParser();
    try {
      final JSONObject jsonTop = (JSONObject) parser.parse(new FileReader(filename));
      JSONArray jsonArr;
      // parse PartitionProfile
      jsonArr = (JSONArray) jsonTop.get("PartitionProfiles");
      if (jsonArr == null) {
        throw new RuntimeException("PartitionProfiles missing in " + filename + ".");
      }
      for (int i = 0; i < jsonArr.size(); i++) {
        final JSONObject jsonObj = (JSONObject) jsonArr.get(i);
        final PartitionProfile P = new PartitionProfile(jsonObj);
        pProfiles.add(P);
      }
      // parse AlgorithmProfile
      jsonArr = (JSONArray) jsonTop.get("AlgorithmProfiles");
      if (jsonArr == null) {
        throw new RuntimeException("AlgorithmProfiles missing in " + filename + ".");
      }
      for (int i = 0; i < jsonArr.size(); i++) {
        final JSONObject jsonObj = (JSONObject) jsonArr.get(i);
        final AlgorithmProfile A = new AlgorithmProfile(jsonObj);
        aProfiles.add(A);
      }
      // parse PartitionerProfile
      jsonArr = (JSONArray) jsonTop.get("PartitionerProfiles");
      if (jsonArr == null) {
        throw new RuntimeException("PartitionerProfiles missing in " + filename + ".");
      }
      for (int i = 0; i < jsonArr.size(); i++) {
        final JSONObject jsonObj = (JSONObject) jsonArr.get(i);
        final PartitionerProfile PP = new PartitionerProfile(pProfiles, aProfiles, jsonObj);
        profiles.add(PP);
      }
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    } catch (final ParseException e) {
      e.printStackTrace();
    }
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (aProfiles == null ? 0 : aProfiles.hashCode());
    result = prime * result + (pProfiles == null ? 0 : pProfiles.hashCode());
    result = prime * result + (profiles == null ? 0 : profiles.hashCode());
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
    final PartitionerProfileParser other = (PartitionerProfileParser) obj;
    if (aProfiles == null) {
      if (other.aProfiles != null) {
        return false;
      }
    } else if (!aProfiles.equals(other.aProfiles)) {
      return false;
    }
    if (pProfiles == null) {
      if (other.pProfiles != null) {
        return false;
      }
    } else if (!pProfiles.equals(other.pProfiles)) {
      return false;
    }
    if (profiles == null) {
      if (other.profiles != null) {
        return false;
      }
    } else if (!profiles.equals(other.profiles)) {
      return false;
    }
    return true;
  }

  private CObjectCollection<PartitionerProfile> profiles;
  private CObjectCollection<PartitionProfile> pProfiles;
  private CObjectCollection<AlgorithmProfile> aProfiles;
}
