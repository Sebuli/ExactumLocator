package localization.indoor.exactumlocator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


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
        }
        if (positions.size() < 2){
            TextView button = (TextView) findViewById(R.id.errorNotFound);
            String message = "Could not find enough access points";
            button.setText(message);
        } else {
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

            String xValue = String.format("%.2f", centroid[0]);
            String yValue = String.format("%.2f", centroid[1]);

            ImageView picture = (ImageView) findViewById(R.id.imageView);
            ImageView sym = (ImageView) findViewById(R.id.pointer);
            String floor = getFloor(floors);

            if (floor.equals("1")){
                picture.setImageResource(R.drawable.exactum1small);
            } else if (floor.equals("2")){
                picture.setImageResource(R.drawable.exactum2small);
            } else if (floor.equals("3")) {
                picture.setImageResource(R.drawable.exactum3small);
            } else if (floor.equals("4")) {
                picture.setImageResource(R.drawable.exactum4small);
            } else {
                picture.setImageResource(R.drawable.exactumksmall);
            }

            double widthper = centroid[0] / 592.0;
            double heightper = centroid[1] / 568.0;
            sym.setX((float) (picture.getMeasuredWidth() * widthper + 20));
            sym.setY((float) (picture.getHeight() * heightper + 25));

            TextView button = (TextView) findViewById(R.id.errorNotFound);
            button.setText("Floor: " + floor + "        x: " + xValue + "       y: " + yValue);
        }
    }

    public double calculateDistance(int level){
        double distance = 7*Math.pow(10.0, (level/40.0) - 0.5);
        return distance;
    }

    public String getFloor(ArrayList<Integer> floors){
        String result = "";
        HashMap<Integer, Integer> count = new HashMap<>();

        for(int floor : floors){
            if (count.containsKey(floor)){
                count.put(floor,count.get(floor) + 1);
            } else {
                count.put(floor, 1);
            }
        }
        int most = 0;
        int currentFloor = -1;
        for (int floor : count.keySet()){
            if (count.get(floor) > most){
                most = count.get(floor);
                currentFloor = floor;
            }
        }

        return result + currentFloor;
    }
}
