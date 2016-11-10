package com.roboboy.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Represents a collection of elements from weighted probability.
 *
 * @param <E>
 */
public class RandomCollection<E> {
	
	private final NavigableMap<Double, E> map = new TreeMap<>();
	private final Random random;
	private double total = 0;
	
	/**
	 * Constructs a new RandomCollection from a new Random instance.
	 */
	public RandomCollection() {
		this(new Random());
	}
	
	/**
	 * Constructs a new RandomCollection from a given Random instance.
	 */
	public RandomCollection(Random random) {
		this.random = random;
	}
	
	/**
	 * Adds an element to the collection.
	 * @param weight How likely this element is to be chosen reletive to others
	 * @param result The element to add
	 */
	public void add(double weight, E result) {
		if (weight <= 0) throw new IllegalArgumentException("Weight must be positive.");
		total += weight;
		map.put(total, result);
	}
	
	/**
	 * Gets a random element from the collection.
	 * @return A random element
	 */
	public E next() {
		if (map.isEmpty()) throw new IllegalStateException("Cannot get from an empty RandomCollection.");
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}
	
	/**
	 * Checks if there are no elements.
	 * @return True if collection is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
}
