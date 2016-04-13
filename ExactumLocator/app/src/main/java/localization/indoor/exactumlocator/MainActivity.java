package localization.indoor.exactumlocator;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public class MainActivity extends Activity
{

    private HashMap<String, String> accessPoints = new HashMap<String, String>();

    private WifiManager wifi;

    private boolean buttonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inilializeAccessPoints();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                buttonPressed = !buttonPressed;
                final Timer myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Button button = (Button) findViewById(R.id.button);
                                button.setText("Stop locating");
                                if (!buttonPressed) {
                                    button.setText("Start locating");
                                    myTimer.cancel();
                                }
                                locate(v);
                            }
                        });
                    }
                }, 0, 2000);
            }
        });
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Wifi is disabled, enabling it", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }

    private void inilializeAccessPoints() {
        accessPoints.put("0:3a:99:d:ed:b0", "k 140 110 BK117");
        accessPoints.put("f4:4e:5:81:a8:50", "k 525 250 BK107");
        accessPoints.put("f4:4e:5:ad:65:80", "k 525 395 CK107");
        accessPoints.put("b0:aa:77:a8:cf:e0", "k 215 250 BK114");
        accessPoints.put("b0:aa:77:9f:11:20", "k 250 525 DK117");
        accessPoints.put("b0:aa:77:cc:ce:c0", "k 515 525 DK107");
        accessPoints.put("58:97:bd:62:3:90", "k 230 385 CK112");

        accessPoints.put("b0:aa:77:3a:69:60", "1 465 285 B114");
        accessPoints.put("b0:aa:77:a8:b5:80", "1 500 420 C116e");
        accessPoints.put("84:b8:2:e2:a1:40", "1 300 275 B120");
        accessPoints.put("b0:aa:77:9f:12:c0", "1 330 530 D122");
        accessPoints.put("58:97:bd:67:b:40", "1 525 110 A111");
        accessPoints.put("58:97:bd:6e:b0:b0", "1 35 275 B123");
        accessPoints.put("d8:b1:90:23:15:40", "1 505 545 D117e");
        accessPoints.put("d8:b1:90:3c:83:40", "1 300 395 C123");
        accessPoints.put("d8:b1:90:3c:76:0", "1 100 395 C128");
        accessPoints.put("d8:b1:90:23:23:f0", "1 120 130 A114");

        accessPoints.put("b0:aa:77:a8:bb:10", "2 45 275 B228e");
        accessPoints.put("b0:aa:77:cc:d3:e0", "2 455 405 C215e");
        accessPoints.put("b0:aa:77:a8:a0:30", "2 45 405 C229e");
        accessPoints.put("d8:b1:90:41:8a:30", "2 205 260 B222");
        accessPoints.put("d8:b1:90:41:8d:20", "2 205 390 C222");
        accessPoints.put("d8:b1:90:3c:7e:20", "2 450 275 B216e");
        accessPoints.put("d8:b1:90:23:1d:0", "2 450 535 D224e");
        accessPoints.put("d8:b1:90:34:5c:80", "2 20 535 D240e");
        accessPoints.put("d8:b1:90:23:1a:d0", "2 485 140 A221e");
        accessPoints.put("d8:b1:90:45:18:d0", "2 300 75 A210");
        accessPoints.put("ec:bd:1d:52:99:c0", "2 125 300 B225");
        accessPoints.put("f4:cf:e2:62:da:10", "2 250 535 D232");

        accessPoints.put("f4:cf:e2:66:a8:c0", "3 70 275 B327e");
        accessPoints.put("b0:aa:77:9f:14:40", "3 285 265 B322");
        accessPoints.put("d8:b1:90:45:11:30", "3 480 360 C314e");
        accessPoints.put("d8:b1:90:23:25:e0", "3 480 230 B314e");
        accessPoints.put("d8:b1:90:3c:76:90", "3 30 405 C329e");
        accessPoints.put("d8:b1:90:41:84:c0", "3 70 145 A337e");
        accessPoints.put("d8:b1:90:26:7e:b0", "3 350 60 A311");
        accessPoints.put("d8:b1:90:3c:76:70", "3 440 100 A318");
        accessPoints.put("d8:b1:90:26:70:30", "3 455 495 D320e");
        accessPoints.put("d8:b1:90:41:82:30", "3 350 140 A327e");
        accessPoints.put("d8:b1:90:3c:7e:80", "3 305 540 D329e");
        accessPoints.put("d8:b1:90:34:4d:40", "3 225 100 A347");
        accessPoints.put("d8:b1:90:23:1f:c0", "3 175 535 D334e");
        accessPoints.put("d8:b1:90:23:1d:60", "3 70 490 D342e");
        accessPoints.put("84:b8:2:e2:a5:90", "3 330 400 C321");

        accessPoints.put("d8:b1:90:34:5d:70", "4 290 540 D430");
        accessPoints.put("ec:bd:1d:51:ac:20", "4 385 230 B413e");
        accessPoints.put("ec:bd:1d:33:56:30", "4 385 100 A413");
        accessPoints.put("ec:bd:1d:6a:f0:70", "4 225 145 A423");
        accessPoints.put("ec:bd:1d:61:11:10", "4 290 360 C408");
        accessPoints.put("ec:bd:1d:41:7a:a0", "4 440 490 D419e");
        accessPoints.put("ec:bd:1d:3d:84:60", "4 440 410 C421e");

    }

    public void locate(View view) {
        wifi.startScan();

        ArrayList<Double> distances = new ArrayList<>();
        ArrayList<String> positions = new ArrayList<>();
        ArrayList<Integer> floors = new ArrayList<>();

        ImageView picture = (ImageView) findViewById(R.id.imageView);
        long[][] testData = new long[wifi.getScanResults().size()][2];
        int k = 0;
        for (ScanResult result : wifi.getScanResults()){
            for (String accessPoint : accessPoints.keySet()){
                if (result.BSSID.equals(accessPoint)){
                    double distance = calculateDistance(Math.abs(result.level));
                    distances.add(distance);
                    String[] position = accessPoints.get(accessPoint).split((" "));
                    positions.add(position[1] + " " + position[2]);
                    floors.add(Integer.valueOf(position[0]));
                }
            }
            testData[k][1] = result.level;
            String trimmed = result.BSSID.replace(":", "");
            testData[k][0] = Long.parseLong(trimmed, 16);
            k++;
        }
        if (positions.size() >= 2){
            double[][] pos = new double[positions.size()][2];
            double[] dist = new double[positions.size()];
            for(int i = 0; i < positions.size(); i++){
                String[] currentPos = positions.get(i).split(" ");
                pos[i][0] = Double.parseDouble(currentPos[0]);
                pos[i][1] = Double.parseDouble(currentPos[1]);
                dist[i] = distances.get(i);
            }

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(pos, dist), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            double[] centroid = optimum.getPoint().toArray();

            String xValue = String.format("%.0f", centroid[0]);
            String yValue = String.format("%.0f", centroid[1]);

            double widthper = centroid[0] / 592.0;
            double heightper = centroid[1] / 568.0;
            ImageView symblue = (ImageView) findViewById(R.id.pointerblue);
            symblue.setX((float) (picture.getMeasuredWidth() * widthper + 20));
            symblue.setY((float) (picture.getHeight() * heightper + 25));

            TextView button = (TextView) findViewById(R.id.errorNotFound);
            button.setText("Triliteration:     x: " + xValue + " y: " + yValue);
        }

        double[][] trainingData = getTrainingData();

        double[] position = getPosition(trainingData, testData);

        double floor = position[0];
        if (floor == 1.0){
            picture.setImageResource(R.drawable.exactum1small);
        } else if (floor == 2.0){
            picture.setImageResource(R.drawable.exactum2small);
        } else if (floor == 3.0) {
            picture.setImageResource(R.drawable.exactum3small);
        } else if (floor == 4.0) {
            picture.setImageResource(R.drawable.exactum4small);
        } else if (floor == 0.0){
            picture.setImageResource(R.drawable.exactumksmall);
        }

        if(floor != -2.0) {
            String xValue = String.format("%.0f", position[1]);
            String yValue = String.format("%.0f", position[2]);

            double widthper = position[1] / 592.0;
            double heightper = position[2] / 568.0;
            ImageView symgreen = (ImageView) findViewById(R.id.pointergreen);
            symgreen.setX((float) (picture.getMeasuredWidth() * widthper + 20));
            symgreen.setY((float) (picture.getHeight() * heightper + 25));

            TextView button = (TextView) findViewById(R.id.textView2);
            button.setText("Floor       " + String.format("%.0f", floor));
            button = (TextView) findViewById(R.id.textView);
            button.setText("Fingerprinting: x: " + xValue + " y: " + yValue);
        }
    }

    private double[] getPosition(double[][] trainingData, long[][] testData) {
        List<Double> positionsX = new ArrayList<>();
        List<Double> positionsY = new ArrayList<>();
        List<Double> floors = new ArrayList<>();

        for (int k = 0; k < testData.length - 1; k = k +2) {
            double smallest = 10000;
            int smallestI = -1;
            for (int i = 0; i < trainingData.length - 1; i++) {
                for (int t = 3; t < trainingData[i].length - 1; t = t + 2) {
                    if (trainingData[i][t] == testData[k][0]) {
                        double distance = Math.abs(testData[k][1] - trainingData[i][t + 1]);
                        if (distance < smallest) {
                            smallest = distance;
                            smallestI = i;
                        }
                    }
                }
                if (smallestI != -1) {
                    int weight = (int)(20 - smallest);
                    if(weight > 0) {
                        for (int l = 0; l < weight; l++) {
                            positionsX.add(trainingData[smallestI][1]);
                            positionsY.add(trainingData[smallestI][2]);
                            floors.add(trainingData[smallestI][0]);
                        }
                    } else {
                        positionsX.add(trainingData[smallestI][1]);
                        positionsY.add(trainingData[smallestI][2]);
                        floors.add(trainingData[smallestI][0]);
                    }
                } else {
                    positionsX.add(-1.0);
                    positionsY.add(-1.0);
                    floors.add(-2.0);
                }
            }
        }
        double floor = getFloor(floors);
        double sumX = 0.0;
        double sumY = 0.0;
        int count = 0;
        for (double i : positionsX){
            if (i != -1){
                count++;
                sumX += i;
            }
        }
        for (double i : positionsY){
            if (i != -1){
                sumY += i;
            }
        }
        if (count == 0){
            count++;
        }
        double x = sumX / count;
        double y = sumY / count;
        double[] result = {floor, x, y};
        return result;
    }
    private long[][] getTestData() {
        BufferedReader in;
        String line;
        List<String> data = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("testData.txt");
            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
            while((line = in.readLine()) != null)
            {
                data.add(line);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long[][] fingerprints = new long[data.size()][2];

        for (int i = 0; i < data.size(); i++){
            String[] fingerprint = data.get(i).split(";");
            for (int t = 0; t < 2; t++) {
                fingerprints[i][t] = Long.parseLong(fingerprint[t]);
            }
        }
        return fingerprints;
    }


    private double[][] getTrainingData() {
        BufferedReader in;
        String line;
        List<String> data = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("trainingData.txt");
            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
            while((line = in.readLine()) != null)
            {
                data.add(line);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] fingerprints = new double[data.size()][1000];

        for (int i = 0; i < data.size(); i++){
            String[] fingerprint = data.get(i).split(";");
            for (int t = 0; t < fingerprint.length; t++) {
                fingerprints[i][t] = Double.parseDouble(fingerprint[t]);
            }
        }
        return fingerprints;
    }


    public double calculateDistance(int level){
        double distance = 7*Math.pow(10.0, (level/40.0) - 0.5);
        return distance;
    }

    public double getFloor(List<Double> floors){
        HashMap<Double, Integer> count = new HashMap<>();

        for(double floor : floors){
            if (count.containsKey(floor) && floor != -2.0){
                count.put(floor,count.get(floor) + 1);
            } else if (floor != -2.0){
                count.put(floor, 1 );
            }
        }
        int most = 0;
        double currentFloor = -2;
        for (double floor : count.keySet()){
            if (count.get(floor) > most){
                most = count.get(floor);
                currentFloor = floor;
            }
        }

        return currentFloor;
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        System.out.print(val);
        return val;
    }
}
