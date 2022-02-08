package com.ferelin.features.about.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.startActivitySafety
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.FailIcon
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.uiComponents.ProfileInfoColumn
import com.ferelin.features.about.uiComponents.ProfileInfoColumnClickable
import com.ferelin.features.about.uiComponents.ProfileInfoRow
import com.ferelin.features.about.uiComponents.ProfileInfoRowClickable
import com.google.accompanist.insets.statusBarsPadding
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileRoute(deps: ProfileDeps, params: ProfileParams) {
  val componentViewModel = viewModel<ProfileComponentViewModel>(
    factory = ProfileComponentViewModelFactory(deps, params)
  )
  val viewModel = viewModel<ProfileViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current

  ProfileScreen(
    uiState = uiState,
    onShareClick = {
      with(uiState.profile) {
        val shareText = "$companyName [$country]\n$phone\n$webUrl"
        val intent = Intent(Intent.ACTION_SEND).apply {
          putExtra(Intent.EXTRA_TEXT, shareText)
          type = "text/plain"
        }
        context.startActivitySafety(intent)
      }
    },
    onUrlClick = {
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse(uiState.profile.webUrl)
      context.startActivitySafety(intent)
    },
    onPhoneClick = {
      val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.fromParts("tel", uiState.profile.phone, null)
      )
      context.startActivitySafety(intent)
    }
  )
}

@Composable
private fun ProfileScreen(
  uiState: ProfileStateUi,
  onShareClick: () -> Unit,
  onUrlClick: () -> Unit,
  onPhoneClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
      .verticalScroll(rememberScrollState())
  ) {
    ClickableIcon(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 6.dp, end = 16.dp),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      imageVector = Icons.Default.Share,
      contentDescription = stringResource(id = R.string.descriptionShare),
      iconTint = AppTheme.colors.buttonPrimary,
      onClick = onShareClick
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(12.dp))
      GlideImage(
        modifier = Modifier
          .size(50.dp)
          .clip(CircleShape),
        imageModel = uiState.profile.logoUrl,
        failure = { FailIcon() }
      )
      Spacer(modifier = Modifier.height(12.dp))
      ProfileInfoColumn(
        name = stringResource(R.string.hintName),
        content = uiState.profile.companyName
      )
      Spacer(modifier = Modifier.height(12.dp))

      if (uiState.profile.webUrl.isNotEmpty()) {
        ProfileInfoColumnClickable(
          name = stringResource(R.string.hintWebsite),
          content = uiState.profile.webUrl,
          onClick = onUrlClick
        )
        Spacer(modifier = Modifier.height(6.dp))
      }
      Divider(
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.contendSecondary
      )
      Spacer(modifier = Modifier.height(6.dp))

      if (uiState.profile.country.isNotEmpty()) {
        ProfileInfoRow(
          name = stringResource(R.string.hintCountry),
          content = uiState.profile.country
        )
        Spacer(modifier = Modifier.height(14.dp))
      }
      if (uiState.profile.industry.isNotEmpty()) {
        ProfileInfoRow(
          name = stringResource(R.string.hintIndustry),
          content = uiState.profile.industry
        )
        Spacer(modifier = Modifier.height(14.dp))
      }
      if (uiState.profile.phone.isNotEmpty()) {
        ProfileInfoRowClickable(
          name = stringResource(R.string.hintPhone),
          content = uiState.profile.phone,
          onClick = onPhoneClick
        )
        Spacer(modifier = Modifier.height(14.dp))
      }
      if (uiState.profile.capitalization.isNotEmpty()) {
        ProfileInfoRow(
          name = stringResource(R.string.hintCapitalization),
          content = uiState.profile.capitalization
        )
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}