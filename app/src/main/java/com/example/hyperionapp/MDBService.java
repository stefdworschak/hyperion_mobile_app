package com.example.hyperionapp;

import android.util.Log;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MDBService {
    /* Class to handle all MongoDB interaction logic */

    // Declare class variables
    private StitchAppClient client = null;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection<Document> coll;

    // Reference: https://stitch.mongodb.com/groups/5ce9815df2a30b088ae5512d/apps/5ea59899af32d22ec9d1b556/sdks/android
    public MDBService(){
        /* Class contructor */

        // If the client is not configured yet, configure the client
        if(client == null) {
            // Load the configurations from the BuildConfig
            try {
                client = Stitch.initializeDefaultAppClient(BuildConfig.MONGODB);
                mongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
                coll = mongoClient.getDatabase(BuildConfig.MONGODB_DB).getCollection(BuildConfig.MONGODB_COL);
            } catch(Exception e){
                Log.d("MONGO Exception", e.getMessage());
            }
        }
    }

    // Reference: https://stitch.mongodb.com/groups/5ce9815df2a30b088ae5512d/apps/5ea59899af32d22ec9d1b556/sdks/android
    // Login with Credentials -> Update Record -> Find records for owner_id
    public void updateData(String user_id, String data){
        this.client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                new Continuation<StitchUser, Task<RemoteUpdateResult>>() {
                    @Override
                    public Task<RemoteUpdateResult> then(@NonNull Task<StitchUser> task) throws Exception {
                        // If the login was unsuccessful throw an error
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Otherwise
                        // Create a document as filer to find the record with the right owner_id
                        Document filterDoc = new Document().append("owner_id", user_id);
                        // Create a new document to use for the update that stores owner_id and data
                        final Document updateDoc = new Document("owner_id", user_id);
                        updateDoc.put("data", data);
                        // Update the document, upsert if record does not exist
                        return coll.updateOne(
                                filterDoc, updateDoc, new RemoteUpdateOptions().upsert(true)
                        );
                    }
                }
        ).continueWithTask(new Continuation<RemoteUpdateResult, Task<List<Document>>>() {
            @Override
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                // If the update was unsuccessful throw an error
                if (!task.isSuccessful()) {
                    // Write an error to the console as well
                    Log.e("STITCH", "Update failed!", task.getException());
                    throw task.getException();
                }
                // Create a new list of documents to store the results of the find()
                List<Document> docs = new ArrayList<>();
                // Find all documents for the specified owner_id (should only return one)
                return coll
                        .find(new Document("owner_id", user_id))
                        .limit(100)
                        .into(docs);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                // If the find() operation was successful return
                if (task.isSuccessful()) {
                    return;
                }
                // Otherwise print StackTrace
                task.getException().printStackTrace();
            }
        });
    }

    public void close(){
        this.client = null;
        this.mongoClient = null;
        this.coll = null;
    }
}
