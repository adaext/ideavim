/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action

import com.maddyhome.idea.vim.api.injector
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class CommandCountTest : VimTestCase() {
  fun `test count operator motion`() {
    configureByText("${c}1234567890")
    typeText(injector.parser.parseKeys("3dl"))
    assertState("4567890")
  }

  fun `test operator count motion`() {
    configureByText("${c}1234567890")
    typeText(injector.parser.parseKeys("d3l"))
    assertState("4567890")
  }

  fun `test count operator count motion`() {
    configureByText("${c}1234567890")
    typeText(injector.parser.parseKeys("2d3l"))
    assertState("7890")
  }

  // See https://github.com/vim/vim/blob/b376ace1aeaa7614debc725487d75c8f756dd773/src/normal.c#L631
  fun `test count resets to 999999999L if gets too large`() {
    configureByText("1")
    typeText(injector.parser.parseKeys("12345678901234567890<C-A>"))
    assertState("1000000000")
  }

  fun `test count select register count operator count motion`() {
    configureByText("${c}123456789012345678901234567890")
    typeText(injector.parser.parseKeys("2\"a3d4l")) // Delete 24 characters
    assertState("567890")
  }

  @TestWithoutNeovim(SkipNeovimReason.TABS)
  fun `test multiple select register counts`() {
    configureByText("${c}12345678901234567890123456789012345678901234567890")
    typeText(injector.parser.parseKeys("2\"a2\"b2\"b2d2l")) // Delete 32 characters
    assertState("345678901234567890")
  }
}
