package com.baulsupp.okurl.scripting

import okhttp3.OkHttpClient
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
  displayName = "OkUrl Script",
  fileExtension = "oks.kts",
  compilationConfiguration = ScriptEnvironmentConfiguration::class
)
open class ScriptEnvironment(val args: List<String>, val client: OkHttpClient) {
  suspend open fun runScript() {

  }
}
