/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.helper;

import com.maddyhome.idea.vim.api.VimInjectorKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ideavim.VimTestCase;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.util.List;

/**
 * @author vlan
 */
public class StringHelperTest extends VimTestCase {

  public void testFalseSpecialKey() {
    List<KeyStroke> expectedKeys = VimInjectorKt.getInjector().getParser().parseKeys("move '<-2");
    expectedKeys.addAll(VimInjectorKt.getInjector().getParser().parseKeys("<CR>"));
    assertEquals(expectedKeys, VimInjectorKt.getInjector().getParser().parseKeys("move '<-2<CR>"));
  }

  public void testParseKeyModifiers() {
    assertTypedKeyStroke('C', "C");
    assertTypedKeyStroke('c', "c");

    assertPressedKeyStroke("control C", "<C-C>");
    assertPressedKeyStroke("control C", "<C-c>");
    assertPressedKeyStroke("control C", "<c-c>");

    assertPressedKeyStroke("alt C", "<A-C>");
    assertPressedKeyStroke("alt C", "<a-c>");

    assertPressedKeyStroke("alt C", "<M-C>");

    assertPressedKeyStroke("meta C", "<D-C>");

    assertPressedKeyStroke("control shift C", "<C-S-C>");
    assertPressedKeyStroke("alt shift C", "<A-S-C>");
    assertPressedKeyStroke("control alt C", "<C-A-C>");
    assertPressedKeyStroke("control alt shift C", "<C-A-S-C>");
    assertPressedKeyStroke("control alt shift C", "<C-A-S-C>");
    assertPressedKeyStroke("meta control alt shift C", "<D-C-A-S-C>");
  }

  public void testParseSpecialKeys() {
    assertTypedKeyStroke('<', "<");
    assertTypedKeyStroke('>', ">");
    assertTypedKeyStroke('\\', "\\");
    assertTypedKeyStroke('\\', "<Leader>");

    assertPressedKeyStroke("ESCAPE", "<Esc>");
    assertPressedKeyStroke("ENTER", "<Enter>");
    assertPressedKeyStroke("ENTER", "<Return>");
    assertPressedKeyStroke("ENTER", "<CR>");
  }

  // VIM-645
  public void testParseSpaceAsTyped() {
    assertTypedKeyStroke(' ', "<Space>");
  }

  // VIM-660
  public void testParseCtrlSpace() {
    assertPressedKeyStroke("control SPACE", "<C-Space>");
  }

  // VIM-655
  public void testParseTypedShiftChar() {
    assertTypedKeyStroke('H', "<S-h>");
  }

  // VIM-651
  public void testParseBackspace() {
    assertPressedKeyStroke("BACK_SPACE", "<BS>");
    assertPressedKeyStroke("BACK_SPACE", "<Backspace>");
  }

  // VIM-666
  public void testParseBarSpecialKey() {
    assertTypedKeyStroke('|', "<Bar>");
  }

  // VIM-679
  public void testControlXCharacter() {
    assertPressedKeyStroke("control X", "\u0018");
  }

  public void testControlBoundCharacters() {
    assertKeyStroke(KeyStroke.getKeyStroke('@', InputEvent.CTRL_DOWN_MASK), "\u0000");
    assertKeyStroke(KeyStroke.getKeyStroke('_', InputEvent.CTRL_DOWN_MASK), "\u001F");
  }

  public void testControlExceptionCharacters() {
    assertPressedKeyStroke("TAB", "\t"); // U+0009
    assertPressedKeyStroke("ENTER", "\n"); // U+000A
  }

  private void assertPressedKeyStroke(@NotNull String expected, @NotNull String actual) {
    assertEquals(KeyStroke.getKeyStroke(expected), parseKeyStroke(actual));
  }

  private void assertKeyStroke(@NotNull KeyStroke expected, @NotNull String actual) {
    assertEquals(expected, parseKeyStroke(actual));
  }

  private void assertTypedKeyStroke(char expected, @NotNull String actual) {
    assertEquals(KeyStroke.getKeyStroke(expected), parseKeyStroke(actual));
  }

  @NotNull
  private KeyStroke parseKeyStroke(@NotNull String s) {
    final List<KeyStroke> actualStrokes = VimInjectorKt.getInjector().getParser().parseKeys(s);
    assertEquals(VimInjectorKt.getInjector().getParser().toKeyNotation(actualStrokes), 1, actualStrokes.size());
    return actualStrokes.get(0);
  }
}
