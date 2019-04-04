import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import java.util.concurrent.LinkedBlockingQueue


data class SensorReading(val timestamp: Long, val sensorType: Int, val x: Float, val y: Float, val z: Float)

class SensorRecord(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val sensorData = LinkedBlockingQueue<SensorReading>() //oldest element is first, most recent last
    private val previousReadings = mutableListOf<SensorReading>()

    private val freefallThreshold = 0.5


    fun startRecording(){
        sensorData.clear()
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stopRecording(){
        sensorManager.unregisterListener(this)
    }

    fun detectTrick(): Rotation {
        // copy values of previous readings in the current readings to be processed
        val currentReadings = mutableListOf<SensorReading>()
        for(r in previousReadings){
            currentReadings.add(SensorReading(r.timestamp, r.sensorType, r.x, r.y, r.z))
        }

        //update previous readings
        previousReadings.clear()
        sensorData.drainTo(previousReadings)

        //add them to readings to be processed
        currentReadings.addAll(previousReadings)

        val accelerometerCurrent = currentReadings.filter{r -> r.sensorType == Sensor.TYPE_ACCELEROMETER} .toTypedArray()
        val gyroscopeCurrent = currentReadings.filter{ r-> r.sensorType == Sensor.TYPE_GYROSCOPE} .toTypedArray()
        val (freefallStartIndex, freefallEndIndex) = getFreefallIndices(accelerometerCurrent, freefallThreshold)
        val (popStartIndex, popEndIndex) = getPopIndices(accelerometerCurrent, freefallStartIndex, freefallEndIndex)
        if(popStartIndex == -1 || popEndIndex == -1){
            return Rotation(0.0,0.0, 0.0)
        }
        val popStartTs = accelerometerCurrent[popStartIndex].timestamp
        val popEndTs = accelerometerCurrent[popEndIndex].timestamp

        return computeRotations(gyroscopeCurrent, popStartTs, popEndTs)

    }


    override fun onSensorChanged(event: SensorEvent?) {
        val currentTs = System.nanoTime()
        if(event !== null)
            this.sensorData.offer(SensorReading(currentTs, event.sensor.type, event.values[0], event.values[1], event.values[2]))
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
    if (freefallStartIndex > -1 && freefallEndIndex > 0) {
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

fun computeRotations(gyro: Array<SensorReading>, startTs: Long, endTs: Long) : Rotation{
    var rotX = 0.0
    var rotY = 0.0
    var rotZ = 0.0
    var i = 0
    var currentTs = Long.MIN_VALUE
    while(currentTs <= endTs && i < gyro.size-1){
        val r = gyro[i]
        val nextR = gyro[i+1]
        if(r.timestamp >= startTs){
            val delta = (nextR.timestamp - r.timestamp)/1e9
            rotX += r.x * delta
            rotY += r.y * delta
            rotZ += r.z * delta
        }
        i += 1
        currentTs = nextR.timestamp
    }
    return Rotation(rotX, rotY, rotZ)
}