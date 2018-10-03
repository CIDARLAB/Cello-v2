/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package common;

/**
 * The Pair class is a 2-tuple class.
 * @param <F> the type of the first element in the tuple.
 * @param <S> the type of the second element in the tuple.
 * @author Vincent Mirian
 * 
 * @date Oct 28, 2017
 *
 */
public class Pair<F, S> {
    private F first; //first member of pair
    private S second; //second member of pair

	/**
	 *  Initializes a newly created Pair with its <i>first</i> set to parameter <i>first</i>,and,
	 *  its <i>second</i> set to parameter <i>second</i>.
	 *  
	 *  @param first the first element of the Pair
	 *  @param second the second element of the Pair
	 */
    public Pair(final F first, final S second) {
        this.setFirst(first);
        this.setSecond(second);
    }

	/**
	 *  Setter for <i>first</i>
	 *  @param first the element to set <i>first</i>
	 */
    public void setFirst(final F first) {
        this.first = first;
    }

	/**
	 *  Setter for <i>second</i>
	 *  @param second the second to set <i>second</i>
	 */
    public void setSecond(final S second) {
        this.second = second;
    }

	/**
	 *  Getter for <i>first</i>
	 *  @return the first element of this instance
	 */
    public F getFirst() {
        return first;
    }

	/**
	 *  Getter for <i>second</i>
	 *  @return the second element of this instance
	 */
    public S getSecond() {
        return second;
    }

	/*
	 * HashCode
	 */
	/**
	 *  Returns a hash code value for the object.
	 *  @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	/**
	 *  Indicates whether some other object is "equal to" this one.
	 *  
	 *  @param obj the object to compare with.
	 *  @return true if this object is the same as the obj argument; false otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof Pair<?, ?>)) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}
}
