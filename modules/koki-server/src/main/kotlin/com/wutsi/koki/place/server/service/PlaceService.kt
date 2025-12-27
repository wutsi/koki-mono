package com.wutsi.koki.place.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.Place
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceSummary
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.dao.PlaceRatingRepository
import com.wutsi.koki.place.server.dao.PlaceRepository
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.mapper.PlaceMapper
import com.wutsi.koki.security.server.service.SecurityService
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PlaceService(
    private val dao: PlaceRepository,
    private val ratingDao: PlaceRatingRepository,
    private val mapper: PlaceMapper,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    fun get(id: Long, tenantId: Long): Place {
        val entity = dao.findByIdAndTenantIdAndDeleted(id, tenantId, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }

        val ratings = ratingDao.findByPlaceId(id)
        return mapper.toPlace(entity, ratings)
    }

    fun search(
        tenantId: Long,
        neighbourhoodIds: List<Long>? = null,
        types: List<PlaceType>? = null,
        statuses: List<PlaceStatus>? = null,
        keyword: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<PlaceSummary> {
        val jql = StringBuilder("SELECT P FROM PlaceEntity P WHERE P.tenantId = :tenantId AND P.deleted=false")

        if (!neighbourhoodIds.isNullOrEmpty()) {
            jql.append(" AND P.neighbourhoodId IN :neighbourhoodIds")
        }
        if (!types.isNullOrEmpty()) {
            jql.append(" AND P.type IN :types")
        }
        if (!statuses.isNullOrEmpty()) {
            jql.append(" AND P.status IN :statuses")
        }
        if (!keyword.isNullOrBlank()) {
            jql.append(" AND (UPPER(P.name) LIKE :keyword OR UPPER(P.nameFr) LIKE :keyword)")
        }

        jql.append(" ORDER BY P.name")

        val query = em.createQuery(jql.toString(), PlaceEntity::class.java)
        query.setParameter("tenantId", tenantId)

        if (!neighbourhoodIds.isNullOrEmpty()) {
            query.setParameter("neighbourhoodIds", neighbourhoodIds)
        }
        if (!types.isNullOrEmpty()) {
            query.setParameter("types", types)
        }
        if (!statuses.isNullOrEmpty()) {
            query.setParameter("statuses", statuses)
        }
        if (!keyword.isNullOrBlank()) {
            query.setParameter("keyword", "%${keyword.uppercase()}%")
        }

        query.firstResult = offset
        query.maxResults = limit

        return query.resultList.map { mapper.toPlaceSummary(it) }
    }

    @Transactional
    fun create(request: CreatePlaceRequest, tenantId: Long): PlaceEntity {
        val userId = securityService.getCurrentUserIdOrNull()

        val entity = PlaceEntity(
            tenantId = tenantId,
            createdById = userId,
            modifiedById = userId,
            name = request.name,
            type = request.type,
            neighbourhoodId = request.neighbourhoodId,
            status = PlaceStatus.DRAFT,

            // Generate AI placeholder content
            summary = generatePlaceholderSummary(request.name, request.type),
            summaryFr = generatePlaceholderSummaryFr(request.name, request.type),
            introduction = generatePlaceholderIntroduction(request.name, request.type),
            introductionFr = generatePlaceholderIntroductionFr(request.name, request.type),
            description = generatePlaceholderDescription(request.name, request.type),
            descriptionFr = generatePlaceholderDescriptionFr(request.name, request.type),
        )

        return dao.save(entity)
    }

    @Transactional
    fun delete(id: Long, tenantId: Long) {
        val entity = dao.findByIdAndTenantIdAndDeleted(id, tenantId, false)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorCode.PLACE_NOT_FOUND,
                    ),
                )
            }

        entity.deleted = true
        entity.deletedAt = Date()
        entity.modifiedById = securityService.getCurrentUserIdOrNull()
        entity.modifiedAt = Date()
        dao.save(entity)
    }

    // AI Placeholder content generation methods
    // These will be replaced with actual AI service calls in the future

    private fun generatePlaceholderSummary(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> "Discover $name, a vibrant neighborhood with excellent amenities and community features."
            PlaceType.SCHOOL -> "$name is an educational institution providing quality education and learning opportunities."
            PlaceType.PARK -> "$name offers beautiful outdoor spaces for recreation and relaxation."
            else -> "Learn more about $name and what makes it special."
        }
    }

    private fun generatePlaceholderSummaryFr(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> "Découvrez $name, un quartier dynamique avec d'excellentes commodités et des équipements communautaires."
            PlaceType.SCHOOL -> "$name est un établissement d'enseignement offrant une éducation de qualité et des opportunités d'apprentissage."
            PlaceType.PARK -> "$name offre de beaux espaces extérieurs pour les loisirs et la détente."
            else -> "En savoir plus sur $name et ce qui le rend spécial."
        }
    }

    private fun generatePlaceholderIntroduction(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> "Welcome to $name! This thriving neighborhood combines modern convenience with a strong sense of community. Residents enjoy easy access to local amenities, excellent transportation connections, and a variety of dining and entertainment options."
            PlaceType.SCHOOL -> "$name is dedicated to providing students with a comprehensive education in a supportive environment. Our institution focuses on academic excellence, personal development, and preparing students for future success."
            PlaceType.PARK -> "$name is a cherished green space in our community. This park provides a peaceful retreat from urban life, offering various recreational facilities and natural beauty for visitors of all ages to enjoy."
            else -> "$name is a notable location worth exploring. This place offers unique features and benefits to visitors and residents alike."
        }
    }

    private fun generatePlaceholderIntroductionFr(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> "Bienvenue à $name ! Ce quartier prospère combine commodité moderne et fort sentiment de communauté. Les résidents profitent d'un accès facile aux commodités locales, d'excellentes connexions de transport et d'une variété d'options de restauration et de divertissement."
            PlaceType.SCHOOL -> "$name se consacre à offrir aux élèves une éducation complète dans un environnement favorable. Notre établissement se concentre sur l'excellence académique, le développement personnel et la préparation des étudiants au succès futur."
            PlaceType.PARK -> "$name est un espace vert précieux dans notre communauté. Ce parc offre une retraite paisible de la vie urbaine, offrant diverses installations récréatives et une beauté naturelle pour les visiteurs de tous âges."
            else -> "$name est un lieu remarquable qui mérite d'être exploré. Cet endroit offre des caractéristiques et des avantages uniques aux visiteurs et aux résidents."
        }
    }

    private fun generatePlaceholderDescription(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> """
                $name stands out as one of the area's most desirable neighborhoods. The community features well-maintained streets, diverse housing options, and a welcoming atmosphere that appeals to families, young professionals, and retirees alike.

                Local amenities include shopping centers, restaurants, cafes, and essential services all within easy reach. The neighborhood benefits from excellent public transportation links, making commuting convenient for residents. Parks and green spaces are scattered throughout, providing opportunities for outdoor activities and community gatherings.

                Safety and security are priorities in $name, with active neighborhood watch programs and responsive local services. Schools in the area maintain strong reputations for academic achievement. The community regularly hosts events that bring residents together, fostering a strong sense of belonging and neighborhood pride.
            """.trimIndent()

            PlaceType.SCHOOL -> """
                $name provides a nurturing educational environment where students can thrive academically and personally. Our experienced faculty members are committed to delivering high-quality instruction tailored to individual learning needs.

                The curriculum encompasses a broad range of subjects designed to challenge students and develop critical thinking skills. Beyond academics, we offer various extracurricular activities including sports, arts, music, and clubs that allow students to explore their interests and develop new talents.

                Our facilities include modern classrooms, well-equipped laboratories, a comprehensive library, and sports facilities. We maintain strong connections with families and the broader community, recognizing that education is a collaborative effort. Our goal is to prepare students not just for exams, but for life-long learning and success.
            """.trimIndent()

            PlaceType.PARK -> """
                $name serves as a vital green lung in our community, offering residents and visitors a chance to connect with nature without leaving the urban environment. The park features well-maintained walking paths, open lawns perfect for picnics and sports, and shaded areas ideal for relaxation.

                Facilities within the park cater to various interests and age groups. Families appreciate the playground equipment and picnic areas, while fitness enthusiasts make use of the jogging trails and outdoor exercise stations. The park also hosts community events throughout the year, from seasonal festivals to outdoor concerts.

                Environmental conservation is taken seriously at $name, with ongoing efforts to preserve native plant species and create habitats for local wildlife. The park staff maintains the grounds year-round, ensuring the space remains clean, safe, and beautiful for all to enjoy.
            """.trimIndent()

            else -> """
                $name represents an important part of our community. This location has its own unique character and offers various features that serve the needs of local residents and visitors.

                Over time, $name has become a familiar landmark in the area, known for its distinctive qualities and the role it plays in the neighborhood. Whether you're a long-time resident or new to the area, this place offers something worth discovering.

                We continue to maintain and improve this location to ensure it meets the evolving needs of our community while preserving the qualities that make it special.
            """.trimIndent()
        }
    }

    private fun generatePlaceholderDescriptionFr(name: String, type: PlaceType): String {
        return when (type) {
            PlaceType.NEIGHBORHOOD -> """
                $name se distingue comme l'un des quartiers les plus recherchés de la région. La communauté comprend des rues bien entretenues, des options de logement diversifiées et une atmosphère accueillante qui plaît aux familles, aux jeunes professionnels et aux retraités.

                Les commodités locales comprennent des centres commerciaux, des restaurants, des cafés et des services essentiels, tous facilement accessibles. Le quartier bénéficie d'excellentes liaisons de transport en commun, rendant les déplacements pratiques pour les résidents. Des parcs et des espaces verts sont dispersés partout, offrant des opportunités pour des activités de plein air et des rassemblements communautaires.

                La sûreté et la sécurité sont des priorités à $name, avec des programmes actifs de surveillance de quartier et des services locaux réactifs. Les écoles de la région maintiennent une solide réputation pour leurs réalisations académiques. La communauté organise régulièrement des événements qui rassemblent les résidents, favorisant un fort sentiment d'appartenance et de fierté de quartier.
            """.trimIndent()

            PlaceType.SCHOOL -> """
                $name offre un environnement éducatif stimulant où les élèves peuvent s'épanouir sur le plan académique et personnel. Nos membres du corps professoral expérimentés sont engagés à dispenser un enseignement de haute qualité adapté aux besoins d'apprentissage individuels.

                Le programme d'études englobe une large gamme de matières conçues pour défier les étudiants et développer leurs capacités de pensée critique. Au-delà des études, nous offrons diverses activités parascolaires, notamment des sports, des arts, de la musique et des clubs qui permettent aux élèves d'explorer leurs intérêts et de développer de nouveaux talents.

                Nos installations comprennent des salles de classe modernes, des laboratoires bien équipés, une bibliothèque complète et des installations sportives. Nous maintenons de solides liens avec les familles et la communauté au sens large, reconnaissant que l'éducation est un effort collaboratif. Notre objectif est de préparer les élèves non seulement aux examens, mais à l'apprentissage et au succès tout au long de la vie.
            """.trimIndent()

            PlaceType.PARK -> """
                $name sert de poumon vert vital dans notre communauté, offrant aux résidents et aux visiteurs une chance de se connecter avec la nature sans quitter l'environnement urbain. Le parc comprend des sentiers pédestres bien entretenus, des pelouses ouvertes parfaites pour les pique-niques et les sports, et des zones ombragées idéales pour la détente.

                Les installations du parc répondent à divers intérêts et groupes d'âge. Les familles apprécient les équipements de jeux et les aires de pique-nique, tandis que les amateurs de fitness utilisent les sentiers de jogging et les stations d'exercice en plein air. Le parc accueille également des événements communautaires tout au long de l'année, des festivals saisonniers aux concerts en plein air.

                La conservation de l'environnement est prise au sérieux à $name, avec des efforts continus pour préserver les espèces végétales indigènes et créer des habitats pour la faune locale. Le personnel du parc entretient le terrain toute l'année, garantissant que l'espace reste propre, sûr et magnifique pour tous.
            """.trimIndent()

            else -> """
                $name représente une partie importante de notre communauté. Cet endroit a son propre caractère unique et offre diverses fonctionnalités qui répondent aux besoins des résidents locaux et des visiteurs.

                Au fil du temps, $name est devenu un point de repère familier dans la région, connu pour ses qualités distinctives et le rôle qu'il joue dans le quartier. Que vous soyez un résident de longue date ou nouveau dans la région, cet endroit offre quelque chose qui vaut la peine d'être découvert.

                Nous continuons à maintenir et à améliorer cet emplacement pour nous assurer qu'il répond aux besoins évolutifs de notre communauté tout en préservant les qualités qui le rendent spécial.
            """.trimIndent()
        }
    }
}
