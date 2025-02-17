/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.statements

import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class TryCatchTest : VimTestCase() {

  fun `test successful catch of a throw statement`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          "  throw 'my exception' |" +
          "catch /my exception/ |" +
          "  echo 'caught' |" +
          "endtry"
      )
    )
    assertPluginError(false)
    assertExOutput("caught\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.PLUGIN_ERROR)
  fun `test unsuccessful catch of a throw statement`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          " throw 'my exception' |" +
          "catch /E117:/ |" +
          "endtry"
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("my exception")
  }

  fun `test vim statement successful catch`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          " echo undefinedVariable |" +
          "catch /E121: Undefined variable:/ |" +
          "endtry"
      )
    )
    assertPluginError(false)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.PLUGIN_ERROR)
  fun `test vim statement unsuccessful catch`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          " echo undefinedVariable |" +
          "catch /E117:/ |" +
          "endtry"
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("E121: Undefined variable: undefinedVariable")
  }

  fun `test multiple catches`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          "  throw 'my exception' |" +
          "catch /E117:/ |" +
          "  echo 'failure' |" +
          "catch /my exception/ |" +
          "endtry"
      )
    )
    assertPluginError(false)
    assertNoExOutput()
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.PLUGIN_ERROR)
  fun `test no matching catch among multiple`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          "  throw 'my exception' |" +
          "catch /E117:/ |" +
          "catch /E118:/ |" +
          "endtry"
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("my exception")
  }

  fun `test finally after catch`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          "  throw 'my exception' |" +
          "catch /E117:/ |" +
          "catch /E118:/ |" +
          "catch /my exception/ |" +
          "finally |" +
          "  echo 'finally block' |" +
          "endtry"
      )
    )
    assertPluginError(false)
    assertExOutput("finally block\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.PLUGIN_ERROR)
  fun `test finally after unsuccessful catch`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        "" +
          "try |" +
          "  throw 'my exception' |" +
          "catch /E117:/ |" +
          "catch /E118:/ |" +
          "finally |" +
          "  echo 'finally block' |" +
          "endtry"
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("my exception")
    assertExOutput("finally block\n")
  }

  fun `test finish in try catch`() {
    configureByText("\n")
    typeText(
      commandToKeys(
        """
        let x = 0 |
        let y = 0 |
        try |
          finish |
          let x = 1 |
        finally |
          let y = 1 |
        endtry |
        """.trimIndent()
      )
    )
    typeText(commandToKeys("echo x .. ' ' .. y"))
    assertExOutput("0 1\n")
  }
}
