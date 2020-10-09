# Swagger definition generator

![Build](https://github.com/pave1-semenov/swagger-definition-generator/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/15181-swagger-schema-generator)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/15181-swagger-schema-generator)

<!-- Plugin description -->
This plugin generates Swagger OpenAPI file with schemas definitions for PHP classes.</p>
## Usage
Just click "Generate Swagger Definitions" in Tools menu or from Editor popup menu
- Plugin needs valid PHP Class file to be opened at the moment</li>
- It dumps only public properties with defined type - either with phpDoc or php 7.4 property type declaration</li>
- The type must not be mixed or a combination of types like "string|int"</li>
- Array types must be defined strictly - "int[]", "string[]", "SomeClass[]". Plain "array" declaration won't work</li>
- Plugin handles inherited properties as well</li>
- Class references are handled recursively. So if you got a class property it would persist in definitions file</li>
<!-- Plugin description end -->
---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
