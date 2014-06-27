package com.zzw.day140620_sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends Activity {

	ImageView imageView;
	SensorManager sensorManager;
	Sensor sensor;
	SensorEventListener listener;
	float preDegree = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView1);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		listener = new SensorListeners();
		sensorManager.registerListener(listener, sensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	private final class SensorListeners implements SensorEventListener {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float degree = -event.values[0];
			Animation animation = new RotateAnimation(preDegree, degree,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setDuration(200);
			imageView.setKeepScreenOn(true);
			imageView.startAnimation(animation);

			preDegree = degree;
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	}

}
