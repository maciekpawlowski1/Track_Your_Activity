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
    private static final int DATABASE_VERSION = 6;

    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql1 = "CREATE TABLE Trainings(id INTEGER PRIMARY KEY, training_key TEXT, distance DOUBLE, time LONG, kcal INTEGER, is_finished TEXT, date LONG, training_type INTEGER, is_saved TEXT)";
        sqLiteDatabase.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Trainings");
        onCreate(sqLiteDatabase);
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
        values.put("training_key", training.getKey());
        values.put("is_saved", "false");
        Log.d("Inserting", training.getTrainingType()+"");
        db.insert("Trainings", null, values);
        db.close();
    }

    public int getWeeklyKm(long mondayStartDate, long sundayEndDate)
    {
        String selectSql = "SELECT SUM(T.distance) FROM Trainings T WHERE T.date >= " + mondayStartDate + " AND T.date <= " + sundayEndDate;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            double sum = cursor.getDouble(0);
            cursor.close();
            return (int)(sum/1000);
        }
        cursor.close();
        return 0;
    }

    public int getCurrentTrainingId()
    {

        String selectSql = "SELECT T.id FROM Trainings T WHERE T.is_finished LIKE 'false' LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public String getCurrentTrainingKey()
    {

        String selectSql = "SELECT T.training_key FROM Trainings T WHERE T.is_finished LIKE 'false' LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            String key = cursor.getString(0);
            cursor.close();
            return key;
        }
        cursor.close();
        return null;
    }

    public int getTypeOfCurrentTraining()
    {
        List<LatLng>locations = new ArrayList<>();

        String selectSql = "SELECT T.training_type FROM Trainings T WHERE T.is_finished LIKE 'false' LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(0);
            cursor.close();
            return type;
        }
        cursor.close();
        return -1;
    }

    public TrainingModel getTraining(int id)
    {

        String selectSql = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type, T.training_key FROM Trainings T WHERE T.id LIKE " + id;
        SQLiteDatabase db = this.getWritableDatabase();

        TrainingModel training = null;
        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            training = new TrainingModel(cursor.getString(6),cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
            training.setId(cursor.getInt(0));
        }
        cursor.close();
        return training;
    }

    public List<TrainingModel> getLast3Trainings()
    {
        List<TrainingModel> trainings = new ArrayList<>();

        String selectSql = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type, T.training_key FROM Trainings T WHERE T.is_finished LIKE 'true' ORDER BY T.date DESC LIMIT 3";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            do {
                TrainingModel training = new TrainingModel(cursor.getString(6), cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
                training.setId(cursor.getInt(0));
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

        String selectSql = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type, T.training_key FROM Trainings T WHERE T.is_finished LIKE 'true' ORDER BY T.date DESC";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            do {
                TrainingModel training = new TrainingModel(cursor.getString(6), cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
                training.setId(cursor.getInt(0));
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
        if(training.getKey() != null)
            values.put("training_key", training.getKey());

        if(training.getDate() != -1)
        {
            values.put("date", training.getDate());
        }

        db.update("Trainings", values, "id = ?",
                new String[]{String.valueOf(training.getId())});


    }

    public void updateTrainingAsSaved(int trainingId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_saved", true);

        db.update("Trainings", values, "id = ?",
                new String[]{String.valueOf(trainingId)});


    }

    public List<TrainingModel> getAllNotSavedTrainings()
    {
        List<TrainingModel> trainings = new ArrayList<>();

        String selectSql = "SELECT T.id, T.distance, T.time, T.kcal, T.date, T.training_type, T.training_key FROM Trainings T WHERE T.is_finished LIKE 'true' AND T.is_saved LIKE 'false'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectSql, null);
        if (cursor.moveToFirst()) {
            do {
                TrainingModel training = new TrainingModel(cursor.getString(6), cursor.getLong(4), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), false, cursor.getInt(5));
                training.setId(cursor.getInt(0));
                trainings.add(training);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return trainings;
    }
}
