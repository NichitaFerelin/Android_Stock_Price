package com.ferelin.core.ui.params

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileParams(
  val companyId: Int
) : Parcelable