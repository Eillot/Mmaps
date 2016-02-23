package dg.shenm233.mmaps.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;

public class Compass {

    public interface OnRotateListener {
        void onRotate(float degree);
    }

    private SensorManager mSensorManager;

    private OnRotateListener mListener;

    public Compass(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerListener(@Nullable OnRotateListener onRotateListener) {
        mListener = onRotateListener;
    }

    public void start() {
        mSensorManager.registerListener(mInternalListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mSensorManager.unregisterListener(mInternalListener);
    }

    private SensorEventListener mInternalListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mListener != null)
                mListener.onRotate(-event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
