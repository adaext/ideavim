/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.newapi

import com.intellij.openapi.editor.RangeMarker
import com.maddyhome.idea.vim.common.LiveRange
import com.maddyhome.idea.vim.common.Offset
import com.maddyhome.idea.vim.common.offset

class IjLiveRange(val marker: RangeMarker) : LiveRange {
  override val startOffset: Offset
    get() = marker.startOffset.offset
}

val RangeMarker.vim: LiveRange
  get() = IjLiveRange(this)

val LiveRange.ij: RangeMarker
  get() = (this as IjLiveRange).marker
