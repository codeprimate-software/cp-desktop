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

package org.cp.desktop.swing.text;

public class InvalidTextFormatException extends Exception {

  /**
   * Creates an instance of the InvalidTextFormatException class.
   */
  public InvalidTextFormatException() {
  }

  /**
   * Creates an instance of the InvalidTextFormatException class initialized with a description of the problem.
   * @param message a String value describing the nature of the problem and reason this Exception was thrown.
   */
  public InvalidTextFormatException(String message) {
    super(message);
  }

  /**
   * Creates an instance of the InvalidTextFormatException class initialized with the specified Throwable,
   * which is also the reason, or cause, for this Exception to be thrown.
   * @param cause a Throwable object indicating the the reason this InvalidTextFormatException was thrown.
   */
  public InvalidTextFormatException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates an instance of the InvalidTextFormatException class initialized with a message desribing
   * the exceptional condition and a reason, or cause, for this Exception to be thrown.
   * @param message a String value describing the nature of the problem and reason this Exception was thrown.
   * @param cause a Throwable object indicating the the reason this InvalidTextFormatException was thrown.
   */
  public InvalidTextFormatException(String message, Throwable cause) {
    super(message, cause);
  }

}
