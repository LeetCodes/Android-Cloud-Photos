package com.cloud.cloudphotos.helper;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class SortFiles {

    /**
     * Sort files in a directory
     * 
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public static File[] getDirectoryList(String path) {
        File f = new File(path);
        File[] files = f.listFiles();
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        return files;
    }
};