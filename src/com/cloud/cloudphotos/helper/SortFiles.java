package com.cloud.cloudphotos.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SortFiles {

    /**
     * Sort files in a direcory
     * 
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public static File[] getDirectoryList(String path) {
        List<String> fileList = new ArrayList<String>();
        File f = new File(path);
        File[] files = f.listFiles();

        return files;
    }
};