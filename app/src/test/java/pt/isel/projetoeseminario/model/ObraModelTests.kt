package pt.isel.projetoeseminario.model

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate

class ObraModelTests {

    @Test
    fun testValidObraCreation() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 12, 31)
        val obra = Obra(
            oid = 1,
            name = "Test",
            location = "test",
            description = "test",
            startDate = startDate,
            endDate = endDate,
            nfcId = "nfc123",
            status = "Completed"
        )

        assertEquals(1, obra.oid)
        assertEquals("Test", obra.name)
        assertEquals("test", obra.location)
        assertEquals("test", obra.description)
        assertEquals(startDate, obra.startDate)
        assertEquals(endDate, obra.endDate)
        assertEquals("nfc123", obra.nfcId)
        assertEquals("Completed", obra.status)
    }

    @Test
    fun testObraWithNullEndDate() {
        val startDate = LocalDate.of(2024, 1, 1)
        val obra = Obra(
            oid = 2,
            name = "Obra",
            location = "obra",
            description = "obra",
            startDate = startDate,
            nfcId = "nfc456",
            status = "Ongoing"
        )

        assertEquals(2, obra.oid)
        assertEquals("Obra", obra.name)
        assertEquals("obra", obra.location)
        assertEquals("obra", obra.description)
        assertEquals(startDate, obra.startDate)
        assertEquals(null, obra.endDate)
        assertEquals("nfc456", obra.nfcId)
        assertEquals("Ongoing", obra.status)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testObraCreationWithBlankName() {
        val startDate = LocalDate.of(2024, 1, 1)
        Obra(
            oid = 3,
            name = "",  // Blank name should cause failure
            location = "Gallery C",
            description = "An ancient artifact.",
            startDate = startDate,
            nfcId = "nfc789",
            status = "Archived"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testObraCreationWithBlankLocation() {
        val startDate = LocalDate.of(2024, 1, 1)
        Obra(
            oid = 4,
            name = "Ancient Vase",
            location = "",  // Blank location should cause failure
            description = "An ancient vase.",
            startDate = startDate,
            nfcId = "nfc101",
            status = "In Storage"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testObraCreationWithBlankDescription() {
        val startDate = LocalDate.of(2024, 1, 1)
        Obra(
            oid = 5,
            name = "Modern Art",
            location = "Gallery D",
            description = "",  // Blank description should cause failure
            startDate = startDate,
            nfcId = "nfc202",
            status = "Displayed"
        )
    }
}