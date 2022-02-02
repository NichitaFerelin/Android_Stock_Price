package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.StoragePathRepository
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface StoragePathUseCase {
  val storagePath: Flow<StoragePath>
  suspend fun setStoragePath(path: String, authority: String)
}

@Reusable
internal class StoragePathUseCaseImpl @Inject constructor(
  private val storagePathRepository: StoragePathRepository,
  private val dispatchersProvider: DispatchersProvider
) : StoragePathUseCase {
  override val storagePath: Flow<StoragePath>
    get() = storagePathRepository.path
      .combine(
        flow = storagePathRepository.authority,
        transform = { path, authority ->
          StoragePath(path, authority)
        }
      )
      .flowOn(dispatchersProvider.IO)

  override suspend fun setStoragePath(path: String, authority: String) {
    storagePathRepository.setStoragePath(path, authority)
  }
}