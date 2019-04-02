import android.content.Context
import android.content.IntentSender
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.service.autofill.Validators.not
import java.util.*

data class SensorReading(val timestamp: Long, val x: Float, val y: Float, val z: Float)

class SensorList : LinkedList<SensorReading>(){

    fun add(timestamp: Long, x: Float, y: Float, z: Float): Boolean{
        val reading = SensorReading(timestamp,x,y,z)
        return super.add(reading)
    }

    fun getToArray() : Array<SensorReading> {
        return this.toTypedArray()
    }

    fun clearBefore(timestamp: Long): Boolean{
        return this.removeAll{ r -> r.timestamp <= timestamp}
    }
}

class SensorRecord(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    public val accelerometerData = SensorList()
    private val gyroscopeData = SensorList()

    private val freefallThreshold = 0.5
    private val trickLookback = 500L // how far to check to detect the trick in ms
    private val trickLookbackNs = trickLookback * 1000000L

    fun startRecording(){
        accelerometerData.clear()
        gyroscopeData.clear()
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stopRecording(){
        sensorManager.unregisterListener(this)
    }

    fun detectTrick(timestamp: Long): Triple<Float, Float, Float> {
        accelerometerData.clearBefore(timestamp - trickLookbackNs)
        gyroscopeData.clearBefore(timestamp - trickLookbackNs)

        val accelerometerCurrent = accelerometerData.getToArray()
        val gyroscopeCurrent = gyroscopeData.getToArray()
        val (freefallStartIndex, freefallEndIndex) = getFreefallIndices(accelerometerCurrent, freefallThreshold)
        val (popStartIndex, popEndIndex) = getPopIndices(accelerometerCurrent, freefallStartIndex, freefallEndIndex)
        if(popStartIndex == -1 || popEndIndex == -1){
            return Triple(0F,0F, 0F)
        }
        val popStartTs = accelerometerCurrent[popStartIndex].timestamp
        val popEndTs = accelerometerCurrent[popEndIndex].timestamp

        val rotations = computeRotations(gyroscopeCurrent, popStartTs, popEndTs)

        return rotations

    }


    override fun onSensorChanged(event: SensorEvent?) {
        val currentTs = System.nanoTime()
        when(event?.sensor?.type){
            Sensor.TYPE_ACCELEROMETER -> this.accelerometerData.add(currentTs, event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_GYROSCOPE -> this.gyroscopeData.add(currentTs, event.values[0], event.values[1], event.values[2])

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

fun getFreefallIndices(acc :Array<SensorReading>, freefallThreshold: Double): Pair<Int, Int>{
    val freefallStartIndex = acc.indexOfFirst{ r-> r.z < freefallThreshold}
    val freefallEndIndex = acc.indexOfLast{ r-> r.z < freefallThreshold }
    return Pair(freefallStartIndex, freefallEndIndex)
}

fun getPopIndices(acc: Array<SensorReading>, freefallStartIndex: Int, freefallEndIndex: Int) : Pair<Int, Int>{
    var popStart = -1
    var popEnd = -1
    if (freefallStartIndex > -1 && freefallEndIndex > -1) {
        var i = freefallStartIndex
        while (i >= 1 && popStart == -1) {
            val diff = acc[i].z - acc[i - 1].z
            if (diff >= 0) {
                popStart = i
            }
            i--
        }

        i = freefallEndIndex
        while (i < acc.size && popEnd == -1) {
            val diff = acc[i].z - acc[i - 1].z
            if (diff <= 0) {
                popEnd = i
            }
            i++
        }
    }
    return Pair(popStart, popEnd)
}

fun computeRotations(gyro: Array<SensorReading>, startTs: Long, endTs: Long) : Triple<Float, Float, Float>{
    var rotX = 0F
    var rotY = 0F
    var rotZ = 0F
    var i = 0
    var currentTs = Long.MIN_VALUE
    while(currentTs <= endTs && i < gyro.size){
        val r = gyro[i]
        val nextR = gyro[i+1]
        if(r.timestamp >= startTs){
            val delta = (nextR.timestamp - r.timestamp)/1e9F
            rotX += r.x * delta
            rotY += r.y * delta
            rotZ += r.z * delta
        }
        i += 1
        currentTs = nextR.timestamp
    }
    return Triple(rotX, rotY, rotZ)
}