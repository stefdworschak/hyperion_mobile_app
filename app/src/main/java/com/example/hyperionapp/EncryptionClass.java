package com.example.hyperionapp;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptionClass {


    public KeyPair generateKeys(){
        KeyPair kp;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.genKeyPair();
            PublicKey publicKey = kp.getPublic();
            PrivateKey privateKey = kp.getPrivate();
        } catch(NoSuchAlgorithmException e){
            kp = null;
        }
        return kp;
    }

    public byte[] encryptText(String text, PrivateKey privateKey){
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encryptedBytes = cipher.doFinal(text.getBytes());
        } catch(NoSuchAlgorithmException e){
            encryptedBytes = null;
        } catch(InvalidKeyException eKey){
            encryptedBytes = null;
        } catch(NoSuchPaddingException ePadding){
            encryptedBytes = null;
        } catch(BadPaddingException eBadPadding){
            encryptedBytes = null;
        } catch(IllegalBlockSizeException eBadPadding){
            encryptedBytes = null;
        }
        return encryptedBytes;
    }

}
