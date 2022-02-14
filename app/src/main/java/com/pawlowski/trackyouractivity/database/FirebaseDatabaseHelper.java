package com.pawlowski.trackyouractivity.database;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.pawlowski.trackyouractivity.models.TrainingModel;
import com.pawlowski.trackyouractivity.models.UserModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    public Task<UserModel> getUserInfo(String accountKey)
    {
        TaskCompletionSource<UserModel> source = new TaskCompletionSource<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("u/"+accountKey);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    source.setException(new Exception("No such user"));
                else
                {
                    String name = dataSnapshot.child("n").getValue(String.class);
                    String dateOfBirth = dataSnapshot.child("b").getValue(String.class);
                    Long goal = dataSnapshot.child("g").getValue(Long.class);
                    Long weight = dataSnapshot.child("w").getValue(Long.class);
                    assert weight != null;
                    assert goal != null;
                    source.setResult(new UserModel(accountKey, name, dateOfBirth, weight.intValue(), goal.intValue()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                source.setException(e);
            }
        });
        return source.getTask();
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
        return reference.updateChildren(values).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(!task.isSuccessful())
                    return task;
                else
                    return database.getReference("k/" + accountKey + "/" + trainingKey).setValue(1);
            }
        });
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
                long kcal = (long) Objects.requireNonNull(dataSnapshot.child("k").getValue());
                long seconds = (long) Objects.requireNonNull(dataSnapshot.child("t").getValue());
                long date = (long) Objects.requireNonNull(dataSnapshot.child("w").getValue());
                long type = (long) Objects.requireNonNull(dataSnapshot.child("a").getValue());
                source.setResult(new TrainingModel(trainingKey, date, distance, seconds, (int)kcal, true, (int)type));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                source.setException(e);
            }
        });

        return source.getTask();
    }

    public Task<List<String>> getKeysToDownload(String accountKey, List<String> currentDownloadedKeys)
    {
        TaskCompletionSource<List<String>> source = new TaskCompletionSource<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("k/"+accountKey);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (!currentDownloadedKeys.contains(key))
                        keys.add(key);
                }


                source.setResult(keys);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                source.setException(e);
            }
        });

        return source.getTask();
    }

    public FileDownloadTask downloadGpxFile(String accountKey, String trainingKey, File destinationFile)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference().child(accountKey + "/" + trainingKey+".gpx").getFile(destinationFile);
    }

    public UploadTask uploadFile(String accountKey, String trainingKey, File file)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference().child(accountKey + "/" + trainingKey+".gpx").putFile(Uri.fromFile(file));
    }
}
