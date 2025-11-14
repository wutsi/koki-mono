package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageVisitor
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.tracking.server.dao.KpiListingRepository
import com.wutsi.koki.tracking.server.dao.TrackRepository
import com.wutsi.koki.tracking.server.domain.KpiListingEntity
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.time.LocalDate
import java.util.UUID

@Service
class KpiListingGenerator(
    private val trackDao: TrackRepository,
    private val kpiDao: KpiListingRepository,
    private val storageServiceBuilder: StorageServiceBuilder,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiListingGenerator::class.java)
        private val EVENTS = listOf(
            TrackEvent.IMPRESSION,
            TrackEvent.VIEW,
            TrackEvent.CLICK,
            TrackEvent.MESSAGE,
        )
    }

    fun generate(date: LocalDate) {
        val storage = storageServiceBuilder.default()
        val tracks = this.load(date, storage)
        logger.add("track_count", tracks.size)

        val kpis = tracks.groupBy { track -> track.productId }
            .map { entry -> toKpiRoomEntity(entry.value) }
            .sortedBy { kpi -> kpi.productId }
        logger.add("kpi_count", tracks.size)

        kpiDao.save(date, kpis)
    }

    private fun load(date: LocalDate, storage: StorageService): List<TrackEntity> {
        val result: MutableList<TrackEntity> = mutableListOf()
        val visitor = object : StorageVisitor {
            override fun visit(url: URL) {
                try {
                    val tracks = load(url, storage)
                    result.addAll(tracks)
                } catch (ex: Exception) {
                    LOGGER.warn("Error while processing $url", ex)
                }
            }
        }

        val folder = trackDao.monthlyFolder(date)
        storage.visit(folder, visitor)
        return result
    }

    private fun load(url: URL, storage: StorageService): List<TrackEntity> {
        return loadTracks(url, storage)
            .filter { track -> !track.bot } // Remove both tracking
            .filter { track -> !track.productId.isNullOrEmpty() } // Has product
            .filter { track -> EVENTS.contains(track.event) }
    }

    private fun loadTracks(url: URL, storage: StorageService): List<TrackEntity> {
        // Download the data
        val file = File.createTempFile(UUID.randomUUID().toString(), ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(url, fout)
        }

        // Load the tracks
        val fin = FileInputStream(file)
        fin.use {
            return trackDao.read(fin)
        }
    }

    private fun toKpiRoomEntity(tracks: List<TrackEntity>): KpiListingEntity {
        return KpiListingEntity(
            tenantId = tracks.first().tenantId,
            productId = tracks.first().productId,
            totalImpressions = tracks.filter { track -> track.event == TrackEvent.IMPRESSION }.size.toLong(),
            totalViews = tracks.filter { track -> track.event == TrackEvent.VIEW }.size.toLong(),
            totalClicks = tracks.filter { track -> track.event == TrackEvent.CLICK }.size.toLong(),
            totalMessages = tracks.filter { track -> track.event == TrackEvent.MESSAGE }.size.toLong(),
            totalVisitors = tracks.mapNotNull { track -> track.deviceId }.toSet().size.toLong(),
        )
    }
}
