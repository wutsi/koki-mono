package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.tracking.server.dao.TrackRepository
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.Collections

@Service
class PersisterFilter(
    private val dao: TrackRepository,
    @Value("\${koki.persister.buffer-size}") private val bufferSize: Int,
) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PersisterFilter::class.java)
    }

    private val buffer = Collections.synchronizedList(mutableListOf<TrackEntity>())

    fun size(): Int = buffer.size

    fun destroy() {
        flush()
    }

    override fun filter(track: TrackEntity): TrackEntity {
        buffer.add(track)
        if (shouldFlush()) {
            flush()
        }
        return track
    }

    fun flush(): Int {
        val copy = mutableListOf<TrackEntity>()
        copy.addAll(buffer)

        if (copy.size > 0) {
            val date = LocalDate.now(ZoneId.of("UTC"))
            val url = dao.save(date, copy)
            LOGGER.info("Storing ${copy.size} tracking events(s): $url")

            buffer.removeAll(copy)
        }

        return copy.size
    }

    private fun shouldFlush(): Boolean {
        return buffer.size >= bufferSize
    }
}
