package com.cloud.cloudphotos.data;

public class Photo {
    private long id;
    private String path;
    private String datestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    @Override
    public String toString() {
        return path;
    }
}