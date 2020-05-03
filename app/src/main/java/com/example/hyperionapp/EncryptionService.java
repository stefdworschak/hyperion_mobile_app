package com.example.hyperionapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

public class EncryptionService {
    /* Class to handle all of the encryption/decryption logic of the application */

    // Declare and instantiate class constants
    private final static int GCM_IV_LENGTH = 12;
    private final static int GCM_TAG_LENGTH = 16;
    private final static Gson gson = new Gson();

    public EncryptionService(){
        /* Empty class constructor */
    }

    // Reference: https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b
    @TargetApi(23)
    public SecretKey generateSymmetricKey(String alias) {
        /*
         * Retrieve an existing or create a new symmetric key to encrypt data
         * @param alias The alias under which the symmetric key shall be stored in the keystore
         * @return SecretKey The symmetric secret key
         */

        // Declare and instantiate AES secret key
        // Additional info on AES: https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
        SecretKey secretKey = null;
        try {
            // Try to retrieve the existing SecretKey if the user has one already with the
            // predefined alias in the Android Keystore
            secretKey = retrieveSymmetricKey(alias);
            // If it is null
            if(secretKey == null) {
                // Create a new KeyGenerator using the Android Keystore
                final KeyGenerator keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                // Set the KeyGenParameterSpec to be used for encryption and decryption
                // Set the BlockMode to GCM
                // Additional Reading on GCM vs CDC: https://crypto.stackexchange.com/questions/2310/what-is-the-difference-between-cbc-and-gcm-mode
                // Set Padding to None
                // Set Randomized Encryption to false
                // Build KeyGenParameter Spec
                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        //This needs to be set to false to be able to provide a custom IV
                        .setRandomizedEncryptionRequired(false)
                        .build();

                // Generate SecretKey from spec
                keyGenerator.init(keyGenParameterSpec);
                secretKey = keyGenerator.generateKey();
            }
            // Catch any errors and write the Stacktrace to the log
        } catch(NoSuchProviderException noProvider){
            noProvider.printStackTrace();
        } catch(NoSuchAlgorithmException noAlgorithm){
            noAlgorithm.printStackTrace();
        } catch (InvalidAlgorithmParameterException invalidAlgorithmPara){
            invalidAlgorithmPara.printStackTrace();
        }
        // Return the retrieved or generated SecretKey
        return secretKey;
    }

    // Reference: https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b
    public SecretKey retrieveSymmetricKey(String alias){
        /*
         * Try to retrieve an existing symmetric key from the Android Keystore
         * @param alias The alias under which the symmetric key should be stored
         * @return SecretKey The symmetric secret key
         */

        // Declare and instantiate AES secret key
        // Additional info on AES: https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
        SecretKey secretKey = null;
        try {
            // Declare and instantiate the Keystore instance
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            // Load the instance
            keyStore.load(null);
            // If the alias we are looking for is in the Keystore
            if(keyStore.containsAlias(alias)) {
                // Retrieve the SecretKey from the entry
                final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                        .getEntry(alias, null);
                secretKey = secretKeyEntry.getSecretKey();
            }
            // Catch any errors and write the Stacktrace to the log
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
        // Return the retrieved SecretKey
        return secretKey;
    }

    // Reference: https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b
    public String encryptSymmetric(String data, String alias){
        /* Symmetrically encrypt data to store it on the phone and retrieve later
         * @param String data The data to be encrypted, here the Patient Details
         * @param String alias The alias under which the symmetric key shall be stored in the keystore
         * @return String The encrypted data
         */

        // Declare and instantiate return String
        String encryptedDataStringIV = null;
        try {
            // Declare and instantiate AES secret key
            // Additional info on AES: https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
            SecretKey secretKey = generateSymmetricKey(alias);
            // Declare and instantiate Cipher using the following settings: AES/GCM/NoPadding
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Reference: https://stackoverflow.com/questions/46153425/aes-gcm-nopadding-aeadbadtagexception/46155266
            // Reference: https://github.com/googlearchive/android-FingerprintDialog/issues/10
            // Create new initialization vector (IV) and GCM Parameter spec
            byte[] iv = new byte[GCM_IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);
            GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);

            // Initialize Cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,  ivSpec);
            // Encrypt data and add IV
            byte[] ciphertext = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = new byte[iv.length + ciphertext.length];

            // Convert the encrypted data from a byte[] (array) to String
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
            encryptedDataStringIV = Base64.encodeToString(encrypted, 0);
            // Catch any errors and write the Stacktrace to the log
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
        // Return the encrypted data as String
        return encryptedDataStringIV;
    }

    // Reference: https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b
    public String decryptSymmetric(String data, String alias){
        /*
         * Decrypt data that was encrypted by the symmetric encryption key
         * @param data String The data to be decrypted, here the Patient Details
         * @param String alias The alias under which the symmetric key shall be stored in the keystore
         * @return String The decrypted data
         */

        // Declare and instantiate return String
        String decryptedDataString = null;
        try {
            // Declare, instantiate and load Keystore instance
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            // Retrieve the Secret key from the Keystore entry at the provided alias
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(alias, null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            // Reference: https://stackoverflow.com/questions/46153425/aes-gcm-nopadding-aeadbadtagexception/46155266
            // Reference: https://github.com/googlearchive/android-FingerprintDialog/issues/10
            // Convert encrypted data String to byte[] (array)
            byte[] encryptedData = Base64.decode(data, 0);
            // Get IV from data String as well
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
            // Declare and instantiate Cipher using the following settings: AES/GCM/NoPadding
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            // Declare and instantiate IV spec
            final GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // Decrypt data and convert it to String
            byte[] decryptedData = cipher.doFinal(encryptedData, GCM_IV_LENGTH, encryptedData.length - GCM_IV_LENGTH);
            decryptedDataString = new String(decryptedData, StandardCharsets.UTF_8);
            // Catch any errors and write the Stacktrace to the log
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
        // Return decrypted String
        return decryptedDataString;
    }

    // Reference: https://gist.github.com/crazygit/79eaacccd84f08a692615d19d960de37#file-aescipher-java
    public String encryptUsingPassword(String password, String data) throws Exception {
        /*
         * Encrypt data with a password
         * @param String password The password to be used as SecretKey for the encryption
         * @param data String The data to be encrypted, here the Patient Details
         * @return String The encrypted data
         */

        // Declaring and instantiating method constants
        final String characterEncoding = "UTF-8";
        // Using CBC here since this was taken from a different reference and was working fine
        // TODO: Change all encryption/decryption to the same either CBC or GCM
        final String cipherTransformation = "AES/CBC/PKCS5Padding";
        final String aesEncryptionAlgorithm = "AES";
        final String messageDigestAlgorithm = "SHA-256";
        final int ivSize = 16;

        // Convert password to byte[] (array)
        byte[] bytePassword = data.getBytes();
        // Declare and instantiate MessageDigest to hash the password
        // Additional Reading: https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
        MessageDigest md = MessageDigest.getInstance(messageDigestAlgorithm);
        // Create hash from password and store it in a variable
        md.update(password.getBytes(characterEncoding));
        byte[] keyBytes = md.digest();

        // Create new SecretKey spec and new IV spec from SecureRandom
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, aesEncryptionAlgorithm);
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[ivSize];
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // Declare and instantiate Cipher
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        // Initialize Cipher
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // Encrypt data and add IV
        byte[] inputBytes = new byte[ivBytes.length + bytePassword.length];
        System.arraycopy(ivBytes, 0, inputBytes, 0, ivBytes.length);
        System.arraycopy(bytePassword, 0, inputBytes, ivBytes.length, bytePassword.length);
        byte[] encryptedData = cipher.doFinal(inputBytes);
        // Return to String converted encrypted data
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }


    public String basicWrite(Context context, String data, String filename){
        /*
         * Write encrypted data to a local file in the app INTERNAL ACCESS storage
         * @param Context context The Application Context from which the file is written
         * @param data String The encrypted data to be written to the file
         * @param String filename The filename for the local file
         * @return String The success/error message
         */

        try {
            // Reference: https://developer.android.com/training/data-storage/app-specific#internal-store-stream
            // Create new FileOutputStream and save the data in a file
            FileOutputStream fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(data.getBytes());
            fOut.close();
            return "File saved successfully!";
        }
        catch (Exception e) {
            // Write the stackTrace to the log
            e.printStackTrace();
        }
        return "File NOT saved successfully!";

    }

    public String basicRead(Context context, String filename){
        /*
         * Read encrypted data from a local file in the app INTERNAL ACCESS storage
         * @param Context context The Application Context from which the file is written
         * @param String filename The filename for the local file
         * @return String The data retrieved from the file
         */
        try {
            // Reference: https://developer.android.com/training/data-storage/app-specific#internal-access-stream
            // Create new FileOutputStream and read the data into a temporary variable to return
            FileInputStream fin = context.openFileInput(filename);
            int count;
            String temp = "";

            // Loop through the document and store data in a temporary variable
            while( (count = fin.read()) != -1){
                temp = temp + (char) count;
            }
            // Return data
            return temp;
        }
        catch(Exception e){
        }
        // Return error
        return "ERROR READING FILE";
    }

    public String saveData(PatientDetails patientModel, String alias, Context context, String filename){
        /*
         * Save viewModel data in a local file
         * @param PatientDetails patientModel The viewModel data that should be saved
         * @param String alias The alias of the SecretKey to be used for the encryption
         * @param Context context The Application Context from which the file is saved
         * @param String filename The filename for the local file
         * @return String The success/error message
         */

        // Convert the viewModel data to a JSON String
        String json = gson.toJson(patientModel);
        // Encrypt the JSON string
        String encryptedString = encryptSymmetric(json, alias);
        // Write the encrypted data to a file
        String written = basicWrite(context, encryptedString, filename);
        return written;
    }

    public String saveString(String saveString, String alias, Context context, String filename){
        /*
         * Save viewModel data in a local file
         * @param String saveString The String that should be saved
         * @param String alias The alias of the SecretKey to be used for the encryption
         * @param Context context The Application Context from which the file is saved
         * @param String filename The filename for the local file
         * @return String The success/error message
         */

        // Encrypt the JSON string
        String encryptedString = encryptSymmetric(saveString, alias);
        // Write the encrypted data to a file
        String written = basicWrite(context, encryptedString, filename);
        return written;
    }

}
