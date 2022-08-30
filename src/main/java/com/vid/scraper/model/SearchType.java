package com.vid.scraper.model;

public enum SearchType {
    VIDEO("video"),
    TAG("tag");

    public final String type;

    SearchType(String type){
        this.type = type;
    }
}
