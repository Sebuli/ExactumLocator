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
        accessPoints.put("fe:f5:28:c5:e4:2c", "1 606 950");
        accessPoints.put("c8:3a:35:0d:49:70", "2 1505.555555 103");
        accessPoints.put("c8:d3:a3:07:9f:0a", "2 230 1450");
        accessPoints.put("bc:f6:85:c3:7f:5a", "1 430 660");
        accessPoints.put("e0:3f:49:3f:06:1c", "1 1320 770");
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

            double widthper = centroid[0] / 2960.0;
            double heightper = centroid[1] / 2840.0;
            sym.setX((float) (picture.getMeasuredWidth() * widthper + 20));
            sym.setY((float) (picture.getHeight() * heightper + 25));

            TextView button = (TextView) findViewById(R.id.errorNotFound);
            button.setText("Floor: " + floor + "        x: " + xValue + "       y: " + yValue);
        }
    }

    public double calculateDistance(int level){
        double distance = Math.pow(10.0, (level/40.0) - 0.5);
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
