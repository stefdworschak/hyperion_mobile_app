package com.example.hyperionapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

public class EncryptionClass {

    @TargetApi(23)
    public String createAndStoreKeys(Context context){
        String pk_string = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            System.out.println("KEYSTORE");
            System.out.println(keyStore);
            int c = 0;
            /*while(keyStore.aliases().hasMoreElements()){
                c++;
                keyStore.aliases().nextElement();
            }*/
            System.out.println("ALIASCOUNT");
            System.out.println(c);
            String alias = "hyperion";
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    //.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            generator.initialize(spec);
            KeyPair kp = generator.generateKeyPair();
            PublicKey publicKey = kp.getPublic();
            System.out.println("PUBLIC KEY: " + publicKey.getEncoded());
            pk_string = new String(Base64.encode(publicKey.getEncoded(), 0));
        } catch(KeyStoreException keyStoreException){
            System.out.println("EXCEPTION1: " + keyStoreException.getMessage());
        } catch(NoSuchAlgorithmException noAlgorithm){
            System.out.println("EXCEPTION2: " + noAlgorithm.getMessage());
        } catch(NoSuchProviderException noProvider){
            System.out.println("EXCEPTION3: " + noProvider.getMessage());
        } catch(InvalidAlgorithmParameterException invalidParameter){
            System.out.println("EXCEPTION4: " + invalidParameter.getMessage());
        } catch(Exception e){
            System.out.println("EXCEPTION5: " + e.getMessage());
        }

        return pk_string;
    }


    public byte[] encryptText(String text, PrivateKey privateKey) {
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encryptedBytes = cipher.doFinal(text.getBytes());
        } catch (NoSuchAlgorithmException e) {
            encryptedBytes = null;
        } catch (InvalidKeyException eKey) {
            encryptedBytes = null;
        } catch (NoSuchPaddingException ePadding) {
            encryptedBytes = null;
        } catch (BadPaddingException eBadPadding) {
            encryptedBytes = null;
        } catch (IllegalBlockSizeException eBadPadding) {
            encryptedBytes = null;
        }
        return encryptedBytes;
    }



    public String readPublicKey(String alias){
        String publicKey_str = "";
        try{
            KeyPair keys = null;
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
            keys = new KeyPair(publicKey, privateKey);
            publicKey_str = new String(Base64.encode(publicKey.getEncoded(), 0));
        } catch(KeyStoreException keyStoreException){
            System.out.println("EXCEPTION1: " + keyStoreException.getMessage());
        } catch(UnrecoverableEntryException unrecoverableEntry){
            System.out.println("EXCEPTION2: " + unrecoverableEntry.getMessage());
        } catch(NoSuchAlgorithmException noAlgorithm){
            System.out.println("EXCEPTION3: " + noAlgorithm.getMessage());
        } catch(CertificateException certException){
            System.out.println("EXCEPTION4: " + certException.getMessage());
        } catch(IOException IOException){
            System.out.println("EXCEPTION5: " + IOException.getMessage());
        }
        return publicKey_str;
    }

    public PrivateKey readPrivateKey(String alias){
        PrivateKey privateKey = null;
        try{
            KeyPair keys = null;
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch(KeyStoreException keyStoreException){
            System.out.println("EXCEPTION1: " + keyStoreException.getMessage());
        } catch(UnrecoverableEntryException unrecoverableEntry){
            System.out.println("EXCEPTION2: " + unrecoverableEntry.getMessage());
        } catch(NoSuchAlgorithmException noAlgorithm){
            System.out.println("EXCEPTION3: " + noAlgorithm.getMessage());
        } catch(CertificateException certException){
            System.out.println("EXCEPTION4: " + certException.getMessage());
        } catch(IOException IOException){
            System.out.println("EXCEPTION5: " + IOException.getMessage());
        }
        return privateKey;
    }

}
