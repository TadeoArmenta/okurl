#!/usr/bin/env ./okscript

data class Contributor(val login: String, val contributions: Int, val avatar_url: String, val url: String)

val repo = args.getOrElse(0) { "square/okhttp" }

println("SCRIPT")

val contributors = runBlocking {
  client.queryList<Contributor>("https://api.github.com/repos/$repo/contributors")
}

contributors.forEach {
  println("${it.login}: ${it.contributions}")
}
