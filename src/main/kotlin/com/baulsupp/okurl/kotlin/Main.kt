package com.baulsupp.okurl.kotlin

import com.baulsupp.oksocial.output.UsageException
import com.baulsupp.okurl.commands.CommandLineClient
import com.baulsupp.okurl.scripting.ScriptEnvironment
import com.github.rvesse.airline.HelpOption
import com.github.rvesse.airline.SingleCommand
import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.parser.errors.ParseException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import org.conscrypt.OpenSSLProvider
import java.io.File
import java.security.Security
import javax.inject.Inject
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.*

@Command(name = Main.NAME, description = "Kotlin scripting for APIs")
class Main : CommandLineClient() {
  @Inject
  override var help: HelpOption<Main>? = null

  override fun initialise() {
    super.initialise()

    OkShell.instance = OkShell(this)
  }

  fun createJvmScriptingHost(): BasicJvmScriptingHost {
//    val cache = FileBasedScriptCache(cacheDir)
    val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration, cache = CompiledJvmScriptsCache.NoCache)
    val evaluator = BasicJvmScriptEvaluator()
    val host = BasicJvmScriptingHost(compiler = compiler, evaluator = evaluator)
    return host
  }

  override fun runCommand(runArguments: List<String>): Int {

    if (runArguments.isEmpty()) {
      System.err.println("usage: okscript file.kts arguments")
      return -2
    }

    val scriptLocation = runArguments[0]
    com.baulsupp.okurl.kotlin.args = runArguments.drop(1)

    val scriptHost = createJvmScriptingHost()
    val scriptSource = FileScriptSource(File(scriptLocation))
    val config = ScriptEvaluationConfiguration {
      providedProperties("args" to args, "client" to client)
//      providedProperties("args" to args.toTypedArray())
    }

    val compilationConfig = createJvmCompilationConfigurationFromTemplate<ScriptEnvironment> {
      jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
      }
    }

    scriptHost.eval(scriptSource, compilationConfig, config).onFailure {
      it.reports.forEach(::println)
    }

    return 0
  }

  override fun name(): String = NAME

  companion object {
    const val NAME = "okscript"
  }
}

@ExperimentalCoroutinesApi
suspend fun main(args: Array<String>) {
  DebugProbes.install()

  Security.insertProviderAt(OpenSSLProvider(), 1)

  try {
    val command = SingleCommand.singleCommand(Main::class.java).parse(*args)
    val result = command.run()
    System.exit(result)
  } catch (e: Throwable) {
    when (e) {
      is ParseException, is UsageException -> {
        System.err.println("okurl: ${e.message}")
        System.exit(-1)
      }
      else -> {
        e.printStackTrace()
        System.exit(-1)
      }
    }
  }
}
