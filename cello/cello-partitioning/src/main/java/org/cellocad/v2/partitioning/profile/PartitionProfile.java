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
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.profile.ProfileObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A partition profile.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public class PartitionProfile extends ProfileObject {

  private void init() {
    blockCapacityUnits = new CObjectCollection<>();
    blockCapacity = new CObjectCollection<>();
    interBlockCapacityUnits = new CObjectCollection<>();
    interBlockCapacity = new CObjectCollection<>();
    blocks = new CObjectCollection<>();
    interBlocks = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link PartitionProfile} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the {@link PartitionProfile}.
   */
  public PartitionProfile(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parse(jsonObj);
    assert isValid();
  }

  /*
   * Parse
   */
  private void parseCapacityUnits(final JSONObject jsonObj,
      final CObjectCollection<ProfileObject> capacityUnits) {
    // CapacityUnits
    final JSONArray jsonArr = (JSONArray) jsonObj.get("Capacity_Units");
    if (jsonArr != null) {
      for (int i = 0; i < jsonArr.size(); i++) {
        final Object capacityObj = jsonArr.get(i);
        if (Utils.isString(capacityObj)) {
          final String name = (String) capacityObj;
          final ProfileObject unit = new ProfileObject();
          unit.setName(name);
          unit.setIdx(capacityUnits.size());
          capacityUnits.add(unit);
        }
      }
    }
  }

  private void parseCapacity(final JSONObject jsonObj,
      final CObjectCollection<ProfileObject> capacityUnits,
      final CObjectCollection<CapacityProfile> capacities) {
    // Capacity
    final JSONArray jsonArr = (JSONArray) jsonObj.get("Capacity");
    if (jsonArr != null) {
      for (int i = 0; i < jsonArr.size(); i++) {
        final JSONObject capacityObj = (JSONObject) jsonArr.get(i);
        final CapacityProfile capacity = new CapacityProfile(capacityObj, capacityUnits);
        capacities.add(capacity);
      }
    }
  }

  private void parseBlocks(final JSONObject jsonObj) {
    // blocks
    final JSONArray jsonArr = (JSONArray) jsonObj.get("Blocks");
    if (jsonArr == null) {
      throw new RuntimeException("Blocks not specified for " + getName() + ".");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject blockObj = (JSONObject) jsonArr.get(i);
      final BlockProfile BP = new BlockProfile(blockObj, blockCapacity);
      blocks.add(BP);
    }
  }

  private void parseInterBlocks(final JSONObject jsonObj) {
    // interBlocks
    final JSONArray jsonArr = (JSONArray) jsonObj.get("InterBlocks");
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject blockObj = (JSONObject) jsonArr.get(i);
      final InterBlockProfile IBP = new InterBlockProfile(blockObj, blocks, interBlockCapacity);
      interBlocks.add(IBP);
    }
  }

  private void parseBlocksInformation(final JSONObject jsonObj) {
    // blocks
    final JSONObject blockObj = (JSONObject) jsonObj.get("Blocks");
    if (blockObj == null) {
      throw new RuntimeException("Blocks not specified for " + getName() + ".");
    }
    // capacityUnits
    parseCapacityUnits(blockObj, blockCapacityUnits);
    // capacity
    parseCapacity(blockObj, blockCapacityUnits, blockCapacity);
    // blocks
    parseBlocks(blockObj);
  }

  private void parseInterBlocksInformation(final JSONObject jsonObj) {
    // interBlocks
    final JSONObject blockObj = (JSONObject) jsonObj.get("InterBlocks");
    if (blockObj != null) {
      // capacityUnits
      parseCapacityUnits(blockObj, interBlockCapacityUnits);
      // capacity
      parseCapacity(blockObj, interBlockCapacityUnits, interBlockCapacity);
      // interBlocks
      parseInterBlocks(blockObj);
    }
  }

  private void parse(final JSONObject jsonObj) {
    // name
    // parseName(JObj);
    // blocks
    parseBlocksInformation(jsonObj);
    // interBlocks
    parseInterBlocksInformation(jsonObj);
  }

  /*
   * CapacityUnits
   */
  private ProfileObject getCapacityUnitsAtIdx(final CObjectCollection<ProfileObject> collection,
      final int index) {
    ProfileObject rtn = null;
    if (index >= 0 && index < getCapacityUnitsSize(collection)) {
      rtn = collection.get(index);
    }
    return rtn;
  }

  private int getCapacityUnitsSize(final CObjectCollection<ProfileObject> collection) {
    int rtn = 0;
    if (collection != null) {
      rtn = collection.size();
    }
    return rtn;
  }

  /*
   * Capacity
   */
  private CapacityProfile getCapacityAtIdx(final CObjectCollection<CapacityProfile> collection,
      final int index) {
    CapacityProfile rtn = null;
    if (index >= 0 && index < getCapacitySize(collection)) {
      rtn = collection.get(index);
    }
    return rtn;
  }

  private int getCapacitySize(final CObjectCollection<CapacityProfile> collection) {
    int rtn = 0;
    if (collection != null) {
      rtn = collection.size();
    }
    return rtn;
  }

  /*
   * Block
   */
  /**
   * Gets the block profile at the given index.
   * 
   * @param index An index.
   * @return The block profile at the given index.
   */
  public BlockProfile getBlockProfileAtIdx(final int index) {
    BlockProfile rtn = null;
    if (index >= 0 && index < getNumBlockProfile()) {
      rtn = blocks.get(index);
    }
    return rtn;
  }

  public int getNumBlockProfile() {
    final int rtn = blocks.size();
    return rtn;
  }

  public CapacityProfile getBlockCapacityAtIdx(final int index) {
    return getCapacityAtIdx(blockCapacity, index);
  }

  public int getNumBlockCapacity() {
    return getCapacitySize(blockCapacity);
  }

  public ProfileObject getBlockCapacityUnitsAtIdx(final int index) {
    return getCapacityUnitsAtIdx(blockCapacityUnits, index);
  }

  public int getNumBlockCapacityUnits() {
    return getCapacityUnitsSize(blockCapacityUnits);
  }

  public CObjectCollection<ProfileObject> getBlockCapacityUnits() {
    return blockCapacityUnits;
  }

  /*
   * InterBlock
   */
  /**
   * Gets the inter block profile at the given index.
   * 
   * @param index An index.
   * @return The inter block profile at the given index.
   */
  public InterBlockProfile getInterBlockProfileAtIdx(final int index) {
    InterBlockProfile rtn = null;
    if (index >= 0 && index < getNumInterBlockProfile()) {
      rtn = interBlocks.get(index);
    }
    return rtn;
  }

  public int getNumInterBlockProfile() {
    final int rtn = interBlocks.size();
    return rtn;
  }

  public CapacityProfile getInterBlockCapacityAtIdx(final int index) {
    return getCapacityAtIdx(interBlockCapacity, index);
  }

  public int getNumInterBlockCapacity() {
    return getCapacitySize(interBlockCapacity);
  }

  public ProfileObject getInterBlockCapacityUnitsAtIdx(final int index) {
    return getCapacityUnitsAtIdx(interBlockCapacityUnits, index);
  }

  public int getNumInterBlockCapacityUnits() {
    return getCapacityUnitsSize(interBlockCapacityUnits);
  }

  public CObjectCollection<ProfileObject> getInterBlockCapacityUnits() {
    return interBlockCapacityUnits;
  }

  /*
   * isValid
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    rtn = super.isValid();
    // blocks
    for (int i = 0; rtn && i < getNumBlockProfile(); i++) {
      final BlockProfile BP = getBlockProfileAtIdx(i);
      // block isValid
      rtn = rtn && BP.isValid();
    }
    // interBlocks
    for (int i = 0; rtn && i < getNumInterBlockProfile(); i++) {
      final InterBlockProfile IBP = getInterBlockProfileAtIdx(i);
      // interblock isValid
      rtn = rtn && IBP.isValid();
    }
    return rtn;
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (blockCapacity == null ? 0 : blockCapacity.hashCode());
    result = prime * result + (blockCapacityUnits == null ? 0 : blockCapacityUnits.hashCode());
    result = prime * result + (blocks == null ? 0 : blocks.hashCode());
    result = prime * result + (interBlockCapacity == null ? 0 : interBlockCapacity.hashCode());
    result =
        prime * result + (interBlockCapacityUnits == null ? 0 : interBlockCapacityUnits.hashCode());
    result = prime * result + (interBlocks == null ? 0 : interBlocks.hashCode());
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
    final PartitionProfile other = (PartitionProfile) obj;
    if (blockCapacity == null) {
      if (other.blockCapacity != null) {
        return false;
      }
    } else if (!blockCapacity.equals(other.blockCapacity)) {
      return false;
    }
    if (blockCapacityUnits == null) {
      if (other.blockCapacityUnits != null) {
        return false;
      }
    } else if (!blockCapacityUnits.equals(other.blockCapacityUnits)) {
      return false;
    }
    if (blocks == null) {
      if (other.blocks != null) {
        return false;
      }
    } else if (!blocks.equals(other.blocks)) {
      return false;
    }
    if (interBlockCapacity == null) {
      if (other.interBlockCapacity != null) {
        return false;
      }
    } else if (!interBlockCapacity.equals(other.interBlockCapacity)) {
      return false;
    }
    if (interBlockCapacityUnits == null) {
      if (other.interBlockCapacityUnits != null) {
        return false;
      }
    } else if (!interBlockCapacityUnits.equals(other.interBlockCapacityUnits)) {
      return false;
    }
    if (interBlocks == null) {
      if (other.interBlocks != null) {
        return false;
      }
    } else if (!interBlocks.equals(other.interBlocks)) {
      return false;
    }
    return true;
  }

  private CObjectCollection<ProfileObject> blockCapacityUnits;
  private CObjectCollection<CapacityProfile> blockCapacity;
  private CObjectCollection<ProfileObject> interBlockCapacityUnits;
  private CObjectCollection<CapacityProfile> interBlockCapacity;
  private CObjectCollection<BlockProfile> blocks;
  private CObjectCollection<InterBlockProfile> interBlocks;

}
