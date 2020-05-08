/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common;

import org.junit.Test;

/**
 * Tests for {@link Utils}.
 *
 * @author Timothy Jones
 * @date 2020-02-14
 */
public class UtilsTest {

  @Test
  public void executeAndWaitForCommand_OsDependentCommand_ShouldReturnNonNullExecCommand() {
    if (Utils.isUnix() || Utils.isMac()) {
      assert (Utils.executeAndWaitForCommand("true") != null);
    }
    if (Utils.isWin()) {
      assert (Utils.executeAndWaitForCommand("ver") != null);
    }
  }

  @Test
  public void executeAndWaitForCommand_OsDependentCommand_ShouldReturnWithoutError() {
    if (Utils.isUnix() || Utils.isMac()) {
      assert (Utils.executeAndWaitForCommand("true").getError().equals(""));
    }
    if (Utils.isWin()) {
      assert (Utils.executeAndWaitForCommand("ver").getError().equals(""));
    }
  }

  @Test
  public void getTimeString_None_NumberOfSecondsPastUnixEpochShouldBePositive() {
    assert (Utils.getLong(Utils.getTimeString()) > 0);
  }

  @Test
  public void getWorkingDirectory_None_ShouldReturnSystemUserDir() {
    assert (Utils.getWorkingDirectory().equals(System.getProperty("user.dir").toString()));
  }

  @Test
  public void getFileSeparator_None_ShouldReturnSystemFileSeparator() {
    assert (Utils.getFileSeparator().equals(System.getProperty("file.separator").toString()));
  }

  @Test(expected = NullPointerException.class)
  public void getPath_Null_ThrowsNullPointerException() {
    Utils.getPath(null);
  }

  @Test
  public void
      getPath_ArrayOfStrings_ShouldReturnConcatenatedArrayAsStringWithTrailingFileSeparator() {
    String[] arr = new String[] {"abc", "def", "hij"};
    String str = String.join(Utils.getFileSeparator(), arr) + Utils.getFileSeparator();
    assert (Utils.getPath(arr).equals(str));
  }

  @Test
  public void
      getPathFile_ArrayOfStrings_ShouldReturnConcatenatedArrayAsStringWithoutTrailingFileSeparator() {
    String[] arr = new String[] {"abc", "def", "hij"};
    assert (Utils.getPathFile(arr).equals(String.join(Utils.getFileSeparator(), arr)));
  }

  @Test
  public void getNewLine_None_ShouldReturnSystemLineSeparator() {
    assert (Utils.getNewLine().equals(System.getProperty("line.separator").toString()));
  }

  @Test
  public void addIntent_FourAndString_ShouldReturnStringIndentedByFourTabs() {
    String str = "abc123";
    assert (Utils.addIndent(4, str).equals("\t\t\t\t" + str));
  }

  @Test
  public void getTabCharacter_None_ShouldReturnTab() {
    assert (Utils.getTabCharacter().equals("\t"));
  }

  @Test
  public void getTabCharacterRepeat_None_ShouldReturn3Tabs() {
    String str = Utils.getTabCharacter();
    assert (Utils.getTabCharacterRepeat(3).equals(str + str + str));
  }

  @Test
  public void isLong_LongAsObject_ShouldReturnTrue() {
    Object o = new Long(12345);
    assert (Utils.isLong(o) == true);
  }

  @Test
  public void isLong_LongAsObject_ShouldReturnFalse() {
    Object o = "abc12456";
    assert (Utils.isLong(o) == false);
  }

  @Test
  public void isDouble_DoubleAsObject_ShouldReturnTrue() {
    Object o = new Double(12.4);
    assert (Utils.isDouble(o) == true);
  }

  @Test
  public void isDouble_DoubleAsObject_ShouldReturnFalse() {
    Object o = "abc12.4";
    assert (Utils.isDouble(o) == false);
  }

  @Test
  public void isString_StringAsObject_ShouldReturnFalse() {
    Object o = 12;
    assert (Utils.isString(o) == false);
  }

  @Test
  public void isString_StringAsObject_ShouldReturnTrue() {
    Object o = "abc";
    assert (Utils.isString(o) == true);
  }

  @Test
  public void isNullRuntimeException_NotNull_ShouldReturnFalse() {
    Boolean b = Utils.isNullRuntimeException("bar", "foo");
    assert (b.equals(false));
  }

  @Test(expected = RuntimeException.class)
  public void isNullRuntimeException_Null_ShouldThrowRuntimeException() {
    Utils.isNullRuntimeException(null, "foo");
  }

  @Test
  public void getBoolean_MalformedBooleanInString_ShouldReturnNull() {
    String str = "trFalse";
    assert (Utils.getBoolean(str) == null);
  }

  @Test
  public void getBoolean_FalseInString_ShouldReturnFalse() {
    String str = "False";
    assert (Utils.getBoolean(str).equals(false));
  }

  @Test
  public void getBoolean_TrueInString_ShouldReturnTrue() {
    String str = "truE";
    assert (Utils.getBoolean(str).equals(true));
  }

  @Test
  public void getByte_MalformedByteInString_ShouldReturnNull() {
    String str = "abc123";
    assert (Utils.getByte(str) == null);
  }

  @Test
  public void getByte_ByteInString_ShouldReturnByte() {
    String str = "123";
    assert (Utils.getByte(str).equals(new Byte((byte) 123)));
  }

  @Test
  public void getCharacter_EmptyString_ShouldReturnNull() {
    String str = "";
    assert (Utils.getCharacter(str) == null);
  }

  @Test
  public void getCharacter_String_ShouldReturnFirstCharacter() {
    String str = "cadxr";
    assert (Utils.getCharacter(str).equals(new Character('c')));
  }

  @Test
  public void getCharacter_SingleCharacterInString_ShouldReturnCharacter() {
    String str = "c";
    assert (Utils.getCharacter(str).equals(new Character('c')));
  }

  @Test
  public void getShort_MalformedShortInString_ShouldReturnNull() {
    String str = "abc1234";
    assert (Utils.getShort(str) == null);
  }

  @Test
  public void getShort_ShortInString_ShouldReturnShort() {
    String str = "1234";
    assert (Utils.getShort(str).equals(new Short((short) 1234)));
  }

  @Test
  public void getInteger_MalformedIntegerInString_ShouldReturnNull() {
    String str = "abc123467";
    assert (Utils.getInteger(str) == null);
  }

  @Test
  public void getInteger_IntegerInString_ShouldReturnInteger() {
    String str = "123467";
    assert (Utils.getInteger(str).equals(new Integer(123467)));
  }

  @Test
  public void getLong_MalformedLongInString_ShouldReturnNull() {
    String str = "abc123467";
    assert (Utils.getLong(str) == null);
  }

  @Test
  public void getLong_LongInString_ShouldReturnLong() {
    String str = "123467";
    assert (Utils.getLong(str).equals(new Long(123467)));
  }

  @Test
  public void getFloat_MalformedFloatInString_ShouldReturnNull() {
    String str = "abc12.1";
    assert (Utils.getFloat(str) == null);
  }

  @Test
  public void getFloat_FloatInString_ShouldReturnFloat() {
    String str = "12.1";
    assert (Utils.getFloat(str).equals(new Float(12.1)));
  }

  @Test
  public void getDouble_MalformedDoubleInString_ShouldReturnNull() {
    String str = "abc12.1";
    assert (Utils.getDouble(str) == null);
  }

  @Test
  public void getDouble_DoubleInString_ShouldReturnDouble() {
    String str = "12.1";
    assert (Utils.getDouble(str).equals(new Double(12.1)));
  }

  @Test
  public void getString_String_ShouldReturnString() {
    String str = "abc123^&$";
    assert (Utils.getString(str).equals(str));
  }
}
