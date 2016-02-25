package com.roboboy.PraedaGrandis.Configuration;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> implements Iterable<E>
{
	private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private final Random random;
	private double total = 0;
	
	public RandomCollection() {
		this(new Random());
	}
	
	public RandomCollection(Random random) {
		this.random = random;
	}
	
	public void add(double weight, E result) {
		if (weight <= 0) throw new IllegalArgumentException("Weight must be positive.");
		total += weight;
		map.put(total, result);
	}
	
	public E next() {
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator() {
		return new RandomCollectionIterator();
	}
	
	private class RandomCollectionIterator implements Iterator<E>
	{
		Iterator<E> backingIterator = map.values().iterator();
		
		@Override
		public boolean hasNext() {
			return backingIterator.hasNext();
		}
		
		@Override
		public E next() {
			return backingIterator.next();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove from a RandomCollection.");
		}
	}
	
}
