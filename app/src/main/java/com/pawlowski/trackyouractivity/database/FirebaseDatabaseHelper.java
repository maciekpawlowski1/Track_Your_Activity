package com.pawlowski.trackyouractivity.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseHelper {

    FirebaseDatabase mDatabase;

    public FirebaseDatabaseHelper()
    {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
    }


    public Task<Void> addUserInfo(String accountKey, String name, String dateOfBirth, int goal, int weight)
    {
        DatabaseReference reference = mDatabase.getReference("u/"+accountKey);
        Map<String, Object> values = new HashMap<>();
        values.put("n", name);
        values.put("w", weight);
        values.put("b", dateOfBirth);
        values.put("g", goal);
        return reference.updateChildren(values);
    }

    public String createNewEmptyTraining(String accountKey)
    {
        DatabaseReference reference = mDatabase.getReference("t/"+accountKey);
        return reference.push().getKey();
    }

    public Task<Void> addTraining(String accountKey, String trainingKey, double distance, int kcal, long seconds)
    {
        DatabaseReference reference = mDatabase.getReference("t/"+accountKey+"/"+trainingKey);
        Map<String, Object> values = new HashMap<>();
        values.put("d", distance);
        values.put("k", kcal);
        values.put("t", seconds);
        return reference.updateChildren(values);
    }
}
