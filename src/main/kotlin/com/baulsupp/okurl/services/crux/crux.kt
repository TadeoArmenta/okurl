package com.baulsupp.okurl.services.crux

data class Identity(val lastName: String?, val website: String?, val parentIdentityId: String?, val role: String? = "",
                    val modifiedAt: String, val companyName: String?, val landingPage: String?, val description: String?,
                    val type: String, val firstName: String?, val createdAt: String, val phone: String?,
                    val identityId: String, val email: String)

data class Dataset(val createdAt: String,
                   val website: String?,
                   val imageName: String?,
                   val modifiedAt: String,
                   val imageUrl: String?,
                   val contactIdentityId: String,
                   val name: String,
                   val datasetId: String,
                   val description: String?,
                   val ownerIdentityId: String,
                   val tags: List<String>)

data class Drive(val owned: List<Dataset>, val subscriptions: List<Dataset>)
