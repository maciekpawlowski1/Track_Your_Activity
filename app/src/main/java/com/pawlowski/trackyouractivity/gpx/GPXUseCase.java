package com.pawlowski.trackyouractivity.gpx;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.urizev.gpx.GPXParser;
import com.urizev.gpx.beans.GPX;
import com.urizev.gpx.beans.Route;
import com.urizev.gpx.beans.Waypoint;

import org.xml.sax.SAXException;

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
    private final Object LOCK = new Object();

    public GPXUseCase(File filesDir)
    {
        mFilesDir = filesDir;
    }

    public void writeToGpx(ArrayList<Waypoint> waypoints, String filename)
    {
        synchronized (LOCK)
        {
            GPX gpx = new GPX();
            Route route = new Route();
            route.setRoutePoints(waypoints);
            gpx.addRoute(route);
            try {
                OutputStream out;
                out = new FileOutputStream(new File(mFilesDir.getAbsolutePath() + File.separator, filename));
                GPXParser parser = new GPXParser();
                parser.writeGPX(gpx, out);

            } catch (FileNotFoundException | TransformerException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Waypoint> readFromGpx(String fileName)
    {
        synchronized (LOCK)
        {
            GPXParser parser = new GPXParser();
            try {
                File file = new File(mFilesDir.getAbsolutePath() + File.separator, fileName);
                if(file.exists())
                {
                    GPX gpx;
                    try
                    {
                        gpx = parser.parseGPX(new FileInputStream(file));
                    }
                    catch (SAXException e)
                    {
                        Thread.sleep(100);
                        gpx = parser.parseGPX(new FileInputStream(file));
                    }

                    HashSet<Route> routes = gpx.getRoutes();
                    if(routes.size() > 0)
                    {
                        return new ArrayList<>(routes).get(0).getRoutePoints();
                    }
                    else
                        return new ArrayList<>();
                }
                else
                {
                    return new ArrayList<>();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public Task<List<Waypoint>> readFromGpxTask(String filename)
    {
        TaskCompletionSource<List<Waypoint>> source = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                source.setResult(readFromGpx(filename));
            }
        }).start();

        return source.getTask();
    }







}
