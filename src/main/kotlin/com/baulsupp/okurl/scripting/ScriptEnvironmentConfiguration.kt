package com.baulsupp.okurl.scripting

import okhttp3.OkHttpClient
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.fileExtension
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

class ScriptEnvironmentConfiguration : ScriptCompilationConfiguration({
  defaultImports.append(
//    "com.baulsupp.okurl.kotlin.client",
//    "com.baulsupp.okurl.kotlin.args",
    "com.baulsupp.okurl.kotlin.queryList",
    "com.baulsupp.okurl.kotlin.query",
    "kotlinx.coroutines.runBlocking"
  )
  jvm {
    dependenciesFromCurrentContext()
  }
  fileExtension("oks.kts")
  providedProperties(
    "args" to List::class.createType(arguments = listOf(KTypeProjection.invariant(String::class.createType()))),
    "client" to OkHttpClient::class.starProjectedType
  )
})

