package com.wutsi.koki.workflow.server.io

import com.mxgraph.layout.mxCompactTreeLayout
import com.mxgraph.util.mxCellRenderer
import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.OutputStream
import javax.imageio.ImageIO

@Service
class WorkflowPNGExporter {
    fun export(workflow: WorkflowEntity, output: OutputStream) {
        val graph = createGraph(workflow)
        generatePNG(graph, output)
    }

    private fun createGraph(workflow: WorkflowEntity): Graph<String, DefaultEdge> {
        val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)

        // Nodes
        workflow.activities
            .filter { it.active }
            .forEach { activity -> graph.addVertex(activity.name) }

        // Edges
        workflow.activities
            .filter { it.active }
            .forEach { activity ->
                activity.predecessors
                    .filter { it.active }
                    .forEach { predecessor -> graph.addEdge(predecessor.name, activity.name) }
            }

        return graph
    }

    private fun generatePNG(graph: Graph<String, DefaultEdge>, output: OutputStream) {
        val adapter = JGraphXAdapter<String, DefaultEdge>(graph)
        val layout = mxCompactTreeLayout(adapter)
        layout.execute(adapter.getDefaultParent())

        // Remove text from edges
        adapter.edgeToCellMap.values.forEach { edge -> edge.value = null }

        // Image
        val image = mxCellRenderer.createBufferedImage(adapter, null, 2.0, Color.WHITE, true, null)
        if (image != null) {
            ImageIO.write(image, "PNG", output)
        }
    }
}
