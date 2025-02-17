/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import org.jetbrains.plugins.ideavim.VimTestCase

class ExecuteCommandTest : VimTestCase() {

  fun `test execute with one expression`() {
    configureByText("\n")
    typeText(commandToKeys("execute 'echo 42'"))
    assertExOutput("42\n")
  }

  fun `test execute with range`() {
    configureByText("\n")
    typeText(commandToKeys("1,2execute 'echo 42'"))
    assertNoExOutput()
    assertPluginError(true)
  }

  fun `test execute multiple expressions`() {
    configureByText("\n")
    typeText(commandToKeys("execute 'echo' 4 + 2 * 3"))
    assertExOutput("10\n")
  }

  fun `test execute adds space between expressions if missing`() {
    configureByText("\n")
    typeText(commandToKeys("execute 'echo ' . \"'result =\"4+2*3.\"'\""))
    assertExOutput("result = 10\n")
  }

  fun `test execute without spaces`() {
    configureByText("\n")
    typeText(commandToKeys("execute('echo '.42)"))
    assertExOutput("42\n")
  }
}
