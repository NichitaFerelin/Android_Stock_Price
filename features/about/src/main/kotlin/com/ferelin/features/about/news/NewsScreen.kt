package com.ferelin.features.about.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ferelin.features.about.news.component.NewsItem

@Composable
internal fun NewsRoute(newsViewModel: NewsViewModel) {
  val uiState by newsViewModel.uiState.collectAsState()

  NewsScreen(
    newsStateUi = uiState,
    onUrlClick = { }
  )
}

@Composable
internal fun NewsScreen(
  newsStateUi: NewsStateUi,
  onUrlClick: (String) -> Unit
) {
  LazyRow {
    items(
      items = newsStateUi.news
    ) { newsViewData ->
      NewsItem(
        modifier = Modifier.clickable { onUrlClick.invoke(newsViewData.sourceUrl) },
        source = newsViewData.source,
        sourceUrl = newsViewData.sourceUrl,
        date = newsViewData.date,
        title = newsViewData.headline,
        content = newsViewData.summary
      )
    }
  }
}