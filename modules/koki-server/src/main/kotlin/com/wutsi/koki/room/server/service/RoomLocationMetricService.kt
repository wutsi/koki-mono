package com.wutsi.koki.room.server.service

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.server.domain.RoomLocationMetricEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class RoomLocationMetricService(
    private val em: EntityManager,
    private val ds: DataSource,
) {
    fun search(
        tenantId: Long,
        ids: List<Long> = emptyList(),
        locationType: LocationType? = null,
        parentLocationId: Long? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<RoomLocationMetricEntity> {
        val jql = StringBuilder(
            "SELECT R FROM RoomLocationMetricEntity R WHERE R.tenantId = :tenantId"
        )

        if (ids.isNotEmpty()) {
            jql.append(" AND R.id IN :ids")
        }
        if (locationType != null) {
            jql.append(" AND R.location.type IN :locationType")
        }
        if (parentLocationId != null) {
            jql.append(" AND R.location.parentId IN :parentLocationId")
        }
        if (country != null) {
            jql.append(" AND R.location.country IN :country")
        }
        jql.append(" ORDER BY R.totalPublishedRentals DESC")

        val query = em.createQuery(jql.toString(), RoomLocationMetricEntity::class.java)
        query.setParameter("tenantId", tenantId)
        if (ids.isNotEmpty()) {
            query.setParameter("ids", ids)
        }
        if (locationType != null) {
            query.setParameter("locationType", locationType)
        }
        if (parentLocationId != null) {
            query.setParameter("parentLocationId", parentLocationId)
        }
        if (country != null) {
            query.setParameter("country", country)
        }

        query.firstResult = offset
        query.maxResults = limit
        return query.resultList
    }

    @Transactional
    fun compile() {
        compileCityStats()
        compileNeighborhoodStats()
    }

    private fun compileCityStats() {
        val sql = """
            insert into T_ROOM_LOCATION_METRIC (location_fk, tenant_fk, total_published_rentals)
                select id, tenant_fk, total_published_rentals from (
                    select L.id as id, R.tenant_fk as tenant_fk, count(*) as total_published_rentals
                    from T_ROOM R join T_LOCATION L on R.city_fk=L.id
                    where R.status=${RoomStatus.PUBLISHED.ordinal} and R.deleted=false
                    group by L.id, R.tenant_fk
                ) TMP
            on duplicate key update
                total_published_rentals = TMP.total_published_rentals
        """.trimIndent()
        execute(sql)
    }

    private fun compileNeighborhoodStats() {
        val sql = """
            insert into T_ROOM_LOCATION_METRIC (location_fk, tenant_fk, total_published_rentals)
                select id, tenant_fk, total_published_rentals from (
                    select L.id as id, R.tenant_fk as tenant_fk, count(*) as total_published_rentals
                    from T_ROOM R join T_LOCATION L on R.neighborhood_fk=L.id
                    where R.status=${RoomStatus.PUBLISHED.ordinal} and R.deleted=false
                    group by L.id, R.tenant_fk
                ) TMP
            on duplicate key update
                total_published_rentals = TMP.total_published_rentals
        """.trimIndent()
        execute(sql)
    }

    private fun execute(sql: String) {
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.prepareStatement(sql)
            stmt.use {
                stmt.execute(sql)
            }
        }
    }
}
