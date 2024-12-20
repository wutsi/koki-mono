package com.wutsi.koki.workflow.server.io

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.util.mxCellRenderer
import com.mxgraph.util.mxConstants
import com.wutsi.koki.workflow.dto.ActivityData
import com.wutsi.koki.workflow.dto.ActivityType
import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.io.WorkflowPNGExporter.Companion.SUFFIX_DONE
import com.wutsi.koki.workflow.server.io.WorkflowPNGExporter.Companion.SUFFIX_RUNNING
import com.wutsi.koki.workflow.server.util.JGraphtUtil
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.DefaultEdge
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.OutputStream
import javax.imageio.ImageIO
import javax.swing.SwingConstants
import kotlin.to

@Service
class WorkflowPNGExporter {
    companion object {
        const val SUFFIX_DONE = "done"
        const val SUFFIX_RUNNING = "running"

        const val STYLE_EDGE = "edge"
        const val STYLE_START = "start"
        const val STYLE_START_RUNNING = "${STYLE_START}_$SUFFIX_RUNNING"
        const val STYLE_START_DONE = "${STYLE_START}_$SUFFIX_DONE"
        const val STYLE_STOP = "stop"
        const val STYLE_STOP_RUNNING = "${STYLE_STOP}_$SUFFIX_RUNNING"
        const val STYLE_STOP_DONE = "${STYLE_STOP}_$SUFFIX_DONE"
        const val STYLE_ACTIVITY = "activity"
        const val STYLE_ACTIVITY_RUNNING = "${STYLE_ACTIVITY}_$SUFFIX_RUNNING"
        const val STYLE_ACTIVITY_DONE = "${STYLE_ACTIVITY}_$SUFFIX_DONE"

        const val COLOR_WHITE = "#ffffff"
        const val COLOR_BLACK = "#000000"
        const val COLOR_RUNNING = "#ffffc5" // Light yellow
        const val COLOR_DONE = "#90ee90" // Light green
    }

    fun export(
        workflow: WorkflowData,
        output: OutputStream,
        runningActivityNames: List<String> = emptyList(),
        doneActivityNames: List<String> = emptyList(),
    ) {
        val graph = JGraphtUtil.createGraph(workflow)
        generatePNG(graph, workflow, output, runningActivityNames, doneActivityNames)
    }

    private fun generatePNG(
        graph: Graph<String, DefaultEdge>,
        workflow: WorkflowData,
        output: OutputStream,
        runningActivityNames: List<String>,
        doneActivityNames: List<String>,
    ) {
        val adapter = JGraphXAdapter<String, DefaultEdge>(graph)
        val layout = mxHierarchicalLayout(adapter, SwingConstants.NORTH)
        layout.execute(adapter.getDefaultParent())

        // Remove text from flows
        adapter.edgeToCellMap.values.forEach { edgeCell -> edgeCell.value = null }

        // Apply style
        applyEdgeStyle(adapter)
        applyVertexStyle(adapter, workflow, runningActivityNames, doneActivityNames)

        // Image
        val image = mxCellRenderer.createBufferedImage(adapter, null, 2.0, Color.WHITE, true, null)
        if (image != null) {
            ImageIO.write(image, "PNG", output)
        }
    }

    private fun applyEdgeStyle(adapter: JGraphXAdapter<String, DefaultEdge>) {
        adapter.stylesheet.putCellStyle(
            STYLE_EDGE,
            mapOf(
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )

        adapter.edgeToCellMap.values.forEach { edgeCell ->
            edgeCell.value = null
            edgeCell.style = STYLE_EDGE
        }
    }

    private fun applyVertexStyle(
        adapter: JGraphXAdapter<String, DefaultEdge>,
        workflow: WorkflowData,
        runningActivityNames: List<String>,
        doneActivityNames: List<String>,
    ) {
        val activityMap = workflow.activities.associateBy { it.name }

        adapter.stylesheet.putCellStyle(
            STYLE_START,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_WHITE,
                mxConstants.STYLE_FONTCOLOR to COLOR_BLACK,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_START_RUNNING,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_RUNNING,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_START_DONE,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_DONE,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )

        adapter.stylesheet.putCellStyle(
            STYLE_STOP_RUNNING,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_DOUBLE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_RUNNING,
                mxConstants.STYLE_FONTCOLOR to COLOR_BLACK,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_STOP_DONE,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_DOUBLE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_DONE,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_STOP,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_ELLIPSE,
                mxConstants.STYLE_FILLCOLOR to COLOR_WHITE,
                mxConstants.STYLE_FONTCOLOR to COLOR_BLACK,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
                mxConstants.STYLE_STROKEWIDTH to 4,
            )
        )

        adapter.stylesheet.putCellStyle(
            STYLE_ACTIVITY,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_RECTANGLE,
                mxConstants.STYLE_FILLCOLOR to COLOR_WHITE,
                mxConstants.STYLE_FONTCOLOR to COLOR_BLACK,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_ACTIVITY_DONE,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_RECTANGLE,
                mxConstants.STYLE_FILLCOLOR to COLOR_DONE,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )
        adapter.stylesheet.putCellStyle(
            STYLE_ACTIVITY_RUNNING,
            mapOf(
                mxConstants.STYLE_SHAPE to mxConstants.SHAPE_RECTANGLE,
                mxConstants.STYLE_FILLCOLOR to COLOR_RUNNING,
                mxConstants.STYLE_STROKECOLOR to COLOR_BLACK,
            )
        )

        adapter.vertexToCellMap.values.forEach { vertexCell ->
            val activity = activityMap[vertexCell.value]
            if (activity?.type == ActivityType.START) {
                vertexCell.value = null
                vertexCell.style = getStyleName(STYLE_START, activity, runningActivityNames, doneActivityNames)
            } else if (activity?.type == ActivityType.END) {
                vertexCell.value = null
                vertexCell.style = getStyleName(STYLE_STOP, activity, runningActivityNames, doneActivityNames)
            } else if (activity != null) {
                vertexCell.style = getStyleName(STYLE_ACTIVITY, activity, runningActivityNames, doneActivityNames)
            }
        }
    }
}

private fun getStyleName(
    baseStyleName: String,
    activity: ActivityData,
    runningActivityNames: List<String> = emptyList(),
    doneActivityNames: List<String> = emptyList(),
): String {
    return if (doneActivityNames.contains(activity.name)) {
        "${baseStyleName}_$SUFFIX_DONE"
    } else if (runningActivityNames.contains(activity.name)) {
        "${baseStyleName}_$SUFFIX_RUNNING"
    } else {
        baseStyleName
    }
}
