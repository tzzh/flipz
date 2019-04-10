import kotlin.math.pow

data class Rotation(val x: Double,val y: Double, val z: Double)

class Trick(val name: String, val rotation: Rotation){

    constructor(name: String, x: Double, y: Double, z:Double) : this(name, Rotation(x,y,z))

    fun distanceTo(r: Rotation) : Double {
        return Math.sqrt((rotation.x - r.x).pow(2) + (rotation.y - r.y).pow(2) + (rotation.z - r.z).pow(2))
    }
}
val KICKFLIP = Trick("Kickflip", 0.0, 2*Math.PI, 0.0)
val HEELFLIP = Trick("Heelflip", 0.0, -2*Math.PI, 0.0)
val SHOVE_IT = Trick("Shove it", 0.0, 0.0, Math.PI)
val FS_SHOVE_IT = Trick("FS Shove it",0.0, 0.0, -Math.PI)
val TRICKS = arrayOf(KICKFLIP, HEELFLIP, SHOVE_IT, FS_SHOVE_IT)

fun getTrick(r: Rotation) : Trick?{
    for(t in TRICKS){
        if(t.distanceTo(r) <= 1){
            return t
        }
    }
    return null
}

fun getTrickDistances(r: Rotation) : List<Pair<String, Double>> {
    return TRICKS.map{ t -> Pair(t.name, t.distanceTo(r)) }
}
