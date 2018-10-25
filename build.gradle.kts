import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  kotlin("jvm") version Versions.kotlin
  `maven-publish`
  distribution
  id("com.github.ben-manes.versions") version "0.20.0"
  id("org.jlleitschuh.gradle.ktlint") version "6.2.0"
  id("com.jfrog.bintray") version "1.8.4"
  id("org.jetbrains.dokka") version "0.9.17"
  id("net.nemerosa.versioning") version "2.8.2"
}

repositories {
  mavenLocal()
  jcenter()
  mavenCentral()
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  maven(url = "https://jitpack.io")
  maven(url = "http://repo.maven.apache.org/maven2")
  maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
  maven(url = "https://repo.spring.io/milestone/")
  maven(url = "https://dl.bintray.com/reactivesocket/RSocket/")
  maven(url = "https://oss.sonatype.org/content/repositories/releases/")
  maven(url = "https://dl.bintray.com/yschimke/baulsupp.com/")
}

group = "com.baulsupp"
val artifactID = "okurl"
description = "OkHttp Kotlin CLI"
val projectVersion = versioning.info.display
//println("Version $projectVersion")
version = projectVersion

base {
  archivesBaseName = "okurl"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.languageVersion = "1.3"
  }
}

tasks {
  "dokka"(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
  }
  withType<GenerateMavenPom> {
    destination = file("$buildDir/libs/${jar.get().baseName}.pom")
  }
}

dependencies {
  implementation(Deps.activation)
  implementation(Deps.airline)
  implementation(Deps.bouncyCastle)
  implementation(Deps.brave)
  implementation(Deps.braveInstrumentationOkhttp3)
  implementation(Deps.braveOkhttp3)
  implementation(Deps.brotli)
  implementation(Deps.byteunits)
  implementation(Deps.byteUnits)
  implementation(Deps.commonsIo)
  implementation(Deps.commonsLang)
  implementation(Deps.conscryptUber)
  implementation(Deps.coroutinesCore)
  implementation(Deps.coroutinesJdk8)
  implementation(Deps.jacksonCbor)
  implementation(Deps.jacksonDatabind)
  implementation(Deps.jacksonJdk8)
  implementation(Deps.jacksonJsr310)
  implementation(Deps.jacksonKotlin)
  implementation(Deps.jacksonParams)
  implementation(Deps.jacksonYaml)
  implementation(Deps.jansi)
  implementation(Deps.jffi)
  implementation(Deps.jjwt)
  implementation(Deps.jjwtImpl)
  implementation(Deps.jjwtJackson)
  implementation(Deps.jkeychain)
  implementation(Deps.jnrUnixSocket)
  implementation(Deps.jsr305)
  implementation(Deps.kotlinCompilerEmbeddable)
  implementation(Deps.kotlinReflect)
  implementation(Deps.kotlinStandardLibrary)
  implementation(Deps.moshi)
  implementation(Deps.moshiAdapters)
  implementation(Deps.moshiKotlin)
  implementation(Deps.nettyResolveDns)
  implementation(Deps.ok2Curl)
  implementation(Deps.okhttp)
  implementation(Deps.okhttpDigest)
  implementation(Deps.okhttpDoh)
  implementation(Deps.okhttpLoggingInterceptor)
  implementation(Deps.okhttpSse)
  implementation(Deps.okhttpTls)
  implementation(Deps.okhttpUDS)
  implementation(Deps.okio)
  implementation(Deps.oksocialOutput)
  implementation(Deps.proxyVol)
  implementation(Deps.slf4jApi)
  implementation(Deps.slf4jJdk14)
  implementation(Deps.tink)
  implementation(Deps.zipkin)
  implementation(Deps.zipkinSenderOkhttp3)
  implementation(Deps.ztExec)

  implementation(Deps.kotlinScriptUtil) {
    exclude(module = "kotlin-compiler")
  }

  testImplementation(Deps.junitJupiterApi)
  testImplementation(Deps.kotlinTest)
  testImplementation(Deps.kotlinTestJunit)
  testImplementation(Deps.okhttpTesting)
  testImplementation(Deps.okhttpMWS)
  testImplementation(Deps.conscryptUber)

  testRuntime(Deps.junitJupiterEngine)
  testRuntime(Deps.slf4jJdk14)
}

val sourcesJar by tasks.creating(Jar::class) {
  classifier = "sources"
  from(kotlin.sourceSets["main"].kotlin)
}

val javadocJar by tasks.creating(Jar::class) {
  classifier = "javadoc"
  from("$buildDir/javadoc")
}

val jar = tasks["jar"] as org.gradle.jvm.tasks.Jar

fun MavenPom.addDependencies() = withXml {
  asNode().appendNode("dependencies").let { depNode ->
    configurations.implementation.get().allDependencies.forEach {
      depNode.appendNode("dependency").apply {
        appendNode("groupId", it.group)
        appendNode("artifactId", it.name)
        appendNode("version", it.version)
      }
    }
  }
}

publishing {
  publications {
    create("mavenJava", MavenPublication::class) {
      artifactId = artifactID
      groupId = project.group.toString()
      version = project.version.toString()
      description = project.description
      artifact(jar)
      artifact(sourcesJar) {
        classifier = "sources"
      }
      artifact(javadocJar) {
        classifier = "javadoc"
      }
      pom.addDependencies()
      pom {
        packaging = "jar"
        developers {
          developer {
            email.set("yuri@schimke.ee")
            id.set("yschimke")
            name.set("Yuri Schimke")
          }
        }
        licenses {
          license {
            name.set("Apache License")
            url.set("http://opensource.org/licenses/apache-2.0")
            distribution.set("repo")
          }
        }
        scm {
          connection.set("scm:git:https://github.com/yschimke/okurl.git")
          developerConnection.set("scm:git:git@github.com:yschimke/okurl.git")
          url.set("https://github.com/yschimke/okurl.git")
        }
      }
    }
  }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
  user = findProperty("baulsuppBintrayUser")
  key = findProperty("baulsuppBintrayKey")
  publish = true
  setPublications("mavenJava")
  pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
    repo = "baulsupp.com"
    name = "okurl"
    userOrg = user
    websiteUrl = "https://github.com/yschimke/okurl"
    githubRepo = "yschimke/okurl"
    vcsUrl = "https://github.com/yschimke/okurl.git"
    desc = project.description
    setLabels("kotlin")
    setLicenses("Apache-2.0")
    version(delegateClosureOf<BintrayExtension.VersionConfig> {
      name = project.version.toString()
    })
  })
}

distributions {
  getByName("main") {
    contents {
      from("${rootProject.projectDir}") {
        include("README.md", "LICENSE")
      }
      from("${rootProject.projectDir}/src/main/scripts") {
        fileMode = Integer.parseUnsignedInt("755", 8)
        into("bin")
      }
      from("${rootProject.projectDir}/certificates") {
        into("certificates")
      }
      from("${rootProject.projectDir}/src/test/kotlin/commands") {
        fileMode = Integer.parseUnsignedInt("755", 8)
        exclude("local")
        into("bin")
      }
      from("${rootProject.projectDir}/bash") {
        into("bash")
      }
      from("${rootProject.projectDir}/zsh") {
        into("zsh")
      }
      from("${rootProject.projectDir}/src/main/resources") {
        into("scripts")
      }
      into("lib") {
        from(jar)
      }
      into("lib") {
        from(configurations.runtimeClasspath)
      }
    }
  }
}
