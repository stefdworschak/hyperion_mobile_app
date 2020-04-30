package com.example.hyperionapp;

import javax.crypto.SecretKey;

public class EncHelper {
    private SecretKey secretKey;
    private String secretKeyString;

    public EncHelper(SecretKey secretKey, String secretKeyString) {
        this.secretKey = secretKey;
        this.secretKeyString = secretKeyString;
    }
    public EncHelper() { }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKeyString() {
        return secretKeyString;
    }

    public void setSecretKeyString(String secretKeyString) {
        this.secretKeyString = secretKeyString;
    }
}
