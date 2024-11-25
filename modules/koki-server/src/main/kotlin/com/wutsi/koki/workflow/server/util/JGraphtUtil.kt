package com.wutsi.koki.workflow.server.util

import com.wutsi.koki.workflow.server.domain.WorkflowEntity
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

object JGraphtUtil {
    fun createGraph(workflow: WorkflowEntity): Graph<String, DefaultEdge> {
        val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

        // Nodes
        workflow.activities
            .filter { it.active }
            .forEach { activity -> graph.addVertex(activity.name) }

        // Edges
        workflow.flows
            .filter { flow -> flow.from.active }
            .filter { flow -> flow.to.active }
            .forEach { flow -> graph.addEdge(flow.from.name, flow.to.name) }
        return graph
    }
}
