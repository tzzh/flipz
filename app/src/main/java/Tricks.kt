import kotlin.math.pow

data class Rotation(val x: Double,val y: Double, val z: Double)

class Trick(val name: String, val rotation: Rotation){

    constructor(name: String, x: Double, y: Double, z:Double) : this(name, Rotation(x,y,z))

    constructor(name: String, tricks: List<Trick>) : this(name, sumRotations(tricks))

    fun distanceTo(r: Rotation) : Double {
        return Math.sqrt((rotation.x - r.x).pow(2) + (rotation.y - r.y).pow(2) + (rotation.z - r.z).pow(2))
    }
}
val KICKFLIP = Trick("Kickflip", 0.0, 2*Math.PI, 0.0)
val HEELFLIP = Trick("Heelflip", 0.0, -2*Math.PI, 0.0)
val SHOVE_IT = Trick("Shove it", 0.0, 0.0, Math.PI)
val FS_SHOVE_IT = Trick("FS Shove it",0.0, 0.0, -Math.PI)

val VARIAL_FLIP = Trick("Varial Flip", listOf(SHOVE_IT, KICKFLIP))
val HARDFLIP = Trick("Hardflip", listOf(FS_SHOVE_IT, KICKFLIP))
val VARIAL_HEELFLIP = Trick("Varial Heel", listOf(FS_SHOVE_IT, HEELFLIP))
val INWARD_HEEL = Trick("Inward Heel", listOf(SHOVE_IT, HEELFLIP))
val SHOVE_IT_360 = Trick("Tre Shove", listOf(SHOVE_IT, SHOVE_IT))
val FS_SHOVE_IT_360 = Trick("FS 360 Shove", listOf(FS_SHOVE_IT, FS_SHOVE_IT))
val DOUBLE_KICKFLIP = Trick("Double Kickflip", listOf(KICKFLIP, KICKFLIP))
val DOUBLE_HEELFLIP = Trick("Double Heelflip", listOf(HEELFLIP, HEELFLIP))

val KICKFLIP_360 = Trick("Tre Flip", listOf(SHOVE_IT_360, KICKFLIP))
val LASER_FLIP = Trick("Laser Flip", listOf(FS_SHOVE_IT_360, HEELFLIP))


val TRICKS = arrayOf(KICKFLIP, HEELFLIP, SHOVE_IT, FS_SHOVE_IT,
    VARIAL_FLIP, HARDFLIP, VARIAL_HEELFLIP, INWARD_HEEL, SHOVE_IT_360, FS_SHOVE_IT_360,
    DOUBLE_KICKFLIP, DOUBLE_HEELFLIP,
    KICKFLIP_360, LASER_FLIP)

fun sumRotations(tricks: List<Trick>) : Rotation{
    return tricks.fold(Rotation(0.0,0.0,0.0)) { acc, t -> Rotation(acc.x+t.rotation.x, acc.y+t.rotation.y, acc.z+t.rotation.z) }
}

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
