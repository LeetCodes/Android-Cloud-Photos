package com.cloud.cloudphotos.helper;

import java.util.Comparator;
import java.util.Map;

public class ArrayListHashMapSort implements Comparator<Map<String, String>> {
    private final String key;

    public ArrayListHashMapSort(String key) {
        this.key = key;
    }

    @Override
    public int compare(Map<String, String> first, Map<String, String> second) {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key).toLowerCase();
        String secondValue = second.get(key).toLowerCase();
        return firstValue.compareTo(secondValue);
    }
}