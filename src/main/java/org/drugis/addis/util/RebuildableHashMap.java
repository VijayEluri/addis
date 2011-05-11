package org.drugis.addis.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RebuildableHashMap<K, V> implements Map<K, V> {
	private Map<K, V> d_nested;

	public RebuildableHashMap() {
		d_nested = new HashMap<K, V>();
	}
	
	public RebuildableHashMap(Map<? extends K, ? extends V> m) {
		d_nested = new HashMap<K,V>(m);
	}
	
	public void rebuild() {
		d_nested = new HashMap<K,V>(d_nested);
	}
	
	public void clear() {
		d_nested.clear();
	}

	public boolean containsKey(Object key) {
		return d_nested.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return d_nested.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return d_nested.entrySet();
	}

	public V get(Object key) {
		return d_nested.get(key);
	}

	public boolean isEmpty() {
		return d_nested.isEmpty();
	}

	public Set<K> keySet() {
		return d_nested.keySet();
	}

	public V put(K key, V value) {
		return d_nested.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		d_nested.putAll(m);
	}

	public V remove(Object key) {
		return d_nested.remove(key);
	}

	public int size() {
		return d_nested.size();
	}

	public Collection<V> values() {
		return d_nested.values();
	}
	
	@Override
	public boolean equals(Object obj) {
		return d_nested.equals(obj);
	}

	@Override
	public int hashCode() {
		return d_nested.hashCode();
	}
}
