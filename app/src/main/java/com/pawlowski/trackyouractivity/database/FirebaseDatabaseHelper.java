package com.pawlowski.trackyouractivity.database;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class FirebaseDatabaseHelper {



    public FirebaseDatabaseHelper()
    {

    }


    public Task<Void> addUserInfo(String accountKey, String name, String dateOfBirth, int goal, int weight)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("u/"+accountKey);
        Map<String, Object> values = new HashMap<>();
        values.put("n", name);
        values.put("w", weight);
        values.put("b", dateOfBirth);
        values.put("g", goal);
        return reference.updateChildren(values);
    }

    public String createNewEmptyTraining(String accountKey)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("t/"+accountKey);
        String key = reference.push().getKey();
        assert key != null;
        reference.child(key).child("t").setValue(0);
        return key;
    }

    public Task<Void> addTraining(String accountKey, String trainingKey, double distance, int kcal, long seconds, long date, int trainingType)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("t/"+accountKey+"/"+trainingKey);
        Map<String, Object> values = new HashMap<>();
        values.put("d", distance);
        values.put("k", kcal);
        values.put("t", seconds);
        values.put("w", date);
        values.put("a", trainingType);
        return reference.updateChildren(values);
    }

    public Task<TrainingModel> downloadTraining(String accountKey, String trainingKey)
    {
        TaskCompletionSource<TrainingModel> source = new TaskCompletionSource<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("t/"+accountKey+"/"+trainingKey);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                double distance = (double) Objects.requireNonNull(dataSnapshot.child("d").getValue());
                int kcal = (int) Objects.requireNonNull(dataSnapshot.child("k").getValue());
                long seconds = (long) Objects.requireNonNull(dataSnapshot.child("t").getValue());
                long date = (long) Objects.requireNonNull(dataSnapshot.child("w").getValue());
                int type = (int) Objects.requireNonNull(dataSnapshot.child("a").getValue());
                source.setResult(new TrainingModel(trainingKey, date, distance, seconds, kcal, true, type));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                source.setException(e);
            }
        });

        return source.getTask();
    }

    /*public UploadTask downloadGpxFile(String accountKey, String trainingKey)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference().child(accountKey + "/" + trainingKey+".gpx").getFile()
    }*/

    public UploadTask uploadFile(String accountKey, String trainingKey, File file)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference().child(accountKey + "/" + trainingKey+".gpx").putFile(Uri.fromFile(file));
    }
}
