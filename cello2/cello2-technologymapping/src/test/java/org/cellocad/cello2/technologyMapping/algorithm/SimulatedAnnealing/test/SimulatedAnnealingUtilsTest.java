/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.test;

import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.SimulatedAnnealingUtils;
import org.junit.Test;

import org.junit.Assert;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-08-13
 *
 */
public class SimulatedAnnealingUtilsTest {

	@Test
	public void testGetCObjectsSortedByIdx() {
		CObject a = new CObject("a",1,3);
		CObject b = new CObject("b",1,1);
		CObject c = new CObject("c",1,0);
		CObject d = new CObject("d",1,2);
		CObjectCollection<CObject> coll = new CObjectCollection<>();
		coll.add(a);
		coll.add(b);
		coll.add(c);
		coll.add(d);
		List<CObject> sorted = SimulatedAnnealingUtils.getCObjectsSortedByIdx(coll);
		Assert.assertEquals(c, sorted.get(0));
		Assert.assertEquals(b, sorted.get(1));
		Assert.assertEquals(d, sorted.get(2));
		Assert.assertEquals(a, sorted.get(3));
	}
	
}
