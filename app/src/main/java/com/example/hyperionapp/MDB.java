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

public class MDB {
    private Long id;
    private StitchAppClient client = null;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection<Document> coll;
    public MDB(){
        if(client == null) {
            client = Stitch.initializeDefaultAppClient("hyperion-nhlat");
            mongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
            coll = mongoClient.getDatabase("hyperion").getCollection("hyperion_auth");
        }
    }

    public void updatePubKey(String user_id, String pub_key){
        this.client.getAuth().loginWithCredential(new AnonymousCredential()).continueWithTask(
                new Continuation<StitchUser, Task<RemoteUpdateResult>>() {
                    @Override
                    public Task<RemoteUpdateResult> then(@NonNull Task<StitchUser> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        id = System.currentTimeMillis()/1000;
                        Document filterDoc = new Document().append("owner_id", user_id);
                        final Document updateDoc = new Document(
                                "owner_id",
                                user_id
                        );
                        updateDoc.put("key", pub_key);
                        return coll.updateOne(
                                filterDoc, updateDoc, new RemoteUpdateOptions().upsert(true)
                        );
                    }
                }
        ).continueWithTask(new Continuation<RemoteUpdateResult, Task<List<Document>>>() {
            @Override
            public Task<List<Document>> then(@NonNull Task<RemoteUpdateResult> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("STITCH", "Update failed!", task.getException());
                    throw task.getException();
                }
                List<Document> docs = new ArrayList<>();
                return coll
                        .find(new Document("owner_id", user_id))
                        .limit(100)
                        .into(docs);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    Log.d("STITCH", "Found docs: " + task.getResult().toString());
                    return;
                }
                Log.e("STITCH", "Error: " + task.getException().toString());
                task.getException().printStackTrace();
            }
        });
    }
}
