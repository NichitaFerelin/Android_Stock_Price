package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.NotifyPriceRepository
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface NotifyPriceUseCase {
  val notifyPriceState: Flow<Boolean>
  suspend fun setNotifyPrice(notify: Boolean)
}

@Reusable
internal class NotifyPriceUseCaseImpl @Inject constructor(
  private val notifyPriceRepository: NotifyPriceRepository,
  dispatchersProvider: DispatchersProvider
) : NotifyPriceUseCase {
  override val notifyPriceState: Flow<Boolean> = notifyPriceRepository.notifyPrice
    .distinctUntilChanged()
    .flowOn(dispatchersProvider.IO)

  override suspend fun setNotifyPrice(notify: Boolean) {
    notifyPriceRepository.setNotifyPrice(notify)
  }
}