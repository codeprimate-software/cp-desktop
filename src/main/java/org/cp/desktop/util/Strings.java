/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.desktop.util;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;

/**
 * Abstract utility class to process Java {@link String Strings}.
 *
 * @author John Blum
 * @see java.lang.String
 * @since 1.0.0
 */
public abstract class Strings {

  public static int count(@Nullable String value, char character) {

    int count = 0;

    if (StringUtils.hasText(value)) {
      for (char chr : value.toCharArray()) {
        if (chr == character) {
          count++;
        }
      }
    }

    return count;
  }

  public static int countWhitespace(@Nullable String value) {
    return count(value, StringUtils.SINGLE_SPACE_CHAR);
  }

  public static @NotNull String insert(@NotNull String value, @Nullable String insertValue, int offset) {

    Assert.notNull(value, "String value is required");

    Assert.isFalse(offset < 0 || offset > value.length(),
      "Offset [%d] cannot be negative or greater than the String value's length [%d]", offset, value.length());

    return String.format("%s%s%s", value.substring(0, offset), insertValue, value.substring(offset));

  }

  public static @NotNull String remove(@NotNull String value, int offset, int length) {
    return String.format("%s%s", value.substring(0, offset), value.substring(offset + length));
  }
}
