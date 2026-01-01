package com.wutsi.koki.portal.pub

import com.wutsi.koki.place.dto.Diploma
import com.wutsi.koki.place.dto.Faith
import com.wutsi.koki.place.dto.Place
import com.wutsi.koki.place.dto.PlaceRating
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceSummary
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.RatingCriteria
import com.wutsi.koki.place.dto.SchoolLevel
import org.apache.commons.lang3.time.DateUtils
import java.util.Date

object PlaceFixtures {
    // Schools
    val place = Place(
        id = 1000,
        heroImageUrl = "https://picsum.photos/800/600",
        name = "International School of Montreal",
        type = PlaceType.NEIGHBORHOOD,
        status = PlaceStatus.PUBLISHED,
        summary = "A leading international school offering world-class education in Montreal",
        summaryFr = "Une école internationale de premier plan offrant une éducation de classe mondiale à Montréal",
        introduction = "Founded in 1965, the International School of Montreal has been providing exceptional education for over 50 years.",
        introductionFr = "Fondée en 1965, l'École internationale de Montréal offre une éducation exceptionnelle depuis plus de 50 ans.",
        description = """
            The International School of Montreal is a private, co-educational day school that offers an international
            curriculum from Pre-Kindergarten to Grade 12. The school is authorized to offer the International
            Baccalaureate (IB) Primary Years Programme (PYP), Middle Years Programme (MYP), and Diploma Programme (DP).

            Our students benefit from small class sizes, experienced teachers, and state-of-the-art facilities including
            science labs, art studios, music rooms, and a modern gymnasium. We emphasize critical thinking, creativity,
            and global citizenship.
        """.trimIndent(),
        descriptionFr = """
            L'École internationale de Montréal est une école privée mixte qui offre un programme international de la
            maternelle à la 12e année. L'école est autorisée à offrir le Programme primaire (PP), le Programme
            d'éducation intermédiaire (PEI) et le Programme du diplôme (PD) du Baccalauréat International (IB).

            Nos élèves bénéficient de petites classes, d'enseignants expérimentés et d'installations ultramodernes
            comprenant des laboratoires de sciences, des studios d'art, des salles de musique et un gymnase moderne.
            Nous mettons l'accent sur la pensée critique, la créativité et la citoyenneté mondiale.
        """.trimIndent(),
        neighbourhoodId = RefDataFixtures.neighborhoods[0].id, // Centre-Ville
        longitude = -73.5878,
        latitude = 45.5234,
        websiteUrl = "https://www.internationalmontreal.com",
        phoneNumber = "+1 514-555-1234",
        private = true,
        international = true,
        diplomas = listOf(Diploma.IB, Diploma.HIGH_SCHOOL_DIPLOMA, Diploma.BACHELOR),
        languages = listOf("en", "fr"),
        academicSystems = listOf("CA", "US", "IB"),
        levels = listOf(SchoolLevel.PRESCHOOL, SchoolLevel.PRIMARY, SchoolLevel.LOWER_SECONDARY),
        faith = null,
        rating = 4.5,
        ratingCriteria = listOf(
            PlaceRating(
                criteria = RatingCriteria.AMENITIES,
                value = 5,
                reason = "State-of-the-art facilities including science labs, art studios, and modern gymnasium"
            ),
            PlaceRating(
                criteria = RatingCriteria.INFRASTRUCTURE,
                value = 5,
                reason = "Modern building with excellent maintenance and accessibility"
            ),
            PlaceRating(
                criteria = RatingCriteria.LIFESTYLE,
                value = 4,
                reason = "Strong emphasis on arts, sports, and extracurricular activities"
            ),
            PlaceRating(
                criteria = RatingCriteria.COMMUTE,
                value = 4,
                reason = "Centrally located with easy access to public transportation"
            ),
        ),
        createdAt = DateUtils.addMonths(Date(), -6),
        modifiedAt = DateUtils.addDays(Date(), -2),
    )

    val catholicSchool = Place(
        id = 1001,
        heroImageUrl = "https://picsum.photos/800/600",
        name = "St. Mary's Catholic School",
        type = PlaceType.SCHOOL,
        status = PlaceStatus.PUBLISHED,
        summary = "A faith-based Catholic school providing quality education in a nurturing environment",
        summaryFr = "Une école catholique offrant une éducation de qualité dans un environnement bienveillant",
        neighbourhoodId = RefDataFixtures.neighborhoods[1].id, // Ahunsic
        longitude = -73.7150,
        latitude = 45.5320,
        websiteUrl = "https://www.stmarys-school.ca",
        phoneNumber = "+1 514-555-5678",
        private = false,
        international = false,
        diplomas = listOf(Diploma.HIGH_SCHOOL_DIPLOMA),
        languages = listOf("en", "fr"),
        academicSystems = listOf("CM"),
        faith = Faith.CATHOLIC,
        levels = listOf(SchoolLevel.PRESCHOOL, SchoolLevel.PRIMARY),
        rating = 4.2,
        ratingCriteria = listOf(
            PlaceRating(
                criteria = RatingCriteria.LIFESTYLE,
                value = 5,
                reason = "Strong community values and character development"
            ),
            PlaceRating(
                criteria = RatingCriteria.AMENITIES,
                value = 4,
                reason = "Good facilities including chapel, sports field, and library"
            ),
        ),
        createdAt = DateUtils.addMonths(Date(), -12),
        modifiedAt = DateUtils.addDays(Date(), -10),
    )

    // Parks
    val park = Place(
        id = 2000,
        heroImageUrl = "https://picsum.photos/800/600",
        name = "Mount Royal Park",
        type = PlaceType.PARK,
        status = PlaceStatus.PUBLISHED,
        summary = "A large urban park offering stunning views, hiking trails, and recreational activities",
        summaryFr = "Un grand parc urbain offrant des vues imprenables, des sentiers de randonnée et des activités récréatives",
        introduction = "Mount Royal Park is Montreal's most iconic green space, designed by Frederick Law Olmsted.",
        introductionFr = "Le parc du Mont-Royal est l'espace vert le plus emblématique de Montréal, conçu par Frederick Law Olmsted.",
        description = """
            Mount Royal Park covers 470 acres and offers visitors a variety of activities year-round. In summer, enjoy
            hiking, picnicking, and outdoor concerts. In winter, the park transforms into a winter wonderland with
            cross-country skiing, snowshoeing, and ice skating at Beaver Lake.

            The park features several lookouts providing panoramic views of Montreal, including the popular Kondiaronk
            Belvedere. It's home to diverse wildlife and is a popular spot for birdwatching.
        """.trimIndent(),
        descriptionFr = """
            Le parc du Mont-Royal couvre 470 acres et offre aux visiteurs une variété d'activités toute l'année.
            En été, profitez de la randonnée, des pique-niques et des concerts en plein air. En hiver, le parc se
            transforme en paradis hivernal avec ski de fond, raquette et patinage sur le lac aux Castors.

            Le parc comprend plusieurs belvédères offrant des vues panoramiques sur Montréal, dont le populaire
            belvédère Kondiaronk. Il abrite une faune diversifiée et est un lieu prisé pour l'observation des oiseaux.
        """.trimIndent(),
        neighbourhoodId = RefDataFixtures.neighborhoods[2].id, // Mont-Royal
        longitude = -73.5878,
        latitude = 45.5088,
        websiteUrl = "https://www.lemontroyal.qc.ca",
        phoneNumber = null,
        rating = 4.8,
        ratingCriteria = listOf(
            PlaceRating(
                criteria = RatingCriteria.AMENITIES,
                value = 5,
                reason = "Extensive trails, lookouts, sports facilities, and year-round activities"
            ),
            PlaceRating(
                criteria = RatingCriteria.LIFESTYLE,
                value = 5,
                reason = "Perfect for outdoor recreation, fitness, and family activities"
            ),
            PlaceRating(
                criteria = RatingCriteria.INFRASTRUCTURE,
                value = 4,
                reason = "Well-maintained paths and facilities"
            ),
            PlaceRating(
                criteria = RatingCriteria.COMMUTE,
                value = 5,
                reason = "Easily accessible from multiple neighborhoods"
            ),
        ),
        createdAt = DateUtils.addYears(Date(), -2),
        modifiedAt = DateUtils.addDays(Date(), -1),
    )

    val communityPark = Place(
        id = 2001,
        heroImageUrl = FileFixtures.images[3].url,
        name = "Jarry Park",
        type = PlaceType.PARK,
        status = PlaceStatus.PUBLISHED,
        summary = "A vibrant community park with sports facilities, playgrounds, and green spaces",
        summaryFr = "Un parc communautaire dynamique avec des installations sportives, des aires de jeux et des espaces verts",
        neighbourhoodId = RefDataFixtures.neighborhoods[1].id, // Ahunsic
        longitude = -73.6289,
        latitude = 45.5347,
        websiteUrl = null,
        phoneNumber = null,
        rating = 4.3,
        ratingCriteria = listOf(
            PlaceRating(
                criteria = RatingCriteria.AMENITIES,
                value = 4,
                reason = "Tennis courts, baseball diamonds, soccer fields, and playground"
            ),
            PlaceRating(
                criteria = RatingCriteria.LIFESTYLE,
                value = 4,
                reason = "Great for families and sports enthusiasts"
            ),
        ),
        createdAt = DateUtils.addYears(Date(), -1),
        modifiedAt = DateUtils.addMonths(Date(), -1),
    )

    // Place Summaries for search results
    val places = listOf(place, catholicSchool, park, communityPark)

    val placeSummaries = listOf(
        PlaceSummary(
            id = place.id,
            heroImageUrl = place.heroImageUrl,
            neighbourhoodId = place.neighbourhoodId,
            type = place.type,
            name = place.name,
            summary = place.summary,
            summaryFr = place.summaryFr,
            rating = place.rating,
            status = place.status,
            academicSystems = place.academicSystems,
            diplomas = place.diplomas,
            faith = place.faith,
            levels = place.levels,
            websiteUrl = place.websiteUrl,
            international = true,
            private = true
        ),
        PlaceSummary(
            id = catholicSchool.id,
            heroImageUrl = catholicSchool.heroImageUrl,
            neighbourhoodId = catholicSchool.neighbourhoodId,
            type = catholicSchool.type,
            name = catholicSchool.name,
            summary = catholicSchool.summary,
            summaryFr = catholicSchool.summaryFr,
            rating = catholicSchool.rating,
            status = catholicSchool.status,
            academicSystems = catholicSchool.academicSystems,
            diplomas = catholicSchool.diplomas,
            faith = catholicSchool.faith,
            levels = catholicSchool.levels,
            websiteUrl = catholicSchool.websiteUrl,
        ),
        PlaceSummary(
            id = park.id,
            heroImageUrl = park.heroImageUrl,
            neighbourhoodId = park.neighbourhoodId,
            type = park.type,
            name = park.name,
            summary = park.summary,
            summaryFr = park.summaryFr,
            rating = park.rating,
            status = catholicSchool.status,
            academicSystems = catholicSchool.academicSystems,
            diplomas = catholicSchool.diplomas,
            faith = catholicSchool.faith,
            levels = catholicSchool.levels,
        ),
        PlaceSummary(
            id = communityPark.id,
            heroImageUrl = communityPark.heroImageUrl,
            neighbourhoodId = communityPark.neighbourhoodId,
            type = communityPark.type,
            name = communityPark.name,
            summary = communityPark.summary,
            summaryFr = communityPark.summaryFr,
            rating = communityPark.rating,
            status = catholicSchool.status,
            academicSystems = catholicSchool.academicSystems,
            diplomas = catholicSchool.diplomas,
            faith = catholicSchool.faith,
            levels = catholicSchool.levels,
            websiteUrl = communityPark.websiteUrl,
            international = true,
            private = true
        ),
    )
}
