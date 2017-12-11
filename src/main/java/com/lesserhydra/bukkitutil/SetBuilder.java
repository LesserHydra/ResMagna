package com.lesserhydra.bukkitutil;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class SetBuilder<E> {
	
	private final Set<E> set;
	private boolean done = false;
	
	public static <T> SetBuilder<T> init(Supplier<Set<T>> setSupplier) { return new SetBuilder<>(setSupplier.get()); }
	
	public SetBuilder<E> add(E value) {
		if (done) throw new IllegalStateException("Builder has already been used.");
		set.add(value);
		return this;
	}
	
	@SafeVarargs
	public final SetBuilder<E> add(E... values) {
		if (done) throw new IllegalStateException("Builder has already been used.");
		Collections.addAll(set, values);
		return this;
	}
	
	public Set<E> build() {
		if (done) throw new IllegalStateException("Builder has already been used.");
		done = true;
		return set;
	}
	
	public Set<E> buildImmutable() {
		if (done) throw new IllegalStateException("Builder has already been used.");
		done = true;
		return Collections.unmodifiableSet(set);
	}
	
	private SetBuilder(Set<E> set) { this.set = set; }
	
}
