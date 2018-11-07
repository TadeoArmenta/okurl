package com.baulsupp.okurl.services.crux

import com.baulsupp.oksocial.output.OutputHandler
import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.authenticator.ValidatedCredentials
import com.baulsupp.okurl.authenticator.oauth2.Oauth2ServiceDefinition
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.completion.ApiCompleter
import com.baulsupp.okurl.completion.BaseUrlCompleter
import com.baulsupp.okurl.completion.CompletionVariableCache
import com.baulsupp.okurl.completion.UrlList
import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.Token
import com.baulsupp.okurl.credentials.TokenValue
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.secrets.Secrets
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class CruxAuthInterceptor : AuthInterceptor<Oauth2Token>() {
  override val serviceDefinition = Oauth2ServiceDefinition("newapi.cruxinformatics.com", "Crux API", "crux",
    "https://app.cruxinformatics.com/docs/api-reference/curl/", "https://app.cruxinformatics.com/profile/inquiries")

  override suspend fun intercept(chain: Interceptor.Chain, credentials: Oauth2Token): Response {
    var request = chain.request()

    request = request.newBuilder().addHeader("Authorization", "Bearer ${credentials.accessToken}").build()

    return chain.proceed(request)
  }

  override suspend fun authorize(
    client: OkHttpClient,
    outputHandler: OutputHandler<Response>,
    authArguments: List<String>
  ): Oauth2Token {
    val apiKey = Secrets.prompt("Crux API Key", "crux.apiKey", "", false)

    return Oauth2Token(apiKey)
  }

  override suspend fun validate(
    client: OkHttpClient,
    credentials: Oauth2Token
  ): ValidatedCredentials {
    val identity = client.query<Identity>("https://newapi.cruxinformatics.com/plat-api/identities/whoami", TokenValue(credentials))
    return ValidatedCredentials("${identity.firstName.orEmpty()} ${identity.lastName.orEmpty()}", identity.companyName)
  }

  override suspend fun apiCompleter(
    prefix: String,
    client: OkHttpClient,
    credentialsStore: CredentialsStore,
    completionVariableCache: CompletionVariableCache,
    tokenSet: Token
  ): ApiCompleter {
    val urlList = UrlList.fromResource(name())

    val completer = BaseUrlCompleter(urlList!!, hosts(credentialsStore), completionVariableCache)

    completer.withCachedVariable(name(), "datasetId") {
      credentialsStore.get(serviceDefinition, tokenSet)?.let {
        val public = client.queryList<Dataset>("https://newapi.cruxinformatics.com/plat-api/datasets/public", tokenSet).map { it.datasetId }
        val user = client.query<Drive>("https://newapi.cruxinformatics.com/plat-api/drives/my", tokenSet).let {
          it.owned + it.subscriptions
        }.map { it.datasetId }
        public + user
      }
    }
    completer.withCachedVariable(name(), "identityId") {
      credentialsStore.get(serviceDefinition, tokenSet)?.let {
        listOf(client.query<Identity>("https://newapi.cruxinformatics.com/plat-api/identities/whoami", tokenSet).identityId)
      }
    }

    return completer
  }

  override fun canRenew(credentials: Oauth2Token): Boolean = false

  override fun hosts(credentialsStore: CredentialsStore): Set<String> = setOf("newapi.cruxinformatics.com")
}
