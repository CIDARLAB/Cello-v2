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

package org.cellocad.v2.common.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.junit.Test;

/**
 * Tests for {@link CircularLinkedList}.
 *
 * @author Timothy Jones
 * @date 2020-06-14
 */
public class CircularLinkedListTest {

  @Test
  public void add_IntegerAndEmptyList_ShouldHaveSizeOne() {
    List<Integer> list = new CircularLinkedList<Integer>();
    list.add(1);
    assertEquals(list.size(), 1);
  }

  @Test
  public void remove_IntegerAndSingletonList_ShouldHaveSizeZero() {
    List<Integer> list = new CircularLinkedList<Integer>();
    list.add(1);
    list.remove(0);
    assertEquals(list.size(), 0);
  }

  @Test
  public void remove_NoneAndSingletonQueue_ShouldHaveSizeZero() {
    Queue<Integer> list = new CircularLinkedList<Integer>();
    list.add(1);
    list.remove();
    assertEquals(list.size(), 0);
  }

  @Test
  public void addFirst_FourIntegersAndEmptyDeque_ShouldHaveSizeFour() {
    Deque<Integer> list = new CircularLinkedList<Integer>();
    list.addFirst(1);
    list.addFirst(2);
    list.addFirst(3);
    list.addFirst(4);
    assertEquals(list.size(), 4);
  }

  @Test
  public void removeLast_NoneAndFourElementDeque_ShouldHaveSizeZero() {
    Deque<Integer> list = new CircularLinkedList<Integer>();
    list.addFirst(1);
    list.addFirst(2);
    list.addFirst(3);
    list.addFirst(4);
    list.removeLast();
    list.removeLast();
    list.removeLast();
    list.removeLast();
    assertEquals(list.size(), 0);
  }

  @Test
  public void listIterator_NoneAndFourElementList_ShouldCircle() {
    List<Integer> list = new CircularLinkedList<Integer>();
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    ListIterator<Integer> it = list.listIterator();
    it.next();
    it.next();
    it.next();
    it.next();
    int i = it.next();
    assertEquals(i, 1);
  }

  @Test
  public void listIterator_NoneAndSingletonList_ShouldReverse() {
    List<Integer> list = new CircularLinkedList<Integer>();
    list.add(1);
    ListIterator<Integer> it = list.listIterator();
    it.next();
    int i = it.next();
    assertEquals(i, 1);
  }

  @Test
  public void listIterator_NoneAndEmptyList_ShouldNotHaveNext() {
    List<Integer> list = new CircularLinkedList<Integer>();
    ListIterator<Integer> it = list.listIterator();
    assertFalse(it.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void next_EmptyListIterator_ShouldThrowNoSuchElementException() {
    List<Integer> list = new CircularLinkedList<Integer>();
    ListIterator<Integer> it = list.listIterator();
    it.next();
  }

  @Test(expected = NoSuchElementException.class)
  public void previous_EmptyListIterator_ShouldThrowNoSuchElementException() {
    List<Integer> list = new CircularLinkedList<Integer>();
    ListIterator<Integer> it = list.listIterator();
    it.previous();
  }

  @Test
  public void next_IteratorAndModifiedList_ShouldCircle() {
    Deque<Integer> list = new CircularLinkedList<Integer>();
    list.addFirst(1);
    list.addLast(2);
    list.addFirst(3);
    list.remove(2);
    Iterator<Integer> it = list.iterator();
    it.next();
    it.next();
    int i = it.next();
    assertEquals(i, 3);
  }

  @Test
  public void ObjectIterator_IteratorAndListOfTwoQueues_ShouldAlternateCircle() {
    List<Queue<Integer>> list = new CircularLinkedList<>();
    Queue<Integer> q1 = new ArrayDeque<Integer>();
    list.add(q1);
    q1.add(2);
    q1.add(4);
    q1.add(6);
    Queue<Integer> q2 = new ArrayDeque<Integer>();
    list.add(q2);
    q2.add(13);
    q2.add(15);
    q2.add(17);
    Iterator<Queue<Integer>> it = list.iterator();
    Queue<Integer> q = null;
    q = it.next();
    q.poll();
    q = it.next();
    q.poll();
    q = it.next();
    q.poll();
    q = it.next();
    int i = q.poll();
    assertEquals(i, 15);
  }
}
