package com.pawlowski.trackyouractivity.gpx;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.urizev.gpx.GPXParser;
import com.urizev.gpx.beans.GPX;
import com.urizev.gpx.beans.Route;
import com.urizev.gpx.beans.Waypoint;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


public class GPXUseCase {

    private final File mFilesDir;

    public GPXUseCase(File filesDir)
    {
        mFilesDir = filesDir;
    }

    public void writeToGpx(ArrayList<Waypoint> waypoints, String filename)
    {
        Log.d("proba", waypoints.size()+"");
        GPX gpx = new GPX();
        Route route = new Route();
        route.setRoutePoints(waypoints);
        gpx.addRoute(route);
        try {
            OutputStream out;
            //out = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory(), filename));
            out = new FileOutputStream(new File(mFilesDir.getAbsolutePath() + File.separator, filename));
            GPXParser parser = new GPXParser();
            parser.writeGPX(gpx, out);

        } catch (FileNotFoundException | TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
            Log.d("Blad", "Nie udalo sie zapisac");
        }
    }

    public List<Waypoint> readFromGpx(String fileName)
    {

        GPXParser parser = new GPXParser();
        try {
            File file = new File(mFilesDir.getAbsolutePath() + File.separator, fileName);
            GPX gpx = parser.parseGPX(new FileInputStream(file));
            HashSet<Route> routes = gpx.getRoutes();
            Log.d("Blad", "Nie udalo sie zapisac");
            if(routes.size() > 0)
            {
                return new ArrayList<>(routes).get(0).getRoutePoints();
            }
            else
                return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
