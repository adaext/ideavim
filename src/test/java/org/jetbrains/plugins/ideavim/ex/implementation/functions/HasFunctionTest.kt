/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.functions

import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class HasFunctionTest : VimTestCase() {

  fun `test has for supported feature`() {
    configureByText("\n")
    typeText(commandToKeys("echo has('ide')"))
    assertExOutput("1\n")
  }

  fun `test has for unsupported feature`() {
    configureByText("\n")
    typeText(commandToKeys("echo has('autocmd')"))
    assertExOutput("0\n")
  }

  fun `test has for int as an argument`() {
    configureByText("\n")
    typeText(commandToKeys("echo has(42)"))
    assertExOutput("0\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test has for list as an argument`() {
    configureByText("\n")
    typeText(commandToKeys("echo has([])"))
    assertPluginError(true)
    assertPluginErrorMessageContains("E730: Using a List as a String")
  }
}
