package com.whswzz.prfluroanalyzer.fluoro.entity;

import java.util.ArrayList;

public class HumpList extends ArrayList<Hump> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public HumpList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public Hump get(int index) {
        return (Hump) super.get(index);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
