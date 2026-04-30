package edu.generalpuzzle.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Solutions {
    private String id;
    private int res;

    public Solutions() {
        // Jackson deserialization
    }

    public Solutions(String id, int res) {
        this.id = id;
        this.res = res;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public int getRes() {
        return res;
    }
}
