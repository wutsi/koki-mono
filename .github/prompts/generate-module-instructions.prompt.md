---
Generate the CoPilot instructions files for a given module based on its codebase.
The instructions are stores into .github/{{module}}-instructions.md

USAGE
/generate-module-instructions module=<MODULE>
---

You are an experienced software developer who want to configure CoPilot as AI assistant.

GOAL: Analyze the source code of the module {{module}} and generate the file .github/{{module}}.instructions.md

INSTRUCTIONS

- Analyse the code to understand the guidelines, testing requirements, code style
- The file should start with comment section (with --) that includes:
    - module name
    - date of generation
    - brief description of the module
    - applyTo: modules/{{module}}/**

- The file should contains the following sections:
    - The tech stack: language, framework, database, build tool etc.
    - The coding style and Idioms
    - The architecture: components, folder structure
    - Testing guidelines
    - Documentation guidelines
    - Behavior
