import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class OverlappingClaimsTest {

    @Test
    fun getClaim_singleClaim() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1)
        assertEquals(expected, getFabric(Claim(1, Square(0, 0), "1x1")))
    }

    @Test
    fun getClaim_twoSquareClaim() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1)
        expected.squares[Square(0, 1)] = mutableSetOf(1)
        assertEquals(expected, getFabric(Claim(1, Square(0, 0), "1x2")))
    }

    @Test
    fun getClaim_fourSquareClaimMiddle() {
        val expected = Fabric()
        expected.squares[Square(1, 1)] = mutableSetOf(1)
        expected.squares[Square(1, 2)] = mutableSetOf(1)
        expected.squares[Square(2, 1)] = mutableSetOf(1)
        expected.squares[Square(2, 2)] = mutableSetOf(1)
        assertEquals(expected, getFabric(Claim(1, Square(1, 1), "2x2")))
    }

    @Test
    fun getClaim_eightSquareClaimMiddle() {
        val expected = Fabric()
        expected.squares[Square(2, 3)] = mutableSetOf(1)
        expected.squares[Square(2, 4)] = mutableSetOf(1)
        expected.squares[Square(2, 5)] = mutableSetOf(1)
        expected.squares[Square(2, 6)] = mutableSetOf(1)
        expected.squares[Square(3, 3)] = mutableSetOf(1)
        expected.squares[Square(3, 4)] = mutableSetOf(1)
        expected.squares[Square(3, 5)] = mutableSetOf(1)
        expected.squares[Square(3, 6)] = mutableSetOf(1)
        assertEquals(expected, getFabric(Claim(1, Square(2, 3), "2x4")))
    }

    @Test
    fun addClaim_emptyFabric_returnsFabric() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1)
        val actual = addClaim(Fabric(HashMap()), Claim(1, Square(0, 0), "1x1"))
        assertEquals(expected, actual)
    }

    @Test
    fun addClaim_simpleFabric_addsClaim() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1)
        expected.squares[Square(0, 1)] = mutableSetOf(2)
        val initialFabric = getFabric(Claim(1, Square(0, 0), "1x1"))

        val actual = addClaim(initialFabric, Claim(2, Square(0, 1), "1x1"))

        assertEquals(expected, actual)
    }

    @Test
    fun addClaim_complexFabric_addsClaim() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1)
        expected.squares[Square(0, 1)] = mutableSetOf(1)
        expected.squares[Square(1, 0)] = mutableSetOf(1)
        expected.squares[Square(1, 1)] = mutableSetOf(1, 2)
        val initialFabric = getFabric(Claim(1, Square(0, 0), "2x2"))

        val actual = addClaim(initialFabric, Claim(2, Square(1, 1), "1x1"))

        assertEquals(expected, actual)
    }

    @Test
    fun getOverlappingSquares_none_noDiff() {
        val fabric = Fabric()
        assertEquals(emptySet<Square>(), getOverlappingSquares(fabric))
    }

    @Test
    fun getOverlappingSquares_one_noDiff() {
        val fabric = Fabric()
        fabric.squares[Square(0, 0)] = mutableSetOf(1)
        assertEquals(emptySet<Square>(), getOverlappingSquares(fabric))
    }

    @Test
    fun getOverlappingSquares_two_simpleDiff() {
        val fabric = Fabric()
        fabric.squares[Square(0, 0)] = mutableSetOf(1)
        fabric.squares[Square(0, 1)] = mutableSetOf(1, 2)
        assertEquals(setOf(Square(0, 1)), getOverlappingSquares(fabric))
    }

    @Test
    fun getOverlappingSquares_two_complexDiff() {
        val fabric = Fabric()
        fabric.squares[Square(0, 0)] = mutableSetOf(1)
        fabric.squares[Square(0, 1)] = mutableSetOf(1, 2)
        fabric.squares[Square(0, 2)] = mutableSetOf(2)
        fabric.squares[Square(1, 0)] = mutableSetOf(2)
        fabric.squares[Square(1, 1)] = mutableSetOf(1, 2)
        fabric.squares[Square(1, 2)] = mutableSetOf(1, 2)
        val expected = setOf(Square(0, 1), Square(1, 1), (Square(1, 2)))
        assertTrue(squaresAreEqual(expected, getOverlappingSquares(fabric)))
    }

    @Test
    fun getOverlappingSquares_three() {
        val fabric = Fabric()
        fabric.squares[Square(0, 0)] = mutableSetOf(1)
        fabric.squares[Square(0, 1)] = mutableSetOf(1, 2, 3)
        fabric.squares[Square(0, 2)] = mutableSetOf(2)
        fabric.squares[Square(1, 0)] = mutableSetOf(2, 3)
        fabric.squares[Square(1, 1)] = mutableSetOf(1, 2)
        fabric.squares[Square(1, 2)] = mutableSetOf(1, 2)
        val expected = setOf(Square(0, 1), Square(0, 1), Square(1, 0), Square(1, 1), (Square(1, 2)))
        assertTrue(squaresAreEqual(expected, getOverlappingSquares(fabric)))
    }

    private fun squaresAreEqual(squares1: Set<Square>, squares2: Set<Square>): Boolean {
        return squares1 == squares2
    }

    @Test
    fun parseClaim() {
        assertEquals(Claim(1, Square(0, 0), "1x1"), parseClaim("#1 @ 0,0: 1x1"))
    }

    @Test
    fun parseClaim_complex() {
        assertEquals(Claim(1234, Square(12, 123), "123x321"), parseClaim("#1234 @ 12,123: 123x321"))
    }

    @Test
    fun createFabric() {
        val expected = Fabric()
        expected.squares[Square(0, 0)] = mutableSetOf(1, 2)
        expected.squares[Square(0, 1)] = mutableSetOf(1)

        val actual = createFabric("createFabric.txt")

        assertEquals(expected, actual)
   }

    @Test
    fun findOverlappingSquares_simple() {
        val expected = setOf(Square(0, 0))
        val actual = findOverlappingSquares("createFabric.txt")

        assertEquals(expected, actual)
    }

    @Test
    fun findOverlapingSquares() {
        assertEquals(104241, findOverlappingSquares("claims.txt").size)
    }

    @Test
    fun findIntactClaims_simple() {
        assertEquals(setOf(3), findIntactClaims("simpleIntactClaim.txt"))
    }

    @Test
    fun findIntactClaims() {
        assertEquals(setOf(806), findIntactClaims("claims.txt"))
    }

}