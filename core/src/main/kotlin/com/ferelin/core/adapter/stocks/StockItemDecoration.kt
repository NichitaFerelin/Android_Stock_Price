/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.core.adapter.stocks

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.R
import com.ferelin.shared.NULL_INDEX

class StockItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val bottomMargin = context.resources.getDimension(R.dimen.stockItemBottomMargin).toInt()
    private val startMargin = context.resources.getDimension(R.dimen.stockItemStartMargin).toInt()
    private val endMargin = context.resources.getDimension(R.dimen.stockItemEndMargin).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        when {
            position == NULL_INDEX -> outRect.applyForStock()
            parent.adapter?.getItemViewType(position) == STOCK_VIEW_TYPE -> outRect.applyForStock()
        }
    }

    private fun Rect.applyForStock() {
        bottom = bottomMargin
        left = startMargin
        right = endMargin
    }
}