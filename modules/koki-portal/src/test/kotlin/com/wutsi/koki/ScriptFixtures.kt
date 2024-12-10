package com.wutsi.koki

import com.wutsi.koki.script.dto.Language
import com.wutsi.koki.script.dto.Script
import com.wutsi.koki.script.dto.ScriptSummary

object ScriptFixtures {
    val SCRIPT_ID = "11111-22222-33333"
    val SCRIPT_NAME = "SCR-001"

    val script = Script(
        id = SCRIPT_ID,
        name = SCRIPT_NAME,
        title = "Generate Incident ID",
        description = "Generate unique identifier for the ticket",
        active = true,
        language = Language.JAVASCRIPT,
        parameters = listOf("input"),
        code = """
            function generate_id(){
                return Date().getTimer + '-100';
            }
            var id = generate_id();
            return id;
        """.trimIndent(),
    )

    val scripts = listOf(
        ScriptSummary(
            id = SCRIPT_ID,
            name = SCRIPT_NAME,
            title = "Generate Incident ID",
            active = true,
            language = Language.JAVASCRIPT,
        ),
        ScriptSummary(
            id = "2",
            name = "SCR-002",
            title = "This is the 2nd script",
            active = false,
            language = Language.JAVASCRIPT,
        ),
        ScriptSummary(
            id = "3",
            name = "SCR-003",
            title = "This is the 3rd script",
            active = true,
            language = Language.PYTHON,
        ),
    )
}
