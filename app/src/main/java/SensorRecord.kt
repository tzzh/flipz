import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import java.util.*

data class SensorReading(val timestamp: Long, val x: Float, val y: Float, val z: Float)

class LimitedList(private val maxLength: Int) : LinkedList<SensorReading>(){
    override fun add(element : SensorReading):Boolean {
        if(this.size == this.maxLength){
            super.remove()
        }
        return super.add(element)
    }

    fun add(timestamp: Long, x: Float, y: Float, z: Float): Boolean{
        val reading = SensorReading(timestamp,x,y,z)
        return this.add(reading)
    }
}

class SensorRecord(private val context: Context) : SensorEventListener {

    private val SENSORS_READINGS = 1000

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val accelerometerData = LimitedList(SENSORS_READINGS)
    private val gyroscopeData = LimitedList(SENSORS_READINGS)

    fun startRecording(){
        accelerometerData.clear()
        gyroscopeData.clear()
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stopRecording(){
        sensorManager.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        when(event?.sensor?.type){
            Sensor.TYPE_ACCELEROMETER -> this.accelerometerData.add(event.timestamp, event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_GYROSCOPE -> this.gyroscopeData.add(event.timestamp, event.values[0], event.values[1], event.values[2])

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}