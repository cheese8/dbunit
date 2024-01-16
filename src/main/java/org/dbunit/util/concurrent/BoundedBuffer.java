/*
  File: BoundedBuffer.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
  17Jul1998  dl               Simplified by eliminating wait counts
  25aug1998  dl               added peek
   5May1999  dl               replace % with conditional (slightly faster)
*/

package org.dbunit.util.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.util.Assert;

/**
 * Efficient array-based bounded buffer class.
 * Adapted from CPJ, chapter 8, which describes design.
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>] <p>
 *
 * @author Doug Lea
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since ? (pre 2.1)
 */
@Slf4j
public class BoundedBuffer implements BoundedChannel {
    protected final Object[] array;      // the elements

    protected int takePtr = 0;            // circular indices
    protected int putPtr = 0;

    protected int usedSlots = 0;          // length
    protected int emptySlots;             // capacity - length

    /**
     * Helper monitor to handle puts.
     **/
    protected final Object putMonitor = new Object();

    /**
     * Create a BoundedBuffer with the given capacity.
     *
     * @throws IllegalArgumentException if capacity less or equal to zero
     **/
    public BoundedBuffer(int capacity) throws IllegalArgumentException {
        Assert.assertThat(capacity > 0, new IllegalArgumentException());
        array = new Object[capacity];
        emptySlots = capacity;
    }

    /**
     * Create a buffer with the current default capacity
     **/

    public BoundedBuffer() {
        this(DefaultChannelCapacity.get());
    }

    /**
     * Return the number of elements in the buffer.
     * This is only a snapshot value, that may change
     * immediately after returning.
     **/
    public synchronized int size() {
        return usedSlots;
    }

    public int capacity() {
        return array.length;
    }

    protected void incEmptySlots() {
        synchronized (putMonitor) {
            ++emptySlots;
            putMonitor.notify();
        }
    }

    protected synchronized void incUsedSlots() {
        ++usedSlots;
        notify();
    }

    protected final void insert(Object x) {
        --emptySlots;
        array[putPtr] = x;
        if (++putPtr >= array.length) putPtr = 0;
    }

    protected final Object extract() {
        --usedSlots;
        Object old = array[takePtr];
        array[takePtr] = null;
        if (++takePtr >= array.length) takePtr = 0;
        return old;
    }

    public Object peek() {
        synchronized (this) {
            if (usedSlots > 0) {
                return array[takePtr];
            } else {
                return null;
            }
        }
    }


    public void put(Object x) throws InterruptedException {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (putMonitor) {
            while (emptySlots <= 0) {
                try {
                    putMonitor.wait();
                } catch (InterruptedException ex) {
                    putMonitor.notify();
                    throw ex;
                }
            }
            insert(x);
        }
        incUsedSlots();
    }

    public boolean offer(Object x, long msecs) throws InterruptedException {
        log.debug("offer(x={}, msecs={}) - start", x, msecs);
        if (x == null) {
            throw new IllegalArgumentException();
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        synchronized (putMonitor) {
            long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
            long waitTime = msecs;
            while (emptySlots <= 0) {
                if (waitTime <= 0) {
                    return false;
                }
                try {
                    putMonitor.wait(waitTime);
                } catch (InterruptedException ex) {
                    putMonitor.notify();
                    throw ex;
                }
                waitTime = msecs - (System.currentTimeMillis() - start);
            }
            insert(x);
        }
        incUsedSlots();
        return true;
    }

    public Object take() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object old;
        synchronized (this) {
            while (usedSlots <= 0) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
            old = extract();
        }
        incEmptySlots();
        return old;
    }

    public Object poll(long msecs) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object old;
        synchronized (this) {
            long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
            long waitTime = msecs;

            while (usedSlots <= 0) {
                if (waitTime <= 0) {
                    return null;
                }
                try {
                    wait(waitTime);
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
                waitTime = msecs - (System.currentTimeMillis() - start);
            }
            old = extract();
        }
        incEmptySlots();
        return old;
    }
}