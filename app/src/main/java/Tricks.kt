import kotlin.math.pow

typealias Trick = Triple<Double, Double, Double>
val KICKFLIP = Trick(0.0, 2*Math.PI, 0.0)
val HEELFLIP = Trick(0.0, -2*Math.PI, 0.0)
val SHOVE_IT = Trick(0.0, 0.0, Math.PI)
val FS_SHOVE_IT = Trick(0.0, 0.0, -Math.PI)
val TRICKS = arrayOf<Trick>(KICKFLIP, HEELFLIP, SHOVE_IT, FS_SHOVE_IT)

fun getTrickDistances(trick: Trick) : Array<Double>{
    return TRICKS.map{ t -> trickDistance(trick, t)} .toTypedArray()
}

fun trickDistance(tA: Trick, tB: Trick): Double {
    return Math.sqrt((tB.first - tA.first).pow(2) + (tB.second - tA.second).pow(2) + (tB.third - tA.third).pow(2))
}