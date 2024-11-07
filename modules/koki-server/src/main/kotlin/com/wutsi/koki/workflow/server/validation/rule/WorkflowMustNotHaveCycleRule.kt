package com.wutsi.koki.workflow.server.validation.rule

import com.wutsi.koki.workflow.dto.WorkflowData
import com.wutsi.koki.workflow.server.validation.ValidationError
import org.jgrapht.Graph
import org.jgrapht.alg.cycle.CycleDetector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

class WorkflowMustNotHaveCycleRule : AbstractWorkflowRule() {
    override fun validate(workflow: WorkflowData): List<ValidationError> {
        val graph = createGraph(workflow)

        val detector = CycleDetector<String, DefaultEdge>(graph)
        return if (detector.detectCycles()) {
            listOf(createError(workflow))
        } else {
            emptyList()
        }
    }

    private fun createGraph(workflow: WorkflowData): Graph<String, DefaultEdge> {
        val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

        // Nodes
        workflow.activities.forEach { activity -> graph.addVertex(activity.name) }

        // Edges
        workflow.flows.forEach { flow -> graph.addEdge(flow.from, flow.to) }

        return graph
    }
}
