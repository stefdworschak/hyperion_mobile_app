package com.example.hyperionapp;

public class SessionDocument {
    private String document_name;
    private String document_hash;
    private String document_type;

    public SessionDocument(String document_name, String document_hash, String document_type) {
        this.document_name = document_name;
        this.document_hash = document_hash;
        this.document_type = document_type;
    }

    public SessionDocument() { }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_hash() {
        return document_hash;
    }

    public void setDocument_hash(String document_hash) {
        this.document_hash = document_hash;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }
}
