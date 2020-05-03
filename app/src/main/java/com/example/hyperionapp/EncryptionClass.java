package com.example.hyperionapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
    Resources used:
    https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b

    For IV generation:
    https://stackoverflow.com/questions/46153425/aes-gcm-nopadding-aeadbadtagexception/46155266
    https://github.com/googlearchive/android-FingerprintDialog/issues/10

    Difference GCM vs CBC:
    https://crypto.stackexchange.com/questions/2310/what-is-the-difference-between-cbc-and-gcm-mode
 */

public class EncryptionClass {
    /*
     * Represents the encrypted Data for the patient
     */
    private final static String HEX = "0123456789ABCDEF";
    private final static int GCM_IV_LENGTH = 12;
    private final static int GCM_TAG_LENGTH = 16;
    private final static Gson gson = new Gson();

    public String encryptUsingPassword(String password, String data) throws Exception {
        final String characterEncoding = "UTF-8";
        final String cipherTransformation = "AES/CBC/PKCS5Padding";
        final String aesEncryptionAlgorithm = "AES";
        final String messageDigestAlgorithm = "SHA-256";
        final int ivSize = 16;
        byte[] keyBytes;
        byte[] plainMessage = data.getBytes();
        MessageDigest md = MessageDigest.getInstance(messageDigestAlgorithm);
        md.update(password.getBytes(characterEncoding));
        keyBytes = md.digest();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, aesEncryptionAlgorithm);

        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[ivSize];
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(cipherTransformation);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] inputBytes = new byte[ivBytes.length + plainMessage.length];
        System.arraycopy(ivBytes, 0, inputBytes, 0, ivBytes.length);
        System.arraycopy(plainMessage, 0, inputBytes, ivBytes.length, plainMessage.length);
        byte[] encryptedData = cipher.doFinal(inputBytes);
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }



    @TargetApi(23)
    public EncHelper generateSingleSymmetricKey(){

        KeyGenerator keyGen = null;
        EncHelper encHelper = new EncHelper();
        try {
            /*
             * Get KeyGenerator object that generates secret keys for the
             * specified algorithm.
             */
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        /* Initializes this key generator for key size to 256. */
        keyGen.init(256);

        /* Generates a secret key */
        SecretKey secretKey = keyGen.generateKey();
        encHelper.setSecretKey(secretKey);
        String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
        encHelper.setSecretKeyString(encodedKey);

        return encHelper;
    }

    @TargetApi(23)
    public SecretKey generateSymmetricKey(String alias) {
        /*
         * Create a new symmetric key to encrypt data with
         * @param alias The alias under which the symmetric key shall be stored in the keystore
         * @return SecretKey The symmetric secret key
         */
        SecretKey secretKey = null;
        try {
            secretKey = retrieveSymmetricKey(alias);
            if(secretKey == null) {
                final KeyGenerator keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        //This needs to be set to false to be able to provide a custom IV
                        .setRandomizedEncryptionRequired(false)
                        .build();

                keyGenerator.init(keyGenParameterSpec);
                secretKey = keyGenerator.generateKey();
            }

        } catch(NoSuchProviderException noProvider){
            System.out.println(noProvider.getMessage());
        } catch(NoSuchAlgorithmException noAlgorithm){
            System.out.println(noAlgorithm.getMessage());
        } catch (InvalidAlgorithmParameterException invalidAlgorithmPara){
            System.out.println(invalidAlgorithmPara.getMessage());
        }
        return secretKey;
    }

    public String encryptSecretKey(String asymm_alias, SecretKey secretKey){
        String encrypted_key = "";
        PublicKey publicKey = readPublicKey(asymm_alias);
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(secretKey.getEncoded());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch(NoSuchAlgorithmException noAlgorithm){
            noAlgorithm.printStackTrace();
        } catch(NoSuchPaddingException noPadding){
            noPadding.printStackTrace();
        }
        //catch(InvalidAlgorithmParameterException invalidParam){
        //    invalidParam.printStackTrace();
        //}
        catch(InvalidKeyException invalidKey){
            invalidKey.printStackTrace();
        }  catch(BadPaddingException badPadding){
            badPadding.printStackTrace();
        } catch(IllegalBlockSizeException illegalSize){
            illegalSize.printStackTrace();
        }
        return null;
    }

    public String encryptSymmetric(String data, String alias, SecretKey secretK){
        /* Symmetrically encrypt data to store it on the phone and retrieve later
         * @param String data The data to be encrypted, here the Patient Details
         * @param String alias The alias under which the symmetric key shall be stored in the keystore
         * @return String The encrypted data
         */
        String encryptedDataStringIV = null;
        try {
            SecretKey secretKey;
            if(secretK == null) {
                secretKey = (SecretKey) generateSymmetricKey(alias);
            } else {
                secretKey = secretK;
            }
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] iv = new byte[GCM_IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey,  ivSpec);
            byte[] ciphertext = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
            encryptedDataStringIV = Base64.encodeToString(encrypted, 0);

        } catch(NoSuchAlgorithmException noAlgorithm){
            noAlgorithm.printStackTrace();
        } catch(NoSuchPaddingException noPadding){
            noPadding.printStackTrace();
        } catch(InvalidKeyException invalidKey){
            invalidKey.printStackTrace();
        }  catch(BadPaddingException badPadding){
            badPadding.printStackTrace();
        } catch(IllegalBlockSizeException illegalSize){
            illegalSize.printStackTrace();
        }  catch(InvalidAlgorithmParameterException invalidAlgorithm){
            invalidAlgorithm.printStackTrace();
        }

        return encryptedDataStringIV;
    }

    public String decryptSymmetrically(String data, String alias){
        /*
         * Decrypt data that was encrypted by the symmetric encryption key
         * @param data String The data to be decrypted, here the Patient Details
         * @param String alias The alias under which the symmetric key shall be stored in the keystore
         * @return String The decrypted data
         */
        String decryptedDataString = null;
        //final String ivString = data.split(":")[0];
        //final String encryptedDataString = data.split(":")[1];

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            System.out.println("ALIAS");
            System.out.println(alias);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(alias, null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            byte[] encryptedData = Base64.decode(data, 0);
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            final GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);

            //final byte[] iv = Arrays.copyOfRange(encryptedData, 0, 128);
            //final IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            Log.d("ENCRYPTED DATA LEN", "" + encryptedData.length);
            byte[] decryptedData = cipher.doFinal(encryptedData, GCM_IV_LENGTH, encryptedData.length - GCM_IV_LENGTH);
            decryptedDataString = new String(decryptedData, StandardCharsets.UTF_8);
        } catch(KeyStoreException keystoreEx){
            keystoreEx.printStackTrace();
        } catch(IOException ioEx){
            ioEx.printStackTrace();
        } catch (NoSuchAlgorithmException noAlgorithm){
            noAlgorithm.printStackTrace();
        } catch(CertificateException certEx){
            certEx.printStackTrace();
        } catch(UnrecoverableEntryException unrecoverableEntry){
            unrecoverableEntry.printStackTrace();
        } catch(NoSuchPaddingException noPadding){
            noPadding.printStackTrace();
        } catch(InvalidKeyException invalidKey){
            invalidKey.printStackTrace();
        } catch(InvalidAlgorithmParameterException invalidAlgorithm){
            invalidAlgorithm.printStackTrace();
        } catch(BadPaddingException badPadding){
            badPadding.printStackTrace();
        } catch(IllegalBlockSizeException illegalSize){
            illegalSize.printStackTrace();
        } catch(NullPointerException nullpointerEx){
            nullpointerEx.printStackTrace();
        }
        return decryptedDataString;
    }


    @TargetApi(23)
    public String createAndStoreKeys(Context context, String alias){
        String pk_string = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
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

    public String readPublicKeyToString(String alias){
        String publicKey_str = "";
        try{
            KeyPair keys = null;
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
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

    public PublicKey readPublicKey(String alias){
        PublicKey pubKey = null;
        try{
            KeyPair keys = null;
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
            return publicKey;
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
        return pubKey;
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


    public String basicWrite(Context c, String mystr, String filename){
        File file = new File(c.getFilesDir(),"hyperion_data");
        if(!file.exists()){
            file.mkdir();
        }
        try {
            FileOutputStream fOut = c.openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(mystr.getBytes());
            fOut.close();

            return "File saved successfully!";
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "File NOT saved successfully!";

    }

    public String basicRead(Context c, String filename){
        try {

            FileInputStream fin = c.openFileInput(filename);
            int count;
            String temp="";

            while( (count = fin.read()) != -1){
                temp = temp + Character.toString((char)count);
            }
            return temp;
        }
        catch(Exception e){
        }
        return "ERROR READING FILE";
    }

    public String getEncryptedData(PatientDetails patientModel, Context c, String filename, SecretKey secretKey){
        String json = gson.toJson(patientModel);
        String encryptedString = encryptSymmetric(json, "", secretKey);
        return encryptedString;
    }

    public String saveData(PatientDetails patientModel, String alias, Context c, String filename){
        String json = gson.toJson(patientModel);
        System.out.println(json);
        String encryptedString = encryptSymmetric(json, alias, null);
        String written = basicWrite(c, encryptedString, filename);
        return written;
    }

    public String saveString(String saveString, String alias, Context c, String filename){
        String encryptedString = encryptSymmetric(saveString, alias, null);
        String written = basicWrite(c, encryptedString, filename);
        return written;
    }

    public SecretKey retrieveSymmetricKey(String alias){
        SecretKey secretKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if(keyStore.containsAlias(alias)) {
                final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                        .getEntry(alias, null);
                secretKey = secretKeyEntry.getSecretKey();
            }
        } catch(KeyStoreException keystoreException){
            keystoreException.printStackTrace();
        } catch(CertificateException certificateException){
            certificateException.printStackTrace();
        }  catch(UnrecoverableEntryException unrecoverableEntry){
            unrecoverableEntry.printStackTrace();
        } catch(NoSuchAlgorithmException noAlgorithm){
            noAlgorithm.printStackTrace();
        } catch(IOException IOException){
            IOException.printStackTrace();
        }
        return secretKey;
    }

}
