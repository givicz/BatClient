package me.BATapp.batclient.utils;

import java.util.*;

/**
 * Fast HashMap implementation for particle state caching
 * Inspired by FerriteCore's FastMap
 * Optimized for small, frequently-accessed maps
 */
public class FastMapCache<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] table;
    private int size = 0;
    private int threshold;
    
    @SuppressWarnings("unchecked")
    public FastMapCache() {
        this.table = new Node[DEFAULT_CAPACITY];
        this.threshold = (int)(DEFAULT_CAPACITY * LOAD_FACTOR);
    }
    
    @SuppressWarnings("unchecked")
    public FastMapCache(int capacity) {
        int cap = 1;
        while (cap < capacity) {
            cap <<= 1;
        }
        this.table = new Node[cap];
        this.threshold = (int)(cap * LOAD_FACTOR);
    }
    
    /**
     * Put key-value pair in cache
     */
    public V put(K key, V value) {
        if (size >= threshold) {
            resize();
        }
        
        int hash = hash(key);
        int index = hash & (table.length - 1);
        
        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            node = node.next;
        }
        
        // Insert new node
        Node<K, V> newNode = new Node<>(hash, key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
        
        return null;
    }
    
    /**
     * Get value from cache
     */
    public V get(K key) {
        int hash = hash(key);
        int index = hash & (table.length - 1);
        
        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        
        return null;
    }
    
    /**
     * Check if key exists
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    /**
     * Remove entry
     */
    public V remove(K key) {
        int hash = hash(key);
        int index = hash & (table.length - 1);
        
        Node<K, V> node = table[index];
        Node<K, V> prev = null;
        
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        
        return null;
    }
    
    /**
     * Get cache size
     */
    public int size() {
        return size;
    }
    
    /**
     * Clear all entries
     */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }
    
    /**
     * Resize hash table when threshold exceeded
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = new Node[oldTable.length << 1];
        threshold = (int)(table.length * LOAD_FACTOR);
        size = 0;
        
        for (Node<K, V> node : oldTable) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }
    
    /**
     * Hash function
     */
    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }
    
    /**
     * Internal node class
     */
    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;
        
        Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }
}
