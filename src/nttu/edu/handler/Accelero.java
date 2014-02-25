package nttu.edu.handler;

import nttu.edu.activities.PlayActivity;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

public class Accelero implements SensorEventListener{
	public static float X, Y, Z;
	private SensorManager manager;
	private Sensor meter;
	private boolean successful;
	private int screenRotation;
	private WindowManager windowManager;
	
	private static final int[][] AXIS = {	//ROTATION in order: 0, 90, 180, 270
		{1, -1, 0, 1}, {-1, -1, 1, 0}, {-1, 1, 0, 1}, {1, 1, 1, 0}
	};
	
	public Accelero() {
		successful = false;
	}
	
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
	
	public void onSensorChanged(SensorEvent e) {
		screenRotation = windowManager.getDefaultDisplay().getRotation(); 
		if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			final int[] axis = AXIS[screenRotation];
			X = (float) (axis[0]) * e.values[axis[2]];
			Y = (float) (axis[1]) * e.values[axis[3]];
			Z = e.values[2];
		}
	}
	
	public void resume(PlayActivity parent){
		windowManager = (WindowManager) parent.getSystemService(Activity.WINDOW_SERVICE);
		manager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) meter = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		if (!(successful = manager.registerListener(this, meter, SensorManager.SENSOR_DELAY_GAME)))
			throw new RuntimeException("Couldn't register SensorEventListener.");
	}
	
	public void pause(){
		if (successful)
			manager.unregisterListener(this, meter);
	}
}
