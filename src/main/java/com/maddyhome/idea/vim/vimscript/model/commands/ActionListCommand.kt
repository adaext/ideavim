/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.vimscript.model.commands

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.ex.ExOutputModel
import com.maddyhome.idea.vim.ex.ranges.Ranges
import com.maddyhome.idea.vim.helper.MessageHelper
import com.maddyhome.idea.vim.newapi.ij
import com.maddyhome.idea.vim.vimscript.model.ExecutionResult
import java.util.*

/**
 * @author smartbomb
 */
data class ActionListCommand(val ranges: Ranges, val argument: String) : Command.SingleExecution(ranges) {
  override val argFlags = flags(RangeFlag.RANGE_FORBIDDEN, ArgumentFlag.ARGUMENT_OPTIONAL, Access.READ_ONLY)

  override fun processCommand(editor: VimEditor, context: ExecutionContext, operatorArguments: OperatorArguments): ExecutionResult {
    val lineSeparator = "\n"
    val searchPattern = argument.trim().lowercase(Locale.getDefault()).split("*")
    val actionManager = ActionManager.getInstance()

    val actions = actionManager.getActionIdList("")
      .sortedWith(String.CASE_INSENSITIVE_ORDER)
      .map { actionName ->
        val shortcuts = actionManager.getAction(actionName).shortcutSet.shortcuts.joinToString(" ") {
          if (it is KeyboardShortcut) injector.parser.toKeyNotation(it.firstKeyStroke) else it.toString()
        }
        if (shortcuts.isBlank()) actionName else "${actionName.padEnd(50)} $shortcuts"
      }
      .filter { line -> searchPattern.all { it in line.lowercase(Locale.getDefault()) } }
      .joinToString(lineSeparator)

    ExOutputModel.getInstance(editor.ij).output(MessageHelper.message("ex.show.all.actions.0.1", lineSeparator, actions))
    return ExecutionResult.Success
  }
}
