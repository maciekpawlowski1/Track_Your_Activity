package com.pawlowski.trackyouractivity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.pawlowski.trackyouractivity.models.TrainingModel;

import java.util.ArrayList;

import java.util.List;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TrackYourActivityDatabase";
    private static final int DATABASE_VERSION = 3;

    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql1 = "CREATE TABLE Trainings(id INTEGER PRIMARY KEY, distance DOUBLE, time LONG, kcal INTEGER, is_finished TEXT, date LONG, training_type INTEGER)";
        sqLiteDatabase.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Trainings");

    }

    public void insertTraining(TrainingModel training)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", training.getDate());
        values.put("distance", training.getDistance());
        values.put("time", training.getTime());
        values.put("kcal", training.getKcal());
        values.put("is_finished", training.isFinished()+"");
        values.put("training_type", training.getTrainingType());
        Log.d("Inserting", training.getTrainingType()+"");
        db.insert("Trainings", null, values);
        db.close();
    }

    public int getCurrentTrainingId()
    {
        List<LatLng>locations = new ArrayList<>();

        String selectKeys = "SELECT T.id FROM Trainings T WHERE T.is_finished LIKE 'false' LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectKeys, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public int getTypeOfCurrentTraining()
    {
        List<LatLng>locations = new ArrayList<>();

        String selectKeys = "SELECT T.training_type FROM Trainings T WHERE T.is_finished LIKE 'false' LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectKeys, null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(0);
            cursor.close();
            return type;
        }
        cursor.close();
        return -1;
    }

    public List<TrainingModel> getLast3Trainings()
    {
        List<TrainingModel> trainings = new ArrayList<>();

        String selectKeys = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type FROM Trainings T WHERE T.is_finished LIKE 'true' ORDER BY T.date DESC LIMIT 3";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectKeys, null);
        if (cursor.moveToFirst()) {
            do {
                TrainingModel training = new TrainingModel(cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
                trainings.add(training);
                Log.d("Getting", training.getTrainingType()+"");
            }while(cursor.moveToNext());
        }
        cursor.close();
        return trainings;
    }

    public List<TrainingModel> getAllTrainings()
    {
        List<TrainingModel> trainings = new ArrayList<>();

        String selectKeys = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type FROM Trainings T WHERE T.is_finished LIKE 'true' ORDER BY T.date";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectKeys, null);
        if (cursor.moveToFirst()) {
            do {
                TrainingModel training = new TrainingModel(cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
                trainings.add(training);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return trainings;
    }

    public void updateTraining(TrainingModel training)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("distance", training.getDistance());
        values.put("time", training.getTime());
        values.put("kcal", training.getKcal());
        values.put("is_finished", training.isFinished()+"");
        values.put("training_type", training.getTrainingType());
        Log.d("Updating", training.getTrainingType()+"");

        if(training.getDate() != -1)
        {
            values.put("date", training.getDate());
        }

        db.update("Trainings", values, "id = ?",
                new String[]{String.valueOf(training.getId())});


    }
}
