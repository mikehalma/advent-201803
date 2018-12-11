import java.io.File
import java.nio.charset.Charset

data class Claim(val claimId: Int, val position: Square, val size: String)

data class Fabric(var squares: MutableMap<Square, MutableSet<Int>> = HashMap())

data class Square(val row: Int, val col: Int)

fun getFabric(claim: Claim): Fabric {
    val fabric = Fabric()
    val upperRow = claim.size.split("x")[0].toInt() + claim.position.row
    val upperCol = claim.size.split("x")[1].toInt() + claim.position.col
    claim.position.row.until(upperRow).forEach { row ->
        claim.position.col.until(upperCol).forEach { col ->
            fabric.squares.getOrPut(Square(row, col), ::mutableSetOf) += claim.claimId
        }
    }
    return fabric
}

fun addClaim(fabric: Fabric, claim: Claim): Fabric {
    var newFabric = getFabric(claim)
    for ((position, claimIds) in newFabric.squares) {
        fabric.squares.getOrPut(position, ::mutableSetOf) += claimIds
    }
    return fabric
}

fun getOverlappingSquares(fabric: Fabric): Set<Square> {
    return fabric.squares.filterValues { it.size > 1 }.keys
}

fun parseClaim(claimString: String): Claim {
    val regex = """#(\d*) @ (\d*),(\d*): (\d*x\d*)""".toRegex()
    val result = regex.find(claimString)
    val (claimId, row, col, size) = result!!.destructured
    return Claim(claimId.toInt(), Square(row.toInt(), col.toInt()), size)
}

fun createFabric(claimFile: String): Fabric {
    val fabric = Fabric()
    loadClaims(claimFile).forEach {
        val claim = parseClaim(it)
        addClaim(fabric, claim)
    }
    return fabric
}

fun findOverlappingSquares(fileName: String): Set<Square> {
    val fabric = createFabric(fileName)
    return getOverlappingSquares(fabric)
}

fun loadClaims(fileName :String) :List<String> {
    return File(object {}.javaClass.getResource(fileName).toURI()).readLines(Charset.defaultCharset())
}

fun findIntactClaims(fileName: String): Set<Int> {
    val fabric = createFabric(fileName)
    val singleUseClaims =
        fabric.squares
            .filter { it.value.size == 1}
            .values
            .flatten()
            .toSet()
    val multiUseClaims =
        fabric.squares
            .filter { it.value.size > 1 }
            .values
            .flatten()
            .toSet()
    return singleUseClaims.minus(multiUseClaims)
}
