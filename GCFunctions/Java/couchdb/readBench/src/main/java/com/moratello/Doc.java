package com.moratello;

public class Doc {
    private String _id;
    private String _rev;
    private String content;
  
    public Doc(String id, String rev, String content) {
        this._id = id;
        this._rev = rev;
        this.content = content;
    }

    public String getId() {
        return _id;
    }

    public String getRev() {
        return _rev;
    }

    public String getContent() {
        return content;
    }

    public void setId(String id) {
        this._id = id;
    }

    public void setRev(String rev) {
        this._rev = rev;
    }

    public void setContent(String content) {
        this.content = content;
    }
  
    public String toString() {
        return "{ id: " + _id + ",\nrev: " + _rev + ",\ncontent: " + content + "\n}";
    }
}