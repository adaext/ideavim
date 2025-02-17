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

class AnonymousFunctionTest : VimTestCase() {

  fun `test anonymous function`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {}"))
    typeText(
      commandToKeys(
        """
           function dict.pi() |
             return 3.1415 |
           endfunction
        """.trimIndent()
      )
    )
    typeText(commandToKeys("echo dict.pi()"))
    assertExOutput("3.1415\n")
  }

  // todo. `commandToKeys` is executed as if it would from the command line. so no script - no scope ¯\_(ツ)_/¯
//  fun `test anonymous function with scope`() {
//    configureByText("\n")d
//    typeText(commandToKeys("let s:dict = {}"))
//    typeText(
//      commandToKeys(
//        """
//           function s:dict.pi() |
//             return 3.1415 |
//           endfunction
//        """.trimIndent()
//      )
//    )
//    typeText(commandToKeys("echo s:dict.pi()"))
//    assertExOutput("3.1415\n")
//  }

  fun `test self in anonymous function`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {'name': 'dict'}"))
    typeText(
      commandToKeys(
        """
           function dict.getName() |
             return self.name |
           endfunction
        """.trimIndent()
      )
    )
    typeText(commandToKeys("echo dict.getName()"))
    assertExOutput("dict\n")
  }

  fun `test inner anonymous function`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {'dict2': {'dict3': {}}}"))
    typeText(
      commandToKeys(
        """
           function dict.dict2.dict3.sayHi() |
             echo 'hi' |
           endfunction
        """.trimIndent()
      )
    )
    typeText(commandToKeys("call dict.dict2.dict3.sayHi()"))
    assertExOutput("hi\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test anonymous function for non-dict variable`() {
    configureByText("\n")
    typeText(commandToKeys("let list = []"))
    typeText(
      commandToKeys(
        """
           function list.sayHi() |
             echo 'hi' |
           endfunction
        """.trimIndent()
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("E1203: Dot can only be used on a dictionary")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN_ERROR)
  fun `test dictionary function already exists`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {'abs' : function('abs')}"))
    typeText(
      commandToKeys(
        """
           function dict.abs(number) |
             if a:number > 0 |
               return a:number |
             else |
               return -a:number |
             endif |
           endfunction
        """.trimIndent()
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("E717: Dictionary entry already exists")
  }

  fun `test replace existing function`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {'abs' : function('abs')}"))
    typeText(
      commandToKeys(
        """
           function! dict.abs(number) |
             if a:number > 0 |
               return a:number |
             else |
               return -a:number |
             endif |
           endfunction
        """.trimIndent()
      )
    )
    typeText(commandToKeys("echo dict.abs(-10)"))
    assertExOutput("10\n")
  }

  @TestWithoutNeovim(SkipNeovimReason.PLUGIN)
  fun `test funcref required`() {
    configureByText("\n")
    typeText(commandToKeys("let dict = {'abs' : 'absolute'}"))
    typeText(
      commandToKeys(
        """
           function! dict.abs(number) |
             if a:number > 0 |
               return a:number |
             else |
               return -a:number |
             endif |
           endfunction
        """.trimIndent()
      )
    )
    assertPluginError(true)
    assertPluginErrorMessageContains("E718: Funcref required")
  }
}
