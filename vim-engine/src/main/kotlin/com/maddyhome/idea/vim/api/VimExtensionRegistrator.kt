/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.api

interface VimExtensionRegistrator {
  fun setOptionByPluginAlias(alias: String): Boolean
  fun getExtensionNameByAlias(alias: String): String?
}
