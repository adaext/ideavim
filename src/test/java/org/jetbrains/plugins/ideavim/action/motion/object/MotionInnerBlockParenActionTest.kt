/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.motion.`object`

import com.maddyhome.idea.vim.api.injector
import org.jetbrains.plugins.ideavim.VimTestCase

class MotionInnerBlockParenActionTest : VimTestCase() {
  // VIM-1633 |v_i)|
  fun `test single letter with single parentheses`() {
    configureByText("(${c}a)")
    typeText(injector.parser.parseKeys("vi)"))
    assertSelection("a")
  }

  fun `test single letter with double parentheses`() {
    configureByText("((${c}a))")
    typeText(injector.parser.parseKeys("vi)"))
    assertSelection("(a)")
  }

  fun `test multiline outside parentheses`() {
    configureByText(
      """(outer
                        |$c(inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vi)"))
    assertSelection("inner")
  }

  fun `test multiline in parentheses`() {
    configureByText(
      """(outer
                        |(inner$c))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vi)"))
    assertSelection("inner")
  }

  fun `test multiline inside of outer parentheses`() {
    configureByText(
      """(outer
                         |$c (inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vi)"))
    assertSelection(
      """outer
                        | (inner)""".trimMargin()
    )
  }

  fun `test double motion`() {
    configureByText(
      """(outer
                      |$c(inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vi)i)"))
    assertSelection(
      """outer
                          |(inner)""".trimMargin()
    )
  }

  fun `test motion with count`() {
    configureByText(
      """(outer
                          |$c(inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("v2i)"))
    assertSelection(
      """outer
                      |(inner)""".trimMargin()
    )
  }

  fun `test text object after motion`() {
    configureByText(
      """(outer
                      |$c(inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vlli)"))
    assertSelection(
      """outer
                      |(inner)""".trimMargin()
    )
  }

  fun `test text object after motion outside parentheses`() {
    configureByText(
      """(outer
                      |(inner$c))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vlli)"))
    assertSelection("inner")
  }

  fun `test text object after motion inside parentheses`() {
    configureByText(
      """(outer
                      |(${c}inner))""".trimMargin()
    )
    typeText(injector.parser.parseKeys("vllli)"))
    assertSelection("inner")
  }

  // VIM-326 |d| |v_ib|
  fun testDeleteInnerBlock() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "foo(\"b${c}ar\")\n"
    )
    assertState("foo()\n")
  }

  // VIM-1008 |d| |v_ib|
  fun testDeleteInnerBlockWithQuote() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "(abc${c}def'ghi)"
    )
    assertState("()")
  }

  // VIM-1008 |d| |v_ib|
  fun testDeleteInnerBlockWithDoubleQuote() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      """(abc${c}def"ghi)"""
    )
    assertState("()")
  }

  // VIM-326 |d| |v_ib|
  fun testDeleteInnerBlockCaretBeforeString() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "foo(${c}\"bar\")\n"
    )
    assertState("foo()\n")
  }

  // VIM-326 |c| |v_ib|
  fun testChangeInnerBlockCaretBeforeString() {
    typeTextInFile(
      injector.parser.parseKeys("ci)"),
      "foo(${c}\"bar\")\n"
    )
    assertState("foo()\n")
  }

  // VIM-392 |c| |v_ib|
  fun testChangeInnerBlockCaretBeforeBlock() {
    typeTextInFile(
      injector.parser.parseKeys("ci)"),
      "foo$c(bar)\n"
    )
    assertState("foo()\n")
    assertOffset(4)
  }

  // |v_ib|
  fun testInnerBlockCrashWhenNoDelimiterFound() {
    typeTextInFile(injector.parser.parseKeys("di)"), "(x\n")
    assertState("(x\n")
  }

  // VIM-275 |d| |v_ib|
  fun testDeleteInnerParensBlockBeforeOpen() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "foo$c(bar)\n"
    )
    assertState("foo()\n")
    assertOffset(4)
  }

  // |d| |v_ib|
  fun testDeleteInnerParensBlockBeforeClose() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "foo(bar$c)\n"
    )
    assertState("foo()\n")
  }

  fun testOutside() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "${c}foo(bar)\n"
    )
    assertState("foo()\n")
  }

  fun testOutsideInString() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "\"1${c}23\"foo(bar)\n"
    )
    assertState("\"123\"foo()\n")
  }

  fun testOutsideInString2() {
    typeTextInFile(
      injector.parser.parseKeys("di)"),
      "\"1${c}23(dsa)d\"foo(bar)\n"
    )
    assertState("\"123()d\"foo(bar)\n")
  }
}
