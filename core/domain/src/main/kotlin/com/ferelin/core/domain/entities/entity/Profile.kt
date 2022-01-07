package com.ferelin.core.domain.entities.entity

data class Profile(
  val id: CompanyId,
  val country: String,
  val phone: String,
  val webUrl: String,
  val industry: String,
  val capitalization: String
)