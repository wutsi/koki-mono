package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SchoolLevel
import com.wutsi.koki.place.server.dao.PlaceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/place/ImportSchoolsEndpoint.sql"])
class ImportSchoolsEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: PlaceRepository

    @Test
    fun `import schools successfully`() {
        // WHEN
        val response = rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val result = response.body!!
        assertEquals(37, result.added) // 38 schools - 1 existing = 37 new
        assertEquals(1, result.updated) // 1 existing school should be updated
        assertEquals(0, result.errors)
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun `verify schools are created with correct data`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify total count
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        assertEquals(38, schools.size)

        // Verify specific school: American International School
        val aisoy = schools.find { it.name == "American International School of Yaounde (AISOY)" }
        assertNotNull(aisoy)
        assertEquals("american international school of yaounde (aisoy)", aisoy.asciiName)
        assertEquals(PlaceType.SCHOOL, aisoy.type)
        assertEquals(PlaceStatus.PUBLISHED, aisoy.status)
        assertEquals(100003L, aisoy.neighbourhoodId) // Quartier Lac
        assertEquals(6297005L, aisoy.cityId) // Yaoundé
        assertEquals(true, aisoy.private)
        assertEquals(true, aisoy.international)
        assertEquals("https://www.aisoy.org/", aisoy.websiteUrl)
        assertEquals(4.6, aisoy.rating) // Rating from CSV

        // Verify semicolon-separated lists are parsed correctly
        assertEquals(4, aisoy.levels?.size)
        assertTrue(aisoy.levels!!.contains(SchoolLevel.PRESCHOOL))
        assertTrue(aisoy.levels!!.contains(SchoolLevel.PRIMARY))
        assertTrue(aisoy.levels!!.contains(SchoolLevel.LOWER_SECONDARY))
        assertTrue(aisoy.levels!!.contains(SchoolLevel.HIGHER_SECONDARY))

        assertEquals(2, aisoy.languages?.size)
        assertTrue(aisoy.languages!!.contains("en"))
        assertTrue(aisoy.languages!!.contains("fr"))

        assertEquals(2, aisoy.academicSystems?.size)
        assertTrue(aisoy.academicSystems!!.contains("US"))
        assertTrue(aisoy.academicSystems!!.contains("IB"))

        assertEquals(2, aisoy.diplomas?.size)
        assertTrue(aisoy.diplomas!!.contains(Diploma.HIGH_SCHOOL_DIPLOMA))
        assertTrue(aisoy.diplomas!!.contains(Diploma.IB))
    }

    @Test
    fun `verify British school with IGCSE diploma`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify BIIS with IGCSE
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        val biis = schools.find { it.name == "British Isles International School (BIIS)" }

        assertNotNull(biis)
        assertEquals(100001L, biis.neighbourhoodId) // Bastos
        assertEquals(true, biis.private)
        assertEquals(true, biis.international)

        // Verify IGCSE diploma is correctly parsed (this was added to enum)
        assertTrue(biis.diplomas!!.contains(Diploma.IGCSE))
        assertTrue(biis.diplomas!!.contains(Diploma.A_LEVEL))
        assertTrue(biis.diplomas!!.contains(Diploma.IB))
    }

    @Test
    fun `verify public university`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify Université de Yaounde I
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        val uy1 = schools.find { it.name == "Université de Yaounde I" }

        assertNotNull(uy1)
        assertEquals(100004L, uy1.neighbourhoodId) // Ngoa-Ekélé
        assertEquals(false, uy1.private) // Public university
        assertEquals(false, uy1.international)

        // Verify university level
        assertEquals(1, uy1.levels?.size)
        assertTrue(uy1.levels!!.contains(SchoolLevel.UNIVERSITY))

        // Verify university diplomas
        assertEquals(3, uy1.diplomas?.size)
        assertTrue(uy1.diplomas!!.contains(Diploma.BACHELOR))
        assertTrue(uy1.diplomas!!.contains(Diploma.MASTER))
        assertTrue(uy1.diplomas!!.contains(Diploma.PHD))
    }

    @Test
    fun `verify school without website`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify Lycée General Leclerc (no website in CSV)
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        val leclerc = schools.find { it.name == "Lycée General Leclerc" }

        assertNotNull(leclerc)
        assertEquals(100004L, leclerc.neighbourhoodId) // Ngoa-Ekélé
        assertEquals(false, leclerc.private) // Public school
        assertEquals(false, leclerc.international)
        assertEquals(null, leclerc.websiteUrl) // No website
    }

    @Test
    fun `verify school ratings are imported`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify schools with ratings
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }

        // Lycée Français Fustel de Coulanges - rating 4.5
        val fustel = schools.find { it.name == "Lycée Français Fustel de Coulanges" }
        assertNotNull(fustel)
        assertEquals(4.5, fustel.rating)

        // British Isles International School - rating 4.4
        val biis = schools.find { it.name == "British Isles International School (BIIS)" }
        assertNotNull(biis)
        assertEquals(4.4, biis.rating)

        // American International School - rating 4.6
        val aisoy = schools.find { it.name == "American International School of Yaounde (AISOY)" }
        assertNotNull(aisoy)
        assertEquals(4.6, aisoy.rating)

        // Verify school without rating has null
        val leclerc = schools.find { it.name == "Lycée General Leclerc" }
        assertNotNull(leclerc)
        assertEquals(3.7, leclerc.rating)
    }

    @Test
    fun `verify existing school is updated`() {
        // GIVEN - There's already one school in the database (from SQL fixture)
        val existingSchool = dao.findById(200001L).get()
        assertEquals("https://www.old-url.org/", existingSchool.websiteUrl)
        assertEquals(false, existingSchool.private)
        assertEquals(false, existingSchool.international)

        // WHEN - Import schools
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify the school was updated with new data from CSV
        val updatedSchool = dao.findById(200001L).get()
        assertEquals("American International School of Yaounde (AISOY)", updatedSchool.name)
        assertEquals("https://www.aisoy.org/", updatedSchool.websiteUrl) // Updated
        assertEquals(true, updatedSchool.private) // Updated
        assertEquals(true, updatedSchool.international) // Updated

        // Verify levels were updated
        assertNotNull(updatedSchool.levels)
        assertEquals(4, updatedSchool.levels!!.size)

        // Verify diplomas were updated
        assertNotNull(updatedSchool.diplomas)
        assertEquals(2, updatedSchool.diplomas!!.size)
    }

    @Test
    fun `verify idempotency - second import updates instead of duplicates`() {
        // GIVEN - First import
        val firstResponse = rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)
        assertEquals(37, firstResponse.body!!.added)
        assertEquals(1, firstResponse.body!!.updated)

        // WHEN - Second import (idempotency test)
        val secondResponse = rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - No new schools added, all updated
        assertEquals(0, secondResponse.body!!.added)
        assertEquals(38, secondResponse.body!!.updated)
        assertEquals(0, secondResponse.body!!.errors)

        // Verify no duplicates in database
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        assertEquals(38, schools.size)
    }

    @Test
    fun `verify school with multiple curricula`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify Academic School of Excellence with multiple curricula
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }
        val ase = schools.find { it.name == "Academic School of Excellence (ASE)" }

        assertNotNull(ase)
        assertEquals(3, ase.academicSystems?.size)
        assertTrue(ase.academicSystems!!.contains("IB"))
        assertTrue(ase.academicSystems!!.contains("FR"))
        assertTrue(ase.academicSystems!!.contains("CM"))

        // Verify multiple diplomas
        assertEquals(3, ase.diplomas?.size)
        assertTrue(ase.diplomas!!.contains(Diploma.IB))
        assertTrue(ase.diplomas!!.contains(Diploma.BACCALAUREAT))
        assertTrue(ase.diplomas!!.contains(Diploma.CEPE))
    }

    @Test
    fun `verify all schools have required fields`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify all schools have required fields
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }

        schools.forEach { school ->
            // All schools must have these fields
            assertNotNull(school.name, "School ${school.id} missing name")
            assertNotNull(school.asciiName, "School ${school.id} missing asciiName")
            assertEquals(PlaceType.SCHOOL, school.type)
            assertEquals(PlaceStatus.PUBLISHED, school.status)
            assertTrue(school.neighbourhoodId > 0, "School ${school.name} has invalid neighbourhoodId")
            assertTrue(school.cityId > 0, "School ${school.name} has invalid cityId")
            assertEquals(false, school.deleted)

            // These should be set (not null) even if empty
            assertNotNull(school.private, "School ${school.name} missing private flag")
            assertNotNull(school.international, "School ${school.name} missing international flag")
        }
    }

    @Test
    fun `verify no AI content is generated for schools`() {
        // WHEN
        rest.getForEntity("/v1/places/import/schools", ImportResponse::class.java)

        // THEN - Verify no AI-generated content
        val schools = dao.findAll().filter { it.type == PlaceType.SCHOOL && !it.deleted }

        schools.forEach { school ->
            // Schools should NOT have AI-generated content
            assertEquals(null, school.summary, "School ${school.name} has summary (should be null)")
            assertEquals(null, school.summaryFr, "School ${school.name} has summaryFr (should be null)")
            assertEquals(null, school.introduction, "School ${school.name} has introduction (should be null)")
            assertEquals(null, school.introductionFr, "School ${school.name} has introductionFr (should be null)")
            assertEquals(null, school.description, "School ${school.name} has description (should be null)")
            assertEquals(null, school.descriptionFr, "School ${school.name} has descriptionFr (should be null)")
            // Note: rating is now imported from CSV, not AI-generated
        }
    }
}
