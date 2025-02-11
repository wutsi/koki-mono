package com.wutsi.koki.refdata.server.service

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.dao.LocationRepository
import com.wutsi.koki.refdata.server.domain.LocationEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.text.Normalizer
import kotlin.jvm.optionals.getOrNull

@Service
class LocationService(
    private val dao: LocationRepository,
    private val em: EntityManager,
) {
    companion object {
        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    }

    fun getOrNull(id: Long): LocationEntity? {
        return dao.findById(id).getOrNull()
    }

    @Transactional
    fun save(location: LocationEntity): LocationEntity {
        return dao.save(location)
    }

    @Transactional
    fun link(parentId: Long, childId: Long): Boolean {
        val child = dao.findById(childId).getOrNull() ?: return false
        val parent = dao.findById(parentId).getOrNull()

        child.parentId = parent?.id
        dao.save(child)
        return false
    }

    fun search(
        keyword: String? = null,
        ids: List<Long> = emptyList(),
        parentId: Long? = null,
        type: LocationType? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<LocationEntity> {
        val jql = StringBuilder("SELECT L FROM LocationEntity L WHERE L.id>0")

        if (keyword != null) {
            jql.append(" AND UPPER(L.asciiName) LIKE :keyword")
        }
        if (ids.isNotEmpty()) {
            jql.append(" AND L.id IN :ids")
        }
        if (parentId != null) {
            jql.append(" AND L.parentId = :parentId")
        }
        if (type != null) {
            jql.append(" AND L.type = :type")
        }
        if (country != null) {
            jql.append(" AND L.country = :country")
        }
        jql.append(" ORDER BY L.name, L.population DESC")

        val query = em.createQuery(jql.toString(), LocationEntity::class.java)
        if (keyword != null) {
            query.setParameter("keyword", "${toAscii(keyword).uppercase()}%")
        }
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (parentId != null) {
            query.setParameter("parentId", parentId)
        }
        if (type != null) {
            query.setParameter("type", type)
        }
        if (country != null) {
            query.setParameter("country", country)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
