package com.ferelin.core.domain.entities.entity

data class StockPrice(
  val id: CompanyId,
  val currentPrice: Double,
  val previousClosePrice: Double,
  val openPrice: Double,
  val highPrice: Double,
  val lowPrice: Double
)