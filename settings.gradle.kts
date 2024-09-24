import org.gradle.api.internal.FeaturePreviews

pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.gradle.org/gradle/enterprise-libs-release-candidates")
            content {
                val rcAndMilestonesPattern = "\\d{1,2}?\\.\\d{1,2}?(\\.\\d{1,2}?)?-((rc-\\d{1,2}?)|(milestone-\\d{1,2}?))"
                // GE plugin marker artifact
                includeVersionByRegex("com.gradle.develocity", "com.gradle.develocity.gradle.plugin", rcAndMilestonesPattern)
                // GE plugin jar
                includeVersionByRegex("com.gradle", "develocity-gradle-plugin", rcAndMilestonesPattern)
            }
        }
        maven {
            name = "Gradle public repository"
            url = uri("https://repo.gradle.org/gradle/public")
            content {
                includeModule("org.openmbee.junit", "junit-xml-parser")
            }
        }
        gradlePluginPortal()
    }
    includeBuild("build-logic-settings")
}

plugins {
    id("gradlebuild.build-environment")
    id("com.gradle.develocity").version("3.18.1") // Run `java build-logic-settings/UpdateDevelocityPluginVersion.java <new-version>` to update
    id("io.github.gradle.gradle-enterprise-conventions-plugin").version("0.10.1")
    id("org.gradle.toolchains.foojay-resolver-convention").version ("0.8.0")
}

includeBuild("build-logic-commons")
includeBuild("build-logic")

apply(from = "gradle/shared-with-buildSrc/mirrors.settings.gradle.kts")

val architectureElements = mutableListOf<ArchitectureElementBuilder>()

// If you include a new subproject here, consult internal documentation "Adding a new Build Tool subproject" page

unassigned {
    subproject("distributions-dependencies") // platform for dependency versions
    subproject("core-platform")              // platform for Gradle distribution core
}

// Gradle Distributions - for testing and for publishing a full distribution
unassigned {
    subproject("distributions-full")
}

// Public API publishing
unassigned {
    subproject("public-api")
}

// Gradle implementation projects
unassigned {
    subproject("core")
    subproject("build-events")
    subproject("diagnostics")
    subproject("composite-builds")
    subproject("core-api")
}

// Core platform
val core = platform("core") {

    // Core Runtime Module
    module("core-runtime") {
        subproject("base-asm")
        subproject("base-services")
        subproject("build-configuration")
        subproject("build-operations")
        subproject("build-operations-trace")
        subproject("build-option")
        subproject("build-process-services")
        subproject("build-profile")
        subproject("build-state")
        subproject("cli")
        subproject("client-services")
        subproject("concurrent")
        subproject("daemon-main")
        subproject("daemon-protocol")
        subproject("daemon-services")
        subproject("daemon-server")
        subproject("distributions-basics")
        subproject("distributions-core")
        subproject("file-temp")
        subproject("files")
        subproject("functional")
        subproject("gradle-cli-main")
        subproject("gradle-cli")
        subproject("installation-beacon")
        subproject("instrumentation-agent")
        subproject("instrumentation-agent-services")
        subproject("instrumentation-declarations")
        subproject("instrumentation-reporting")
        subproject("internal-instrumentation-api")
        subproject("internal-instrumentation-processor")
        subproject("io")
        subproject("stdlib-java-extensions")
        subproject("launcher")
        subproject("logging")
        subproject("logging-api")
        subproject("messaging")
        subproject("native")
        subproject("process-memory-services")
        subproject("process-services")
        subproject("report-rendering")
        subproject("serialization")
        subproject("service-lookup")
        subproject("service-provider")
        subproject("service-registry-builder")
        subproject("service-registry-impl")
        subproject("time")
        subproject("tooling-api-provider")
        subproject("wrapper-main")
        subproject("wrapper-shared")
    }

    // Core Configuration Module
    module("core-configuration") {
        subproject("api-metadata")
        subproject("base-services-groovy")
        subproject("bean-serialization-services")
        subproject("configuration-cache")
        subproject("configuration-cache-base")
        subproject("configuration-problems-base")
        subproject("core-kotlin-extensions")
        subproject("core-serialization-codecs")
        subproject("declarative-dsl-api")
        subproject("declarative-dsl-core")
        subproject("declarative-dsl-evaluator")
        subproject("declarative-dsl-provider")
        subproject("declarative-dsl-tooling-models")
        subproject("declarative-dsl-tooling-builders")
        subproject("declarative-dsl-internal-utils")
        subproject("dependency-management-serialization-codecs")
        subproject("encryption-services")
        subproject("file-collections")
        subproject("file-operations")
        subproject("flow-services")
        subproject("graph-serialization")
        subproject("guava-serialization-codecs")
        subproject("input-tracking")
        subproject("kotlin-dsl")
        subproject("kotlin-dsl-provider-plugins")
        subproject("kotlin-dsl-tooling-builders")
        subproject("kotlin-dsl-tooling-models")
        subproject("kotlin-dsl-plugins")
        subproject("kotlin-dsl-integ-tests")
        subproject("stdlib-kotlin-extensions")
        subproject("stdlib-serialization-codecs")
        subproject("model-core")
        subproject("model-groovy")
    }

    // Core Execution Module
    module("core-execution") {
        subproject("build-cache")
        subproject("build-cache-base")
        subproject("build-cache-example-client")
        subproject("build-cache-local")
        subproject("build-cache-http")
        subproject("build-cache-packaging")
        subproject("build-cache-spi")
        subproject("execution-e2e-tests")
        subproject("file-watching")
        subproject("execution")
        subproject("hashing")
        subproject("persistent-cache")
        subproject("snapshots")
        subproject("worker-main")
        subproject("workers")
    }
}

// Documentation Module
module("documentation") {
    subproject("docs")
    subproject("docs-asciidoctor-extensions-base")
    subproject("docs-asciidoctor-extensions")
    subproject("samples")
}

// IDE Module
module("ide") {
    subproject("base-ide-plugins")
    subproject("ide")
    subproject("ide-native")
    subproject("ide-plugins")
    subproject("problems")
    subproject("problems-api")
    subproject("problems-rendering")
    subproject("tooling-api")
    subproject("tooling-api-builders")
}

// Software Platform
val software = platform("software") {
    uses(core)
    subproject("antlr")
    subproject("build-init")
    subproject("dependency-management")
    subproject("plugins-distribution")
    subproject("distributions-publishing")
    subproject("ivy")
    subproject("maven")
    subproject("platform-base")
    subproject("plugins-version-catalog")
    subproject("publish")
    subproject("resources")
    subproject("resources-http")
    subproject("resources-gcs")
    subproject("resources-s3")
    subproject("resources-sftp")
    subproject("reporting")
    subproject("security")
    subproject("signing")
    subproject("testing-base")
    subproject("testing-base-infrastructure")
    subproject("test-suites-base")
    subproject("version-control")
}

// JVM Platform
val jvm = platform("jvm") {
    uses(core)
    uses(software)
    subproject("code-quality")
    subproject("distributions-jvm")
    subproject("ear")
    subproject("jacoco")
    subproject("jvm-services")
    subproject("language-groovy")
    subproject("language-java")
    subproject("language-jvm")
    subproject("toolchains-jvm")
    subproject("toolchains-jvm-shared")
    subproject("java-compiler-plugin")
    subproject("java-platform")
    subproject("normalization-java")
    subproject("platform-jvm")
    subproject("plugins-application")
    subproject("plugins-groovy")
    subproject("plugins-java")
    subproject("plugins-java-base")
    subproject("plugins-java-library")
    subproject("plugins-jvm-test-fixtures")
    subproject("plugins-jvm-test-suite")
    subproject("plugins-test-report-aggregation")
    subproject("scala")
    subproject("testing-jvm")
    subproject("testing-jvm-infrastructure")
    subproject("testing-junit-platform")
    subproject("war")
}

// Extensibility Platform
platform("extensibility") {
    uses(core)
    uses(jvm)
    subproject("plugin-use")
    subproject("plugin-development")
    subproject("unit-test-fixtures")
    subproject("test-kit")
}

// Native Platform
platform("native") {
    uses(core)
    uses(software)
    subproject("distributions-native")
    subproject("platform-native")
    subproject("language-native")
    subproject("tooling-native")
    subproject("testing-native")
}


// Develocity Module
module("enterprise") {
    subproject("enterprise")
    subproject("enterprise-logging")
    subproject("enterprise-operations")
    subproject("enterprise-plugin-performance")
    subproject("enterprise-workers")
}

testing {
    subproject("architecture-test")
    subproject("distributions-integ-tests")
    subproject("integ-test")
    subproject("internal-architecture-testing")
    subproject("internal-integ-testing")
    subproject("internal-performance-testing")
    subproject("internal-testing")
    subproject("performance")
    subproject("precondition-tester")
    subproject("public-api-tests")
    subproject("soak")
    subproject("smoke-ide-test") // eventually should be owned by IDEX team
    subproject("smoke-test")
}

// Internal utility and verification projects
unassigned {
    subproject("internal-build-reports")
}

rootProject.name = "gradle"

FeaturePreviews.Feature.values().forEach { feature ->
    if (feature.isActive) {
        enableFeaturePreview(feature.name)
    }
}

fun remoteBuildCacheEnabled(settings: Settings) = settings.buildCache.remote?.isEnabled == true

fun getBuildJavaHome() = System.getProperty("java.home")

gradle.settingsEvaluated {
    if ("true" == System.getProperty("org.gradle.ignoreBuildJavaVersionCheck")) {
        return@settingsEvaluated
    }

    if (!JavaVersion.current().isJava11) {
        throw GradleException("This build requires JDK 11. It's currently ${getBuildJavaHome()}. You can ignore this check by passing '-Dorg.gradle.ignoreBuildJavaVersionCheck=true'.")
    }
}

// region platform include DSL

gradle.rootProject {
    tasks.register("architectureDoc", GeneratorTask::class.java) {
        description = "Generates the architecture documentation"
        outputFile = layout.projectDirectory.file("architecture/platforms.md")
        elements = provider { architectureElements.map { it.build() } }
    }
}

abstract class GeneratorTask : DefaultTask() {
    private val markerComment = "<!-- This diagram is generated. Use `./gradlew :architectureDoc` to update it -->"
    private val startDiagram = "```mermaid"
    private val endDiagram = "```"

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val elements: ListProperty<ArchitectureElement>

    @TaskAction
    fun generate() {
        val markdownFile = outputFile.asFile.get()
        val head = if (markdownFile.exists()) {
            val content = markdownFile.readText().lines()
            val markerPos = content.indexOfFirst { it.contains(markerComment) }
            if (markerPos < 0) {
                throw IllegalArgumentException("Could not locate the generated diagram in $markdownFile")
            }
            val endPos = content.subList(markerPos, content.size).indexOfFirst { it.contains(endDiagram) && !it.contains(startDiagram) }
            if (endPos < 0) {
                throw IllegalArgumentException("Could not locate the end of the generated diagram in $markdownFile")
            }
            content.subList(0, markerPos)
        } else {
            emptyList()
        }

        markdownFile.bufferedWriter().use {
            PrintWriter(it).run {
                for (line in head) {
                    println(line)
                }
                graph(elements.get())
            }
        }
    }

    private fun PrintWriter.graph(elements: List<ArchitectureElement>) {
        println(
            """
            $markerComment
            $startDiagram
        """.trimIndent()
        )
        val writer = NodeWriter(this, "    ")
        writer.node("graph TD")
        for (element in elements) {
            if (element is Platform) {
                writer.platform(element)
            } else {
                writer.element(element)
            }
        }
        println(endDiagram)
    }

    private fun NodeWriter.platform(platform: Platform) {
        println()
        node("subgraph ${platform.id}[\"${platform.name} platform\"]") {
            for (child in platform.children) {
                element(child)
            }
        }
        node("end")
        node("style ${platform.id} fill:#c2e0f4,stroke:#3498db,stroke-width:2px,color:#000;")
        for (dep in platform.uses) {
            node("${platform.id} --> $dep")
        }
    }

    private fun NodeWriter.element(element: ArchitectureElement) {
        println()
        node("${element.id}[\"${element.name} module\"]")
        node("style ${element.id} stroke:#1abc9c,fill:#b1f4e7,stroke-width:2px,color:#000;")
    }

    private class NodeWriter(private val writer: PrintWriter, private val indent: String) {
        fun println() {
            writer.println()
        }

        fun node(node: String) {
            writer.print(indent)
            writer.println(node)
        }

        fun node(node: String, builder: NodeWriter.() -> Unit) {
            writer.print(indent)
            writer.println(node)
            builder(NodeWriter(writer, "$indent    "))
        }
    }
}

/**
 * Defines a top-level architecture module.
 */
fun module(moduleName: String, moduleConfiguration: ArchitectureModuleBuilder.() -> Unit) {
    val module = ArchitectureModuleBuilder(moduleName)
    architectureElements.add(module)
    module.moduleConfiguration()
}

/**
 * Defines a platform.
 */
fun platform(platformName: String, platformConfiguration: PlatformBuilder.() -> Unit): PlatformBuilder {
    val platform = PlatformBuilder(platformName)
    architectureElements.add(platform)
    platform.platformConfiguration()
    return platform
}

/**
 * Defines the testing module, for project helping test Gradle.
 */
fun testing(moduleConfiguration: ProjectScope.() -> Unit) =
    ProjectScope("testing").moduleConfiguration()

/**
 * Defines a bucket of unassigned projects.
 */
fun unassigned(moduleConfiguration: ProjectScope.() -> Unit) =
    ProjectScope("subprojects").moduleConfiguration()

class ProjectScope(
    private val basePath: String
) {
    fun subproject(projectName: String) {
        include(projectName)
        project(":$projectName").projectDir = file("$basePath/$projectName")
    }
}

class ElementId(val id: String) : Serializable {
    override fun toString(): String {
        return id
    }
}

sealed class ArchitectureElement(
    val name: String,
    val id: ElementId
) : Serializable

class Platform(name: String, id: ElementId, val uses: List<ElementId>, val children: List<ArchitectureModule>) : ArchitectureElement(name, id)

class ArchitectureModule(name: String, id: ElementId) : ArchitectureElement(name, id)

sealed class ArchitectureElementBuilder(
    val name: String
) {
    val id: ElementId = ElementId(name.replace("-", "_"))

    abstract fun build(): ArchitectureElement
}

class ArchitectureModuleBuilder(
    name: String,
    private val projectScope: ProjectScope
) : ArchitectureElementBuilder(name) {
    constructor(name: String) : this(name, ProjectScope("platforms/$name"))

    fun subproject(projectName: String) {
        projectScope.subproject(projectName)
    }

    override fun build(): ArchitectureModule {
        return ArchitectureModule(name, id)
    }
}

class PlatformBuilder(
    name: String,
    private val projectScope: ProjectScope
) : ArchitectureElementBuilder(name) {
    private val modules = mutableListOf<ArchitectureModuleBuilder>()
    private val uses = mutableListOf<PlatformBuilder>()

    constructor(name: String) : this(name, ProjectScope("platforms/$name"))

    fun subproject(projectName: String) {
        projectScope.subproject(projectName)
    }

    fun uses(platform: PlatformBuilder) {
        uses.add(platform)
    }

    fun module(platformName: String, moduleConfiguration: ArchitectureModuleBuilder.() -> Unit) {
        val module = ArchitectureModuleBuilder(platformName)
        modules.add(module)
        module.moduleConfiguration()
    }

    override fun build(): Platform {
        return Platform(name, id, uses.map { it.id }, modules.map { it.build() })
    }
}
/*
 * Copyright (c) 1996, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.io

import java . util . Objects
    import java . util . Formatter
    import java . util . Locale
    import java . nio . charset . Charset
    import java . nio . charset . IllegalCharsetNameException
    import java . nio . charset . UnsupportedCharsetException
    import jdk . internal . access . JavaIOPrintWriterAccess
    import jdk . internal . access . SharedSecrets
    import jdk . internal . misc . InternalLock
/**
 * Prints formatted representations of objects to a text-output stream.  This
 * class implements all of the `print` methods found in [ ].  It does not contain methods for writing raw bytes, for which
 * a program should use unencoded byte streams.
 *
 *
 *  Unlike the [PrintStream] class, if automatic flushing is enabled
 * it will be done only when one of the `println`, `printf`, or
 * `format` methods is invoked, rather than whenever a newline character
 * happens to be output.  These methods use the platform's own notion of line
 * separator rather than the newline character.
 *
 *
 *  Methods in this class never throw I/O exceptions, although some of its
 * constructors may.  The client may inquire as to whether any errors have
 * occurred by invoking [checkError()][.checkError].
 *
 *
 *  This class always replaces malformed and unmappable character sequences with
 * the charset's default replacement string.
 * The [java.nio.charset.CharsetEncoder] class should be used when more
 * control over the encoding process is required.
 *
 * @author      Frank Yellin
 * @author      Mark Reinhold
 * @since       1.1
 */
class PrintWriter : Writer {
    /**
     * The underlying character-output stream of this
     * `PrintWriter`.
     *
     * @since 1.2
     */
    protected var out: Writer? = null

    private val autoFlush: Boolean
    private var trouble = false
    private var formatter: Formatter? = null
    private var psOut: PrintStream? = null

    /**
     * Creates a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    constructor(out: Writer?) : this(out, false)

    /**
     * Creates a new PrintWriter.
     *
     * @param  out        A character-output stream
     * @param  autoFlush  A boolean; if true, the `println`,
     * `printf`, or `format` methods will
     * flush the output buffer
     */
    constructor(out: Writer?, autoFlush: Boolean) : super(out) {
        this.out = out
        this.autoFlush = autoFlush
    }

    /**
     * Creates a new PrintWriter, without automatic line flushing, from an
     * existing OutputStream.  This convenience constructor creates the
     * necessary intermediate OutputStreamWriter, which will convert characters
     * into bytes using the default charset, or where `out` is a
     * `PrintStream`, the charset used by the print stream.
     *
     * @param  out        An output stream
     *
     * @see OutputStreamWriter.OutputStreamWriter
     * @see Charset.defaultCharset
     */
    constructor(out: OutputStream?) : this(out, false)

    /**
     * Creates a new PrintWriter from an existing OutputStream.  This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using
     * the default charset, or where `out` is a `PrintStream`,
     * the charset used by the print stream.
     *
     * @param  out        An output stream
     * @param  autoFlush  A boolean; if true, the `println`,
     * `printf`, or `format` methods will
     * flush the output buffer
     *
     * @see OutputStreamWriter.OutputStreamWriter
     * @see Charset.defaultCharset
     */
    constructor(out: OutputStream, autoFlush: Boolean) : this(out, autoFlush, if (out is PrintStream) out.charset() else Charset.defaultCharset())

    /**
     * Creates a new PrintWriter from an existing OutputStream.  This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using the
     * specified charset.
     *
     * @param  out        An output stream
     * @param  autoFlush  A boolean; if true, the `println`,
     * `printf`, or `format` methods will
     * flush the output buffer
     * @param  charset
     * A [charset][Charset]
     *
     * @since 10
     */
    constructor(out: OutputStream?, autoFlush: Boolean, charset: Charset?) : this(BufferedWriter(OutputStreamWriter(out, charset)), autoFlush) {
        // save print stream for error propagation
        if (out is java.io.PrintStream) {
            psOut = out as PrintStream?
        }
    }

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file name.  This convenience constructor creates the necessary
     * intermediate [OutputStreamWriter],
     * which will encode characters using the [ ][Charset.defaultCharset] for this
     * instance of the Java virtual machine.
     *
     * @param  fileName
     * The name of the file to use as the destination of this writer.
     * If the file exists then it will be truncated to zero size;
     * otherwise, a new file will be created.  The output will be
     * written to the file and is buffered.
     *
     * @throws  FileNotFoundException
     * If the given string does not denote an existing, writable
     * regular file and a new regular file of that name cannot be
     * created, or if some other error occurs while opening or
     * creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite] denies write
     * access to the file
     * @see Charset.defaultCharset
     * @since  1.5
     */
    constructor(fileName: String?) : this(
        BufferedWriter(OutputStreamWriter(FileOutputStream(fileName))),
        false
    )

    /* Package private constructor, using the specified lock
     * for synchronization.
     */
    internal constructor(out: Writer?, lock: Object?) : super(lock) {
        this.out = out
        this.autoFlush = false
    }

    /* Private constructor */
    private constructor(charset: Charset, file: File) : this(
        BufferedWriter(OutputStreamWriter(FileOutputStream(file), charset)),
        false
    )

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file name and charset.  This convenience constructor creates
     * the necessary intermediate [ OutputStreamWriter][OutputStreamWriter], which will encode characters using the provided
     * charset.
     *
     * @param  fileName
     * The name of the file to use as the destination of this writer.
     * If the file exists then it will be truncated to zero size;
     * otherwise, a new file will be created.  The output will be
     * written to the file and is buffered.
     *
     * @param  csn
     * The name of a supported [charset][Charset]
     *
     * @throws  FileNotFoundException
     * If the given string does not denote an existing, writable
     * regular file and a new regular file of that name cannot be
     * created, or if some other error occurs while opening or
     * creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite] denies write
     * access to the file
     *
     * @throws  UnsupportedEncodingException
     * If the named charset is not supported
     *
     * @since  1.5
     */
    constructor(fileName: String?, csn: String) : this(toCharset(csn), File(fileName))

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file name and charset.  This convenience constructor creates
     * the necessary intermediate [ OutputStreamWriter][OutputStreamWriter], which will encode characters using the provided
     * charset.
     *
     * @param  fileName
     * The name of the file to use as the destination of this writer.
     * If the file exists then it will be truncated to zero size;
     * otherwise, a new file will be created.  The output will be
     * written to the file and is buffered.
     *
     * @param  charset
     * A [charset][Charset]
     *
     * @throws  IOException
     * if an I/O error occurs while opening or creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite] denies write
     * access to the file
     *
     * @since  10
     */
    constructor(fileName: String?, charset: Charset?) : this(Objects.requireNonNull(charset, "charset"), File(fileName))

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file.  This convenience constructor creates the necessary
     * intermediate [OutputStreamWriter],
     * which will encode characters using the [ ][Charset.defaultCharset] for this
     * instance of the Java virtual machine.
     *
     * @param  file
     * The file to use as the destination of this writer.  If the file
     * exists then it will be truncated to zero size; otherwise, a new
     * file will be created.  The output will be written to the file
     * and is buffered.
     *
     * @throws  FileNotFoundException
     * If the given file object does not denote an existing, writable
     * regular file and a new regular file of that name cannot be
     * created, or if some other error occurs while opening or
     * creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite]
     * denies write access to the file
     * @see Charset.defaultCharset
     * @since  1.5
     */
    constructor(file: File?) : this(
        BufferedWriter(OutputStreamWriter(FileOutputStream(file))),
        false
    )

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file and charset.  This convenience constructor creates the
     * necessary intermediate [ OutputStreamWriter][OutputStreamWriter], which will encode characters using the provided
     * charset.
     *
     * @param  file
     * The file to use as the destination of this writer.  If the file
     * exists then it will be truncated to zero size; otherwise, a new
     * file will be created.  The output will be written to the file
     * and is buffered.
     *
     * @param  csn
     * The name of a supported [charset][Charset]
     *
     * @throws  FileNotFoundException
     * If the given file object does not denote an existing, writable
     * regular file and a new regular file of that name cannot be
     * created, or if some other error occurs while opening or
     * creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite]
     * denies write access to the file
     *
     * @throws  UnsupportedEncodingException
     * If the named charset is not supported
     *
     * @since  1.5
     */
    constructor(file: File?, csn: String) : this(toCharset(csn), file)

    /**
     * Creates a new PrintWriter, without automatic line flushing, with the
     * specified file and charset.  This convenience constructor creates the
     * necessary intermediate [ OutputStreamWriter][java.io.OutputStreamWriter], which will encode characters using the provided
     * charset.
     *
     * @param  file
     * The file to use as the destination of this writer.  If the file
     * exists then it will be truncated to zero size; otherwise, a new
     * file will be created.  The output will be written to the file
     * and is buffered.
     *
     * @param  charset
     * A [charset][Charset]
     *
     * @throws  IOException
     * if an I/O error occurs while opening or creating the file
     *
     * @throws  SecurityException
     * If a security manager is present and [          ][SecurityManager.checkWrite]
     * denies write access to the file
     *
     * @since  10
     */
    constructor(file: File?, charset: Charset?) : this(Objects.requireNonNull(charset, "charset"), file)

    /** Checks to make sure that the stream has not been closed  */
    @kotlin.Throws(IOException::class)
    private fun ensureOpen() {
        if (out == null) throw IOException("Stream closed")
    }

    /**
     * Flushes the stream.
     * @see .checkError
     */
    fun flush() {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implFlush()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implFlush()
            }
        }
    }

    private fun implFlush() {
        try {
            ensureOpen()
            out.flush()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Closes the stream and releases any system resources associated
     * with it. Closing a previously closed stream has no effect.
     *
     * @see .checkError
     */
    fun close() {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implClose()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implClose()
            }
        }
    }

    private fun implClose() {
        try {
            if (out != null) {
                out.close()
                out = null
            }
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Flushes the stream if it's not closed and checks its error state.
     *
     * @return `true` if and only if this stream has encountered an
     * `IOException`, or the `setError` method has been
     * invoked
     */
    fun checkError(): Boolean {
        if (out != null) {
            flush()
        }
        if (out is PrintWriter) {
            return out.checkError()
        } else if (psOut != null) {
            return psOut.checkError()
        }
        return trouble
    }

    /**
     * Sets the error state of the stream to `true`.
     *
     *
     *  This method will cause subsequent invocations of [ ][.checkError] to return `true` until [ ][.clearError] is invoked.
     */
    protected fun setError() {
        trouble = true
    }

    /**
     * Clears the error state of this stream.
     *
     *
     *  This method will cause subsequent invocations of [ ][.checkError] to return `false` until another write
     * operation fails and invokes [.setError].
     *
     * @since 1.6
     */
    protected fun clearError() {
        trouble = false
    }

    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of Writer
     */
    /**
     * Writes a single character.
     * @param c int specifying a character to be written.
     */
    fun write(c: Int) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implWrite(c)
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implWrite(c)
            }
        }
    }

    private fun implWrite(c: Int) {
        try {
            ensureOpen()
            out.write(c)
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Writes A Portion of an array of characters.
     * @param buf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     *
     * @throws  IndexOutOfBoundsException
     * If the values of the `off` and `len` parameters
     * cause the corresponding method of the underlying `Writer`
     * to throw an `IndexOutOfBoundsException`
     */
    /**
     * Writes an array of characters.  This method cannot be inherited from the
     * Writer class because it must suppress I/O exceptions.
     * @param buf Array of characters to be written
     */
    @kotlin.jvm.JvmOverloads
    fun write(buf: CharArray, off: Int = 0, len: Int = buf.length) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implWrite(buf, off, len)
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implWrite(buf, off, len)
            }
        }
    }

    private fun implWrite(buf: CharArray, off: Int, len: Int) {
        try {
            ensureOpen()
            out.write(buf, off, len)
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Writes a portion of a string.
     * @param s A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     *
     * @throws  IndexOutOfBoundsException
     * If the values of the `off` and `len` parameters
     * cause the corresponding method of the underlying `Writer`
     * to throw an `IndexOutOfBoundsException`
     */
    fun write(s: String, off: Int, len: Int) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implWrite(s, off, len)
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implWrite(s, off, len)
            }
        }
    }

    private fun implWrite(s: String, off: Int, len: Int) {
        try {
            ensureOpen()
            out.write(s, off, len)
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Writes a string.  This method cannot be inherited from the Writer class
     * because it must suppress I/O exceptions.
     * @param s String to be written
     */
    fun write(s: String) {
        write(s, 0, s.length())
    }

    private fun newLine() {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implNewLine()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implNewLine()
            }
        }
    }

    private fun implNewLine() {
        try {
            ensureOpen()
            out.write(System.lineSeparator())
            if (autoFlush) out.flush()
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /* Methods that do not terminate lines */
    /**
     * Prints a boolean value.  The string produced by [ ][java.lang.String.valueOf] is translated into bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [ ][.write] method.
     *
     * @param      b   The `boolean` to be printed
     * @see Charset.defaultCharset
     */
    fun print(b: Boolean) {
        write(String.valueOf(b))
    }

    /**
     * Prints a character.  The character is translated into one or more bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [ ][.write] method.
     *
     * @param      c   The `char` to be printed
     * @see Charset.defaultCharset
     */
    fun print(c: Char) {
        write(c.code)
    }

    /**
     * Prints an integer.  The string produced by [ ][java.lang.String.valueOf] is translated into bytes according
     * to the default charset, and these bytes are
     * written in exactly the manner of the [.write]
     * method.
     *
     * @param      i   The `int` to be printed
     * @see java.lang.Integer.toString
     * @see Charset.defaultCharset
     */
    fun print(i: Int) {
        write(String.valueOf(i))
    }

    /**
     * Prints a long integer.  The string produced by [ ][java.lang.String.valueOf] is translated into bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [.write]
     * method.
     *
     * @param      l   The `long` to be printed
     * @see java.lang.Long.toString
     * @see Charset.defaultCharset
     */
    fun print(l: Long) {
        write(String.valueOf(l))
    }

    /**
     * Prints a floating-point number.  The string produced by [ ][java.lang.String.valueOf] is translated into bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [.write]
     * method.
     *
     * @param      f   The `float` to be printed
     * @see java.lang.Float.toString
     * @see Charset.defaultCharset
     */
    fun print(f: Float) {
        write(String.valueOf(f))
    }

    /**
     * Prints a double-precision floating-point number.  The string produced by
     * [java.lang.String.valueOf] is translated into
     * bytes according to the default charset, and these
     * bytes are written in exactly the manner of the [ ][.write] method.
     *
     * @param      d   The `double` to be printed
     * @see java.lang.Double.toString
     * @see Charset.defaultCharset
     */
    fun print(d: Double) {
        write(String.valueOf(d))
    }

    /**
     * Prints an array of characters.  The characters are converted into bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [.write]
     * method.
     *
     * @param      s   The array of chars to be printed
     * @see Charset.defaultCharset
     * @throws  NullPointerException  If `s` is `null`
     */
    fun print(s: CharArray) {
        write(s)
    }

    /**
     * Prints a string.  If the argument is `null` then the string
     * `"null"` is printed.  Otherwise, the string's characters are
     * converted into bytes according to the default charset,
     * and these bytes are written in exactly the manner of the
     * [.write] method.
     *
     * @param      s   The `String` to be printed
     * @see Charset.defaultCharset
     */
    fun print(s: String?) {
        write(String.valueOf(s))
    }

    /**
     * Prints an object.  The string produced by the [ ][java.lang.String.valueOf] method is translated into bytes
     * according to the default charset, and these bytes
     * are written in exactly the manner of the [.write]
     * method.
     *
     * @param      obj   The `Object` to be printed
     * @see java.lang.Object.toString
     * @see Charset.defaultCharset
     */
    fun print(obj: Object?) {
        write(String.valueOf(obj))
    }

    /* Methods that do terminate lines */
    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator is [System.lineSeparator] and is not necessarily
     * a single newline character (`'\n'`).
     */
    fun println() {
        newLine()
    }

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes [.print] and then
     * [.println].
     *
     * @param x the `boolean` value to be printed
     */
    fun println(x: Boolean) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes [.print] and then [ ][.println].
     *
     * @param x the `char` value to be printed
     */
    fun println(x: Char) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes [.print] and then [ ][.println].
     *
     * @param x the `int` value to be printed
     */
    fun println(x: Int) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes [.print] and then
     * [.println].
     *
     * @param x the `long` value to be printed
     */
    fun println(x: Long) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes [.print] and then
     * [.println].
     *
     * @param x the `float` value to be printed
     */
    fun println(x: Float) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes [ ][.print] and then [.println].
     *
     * @param x the `double` value to be printed
     */
    fun println(x: Double) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints an array of characters and then terminates the line.  This method
     * behaves as though it invokes [.print] and then
     * [.println].
     *
     * @param x the array of `char` values to be printed
     */
    fun println(x: CharArray?) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes [.print] and then
     * [.println].
     *
     * @param x the `String` value to be printed
     */
    fun println(x: String?) {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(x)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(x)
                println()
            }
        }
    }

    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes [.print] and then
     * [.println].
     *
     * @param x  The `Object` to be printed.
     */
    fun println(x: Object?) {
        val s: String = String.valueOf(x)
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                print(s)
                println()
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                print(s)
                println()
            }
        }
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     *
     *
     *  An invocation of this method of the form
     * `out.printf(format, args)`
     * behaves in exactly the same way as the invocation
     *
     * {@snippet lang=java :
     * *     out.format(format, args)
     * * }
     *
     * @param  format
     * A format string as described in [Format string syntax](../util/Formatter.html#syntax).
     *
     * @param  args
     * Arguments referenced by the format specifiers in the format
     * string.  If there are more arguments than format specifiers, the
     * extra arguments are ignored.  The number of arguments is
     * variable and may be zero.  The maximum number of arguments is
     * limited by the maximum dimension of a Java array as defined by
     * <cite>The Java Virtual Machine Specification</cite>.
     * The behaviour on a
     * `null` argument depends on the [conversion](../util/Formatter.html#syntax).
     *
     * @throws  java.util.IllegalFormatException
     * If a format string contains an illegal syntax, a format
     * specifier that is incompatible with the given arguments,
     * insufficient arguments given the format string, or other
     * illegal conditions.  For specification of all possible
     * formatting errors, see the [Details](../util/Formatter.html#detail) section of the
     * formatter class specification.
     *
     * @throws  NullPointerException
     * If the `format` is `null`
     *
     * @return  This writer
     *
     * @since  1.5
     */
    fun printf(format: String?, vararg args: Object?): PrintWriter {
        return format(format, *args)
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     *
     *
     *  An invocation of this method of the form
     * `out.printf(l, format, args)`
     * behaves in exactly the same way as the invocation
     *
     * {@snippet lang=java :
     * *     out.format(l, format, args)
     * * }
     *
     * @param  l
     * The [locale][java.util.Locale] to apply during
     * formatting.  If `l` is `null` then no localization
     * is applied.
     *
     * @param  format
     * A format string as described in [Format string syntax](../util/Formatter.html#syntax).
     *
     * @param  args
     * Arguments referenced by the format specifiers in the format
     * string.  If there are more arguments than format specifiers, the
     * extra arguments are ignored.  The number of arguments is
     * variable and may be zero.  The maximum number of arguments is
     * limited by the maximum dimension of a Java array as defined by
     * <cite>The Java Virtual Machine Specification</cite>.
     * The behaviour on a
     * `null` argument depends on the [conversion](../util/Formatter.html#syntax).
     *
     * @throws  java.util.IllegalFormatException
     * If a format string contains an illegal syntax, a format
     * specifier that is incompatible with the given arguments,
     * insufficient arguments given the format string, or other
     * illegal conditions.  For specification of all possible
     * formatting errors, see the [Details](../util/Formatter.html#detail) section of the
     * formatter class specification.
     *
     * @throws  NullPointerException
     * If the `format` is `null`
     *
     * @return  This writer
     *
     * @since  1.5
     */
    fun printf(l: Locale?, format: String?, vararg args: Object?): PrintWriter {
        return format(l, format, *args)
    }

    /**
     * Writes a formatted string to this writer using the specified format
     * string and arguments.  If automatic flushing is enabled, calls to this
     * method will flush the output buffer.
     *
     *
     *  The locale always used is the one returned by [ ][java.util.Locale.getDefault], regardless of any
     * previous invocations of other formatting methods on this object.
     *
     * @param  format
     * A format string as described in [Format string syntax](../util/Formatter.html#syntax).
     *
     * @param  args
     * Arguments referenced by the format specifiers in the format
     * string.  If there are more arguments than format specifiers, the
     * extra arguments are ignored.  The number of arguments is
     * variable and may be zero.  The maximum number of arguments is
     * limited by the maximum dimension of a Java array as defined by
     * <cite>The Java Virtual Machine Specification</cite>.
     * The behaviour on a
     * `null` argument depends on the [conversion](../util/Formatter.html#syntax).
     *
     * @throws  java.util.IllegalFormatException
     * If a format string contains an illegal syntax, a format
     * specifier that is incompatible with the given arguments,
     * insufficient arguments given the format string, or other
     * illegal conditions.  For specification of all possible
     * formatting errors, see the [Details](../util/Formatter.html#detail) section of the
     * Formatter class specification.
     *
     * @throws  NullPointerException
     * If the `format` is `null`
     *
     * @return  This writer
     *
     * @since  1.5
     */
    fun format(format: String?, vararg args: Object?): PrintWriter {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implFormat(format, *args)
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implFormat(format, *args)
            }
        }
        return this
    }

    private fun implFormat(format: String?, vararg args: Object) {
        try {
            ensureOpen()
            if ((formatter == null)
                || (formatter.locale() !== Locale.getDefault())
            ) formatter = Formatter(this)
            formatter.format(Locale.getDefault(), format, args)
            if (autoFlush) out.flush()
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Writes a formatted string to this writer using the specified format
     * string and arguments.  If automatic flushing is enabled, calls to this
     * method will flush the output buffer.
     *
     * @param  l
     * The [locale][java.util.Locale] to apply during
     * formatting.  If `l` is `null` then no localization
     * is applied.
     *
     * @param  format
     * A format string as described in [Format string syntax](../util/Formatter.html#syntax).
     *
     * @param  args
     * Arguments referenced by the format specifiers in the format
     * string.  If there are more arguments than format specifiers, the
     * extra arguments are ignored.  The number of arguments is
     * variable and may be zero.  The maximum number of arguments is
     * limited by the maximum dimension of a Java array as defined by
     * <cite>The Java Virtual Machine Specification</cite>.
     * The behaviour on a
     * `null` argument depends on the [conversion](../util/Formatter.html#syntax).
     *
     * @throws  java.util.IllegalFormatException
     * If a format string contains an illegal syntax, a format
     * specifier that is incompatible with the given arguments,
     * insufficient arguments given the format string, or other
     * illegal conditions.  For specification of all possible
     * formatting errors, see the [Details](../util/Formatter.html#detail) section of the
     * formatter class specification.
     *
     * @throws  NullPointerException
     * If the `format` is `null`
     *
     * @return  This writer
     *
     * @since  1.5
     */
    fun format(l: Locale?, format: String?, vararg args: Object?): PrintWriter {
        val lock: Object = this.lock
        if (lock is InternalLock) {
            lock.lock()
            try {
                implFormat(l, format, *args)
            } finally {
                lock.unlock()
            }
        } else {
            synchronized(lock) {
                implFormat(l, format, *args)
            }
        }
        return this
    }

    private fun implFormat(l: Locale, format: String, vararg args: Object) {
        try {
            ensureOpen()
            if ((formatter == null) || (formatter.locale() !== l)) formatter = Formatter(this, l)
            formatter.format(l, format, args)
            if (autoFlush) out.flush()
        } catch (x: InterruptedIOException) {
            Thread.currentThread().interrupt()
        } catch (x: IOException) {
            trouble = true
        }
    }

    /**
     * Appends the specified character sequence to this writer.
     *
     *
     *  An invocation of this method of the form `out.append(csq)`
     * when `csq` is not `null`, behaves in exactly the same way
     * as the invocation
     *
     * {@snippet lang=java :
     * *     out.write(csq.toString())
     * * }
     *
     *
     *  Depending on the specification of `toString` for the
     * character sequence `csq`, the entire sequence may not be
     * appended. For instance, invoking the `toString` method of a
     * character buffer will return a subsequence whose content depends upon
     * the buffer's position and limit.
     *
     * @param  csq
     * The character sequence to append.  If `csq` is
     * `null`, then the four characters `"null"` are
     * appended to this writer.
     *
     * @return  This writer
     *
     * @since  1.5
     */
    fun append(csq: CharSequence?): PrintWriter {
        write(String.valueOf(csq))
        return this
    }

    /**
     * Appends a subsequence of the specified character sequence to this writer.
     *
     *
     *  An invocation of this method of the form
     * `out.append(csq, start, end)`
     * when `csq` is not `null`, behaves in
     * exactly the same way as the invocation
     *
     * {@snippet lang=java :
     * *     out.write(csq.subSequence(start, end).toString())
     * * }
     *
     * @param  csq
     * The character sequence from which a subsequence will be
     * appended.  If `csq` is `null`, then characters
     * will be appended as if `csq` contained the four
     * characters `"null"`.
     *
     * @param  start
     * The index of the first character in the subsequence
     *
     * @param  end
     * The index of the character following the last character in the
     * subsequence
     *
     * @return  This writer
     *
     * @throws  IndexOutOfBoundsException
     * If `start` or `end` are negative, `start`
     * is greater than `end`, or `end` is greater than
     * `csq.length()`
     *
     * @since  1.5
     */
    fun append(csq: CharSequence?, start: Int, end: Int): PrintWriter {
        var csq = csq
        if (csq == null) csq = "null"
        return append(csq.subSequence(start, end))
    }

    /**
     * Appends the specified character to this writer.
     *
     *
     *  An invocation of this method of the form `out.append(c)`
     * behaves in exactly the same way as the invocation
     *
     * {@snippet lang=java :
     * *     out.write(c)
     * * }
     *
     * @param  c
     * The 16-bit character to append
     *
     * @return  This writer
     *
     * @since 1.5
     */
    fun append(c: Char): PrintWriter {
        write(c.code)
        return this
    }

    companion object {
        /**
         * Returns a charset object for the given charset name.
         * @throws NullPointerException          is csn is null
         * @throws UnsupportedEncodingException  if the charset is not supported
         */
        @kotlin.Throws(UnsupportedEncodingException::class)
        private fun toCharset(csn: String): Charset {
            Objects.requireNonNull(csn, "charsetName")
            try {
                return Charset.forName(csn)
            } catch (unused: IllegalCharsetNameException) {
                // UnsupportedEncodingException should be thrown
                throw UnsupportedEncodingException(csn)
            } catch (unused: UnsupportedCharsetException) {
                throw UnsupportedEncodingException(csn)
            }
        }

        init {
            SharedSecrets.setJavaIOCPrintWriterAccess(object : JavaIOPrintWriterAccess() {
                fun lock(pw: PrintWriter): Object {
                    return pw.lock
                }
            })
        }
    }
}
// endregion
