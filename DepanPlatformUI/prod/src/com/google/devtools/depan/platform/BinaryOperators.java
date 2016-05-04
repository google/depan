/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.platform;

/**
 * Interface for any object of type T that can be combined with others of the
 * same type T using binary operations such as and, or, xor or not.
 * 
 * Note that order mater for the not operation: A ! B is not the same as B ! A.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 * @param <T> type of objects to combine with binary operators.
 */
public interface BinaryOperators<T> {
  
  /**
   * Combine this and that with an AND operator, to create a new T
   * containing elements present in both objects. {1,2,3} & {3,4,5} = {3}.
   * 
   * @param that
   * @return a T containing elements present in this and in that.
   */
  public T and(T that);

  /**
   * Combine this and that with an OR operator, to create a new T containing
   * elements present at least in one object. {1,2,3} OR {3,4,5} = {1,2,3,4,5}.
   * 
   * @param that
   * @return a T containing elements present in this or in that.
   */
  public T or(T that);

  /**
   * Combine this and that with a XOR operator, to create a new T containing
   * elements present at most in one object. {1,2,3} XOR {3,4,5} = {1,2,4,5}.
   * 
   * @param that
   * @return a T containing elements present in this or in that, but not both.
   */
  public T xor(T that);

  /**
   * Combine this and that with a NOT operator, to create a new T containing
   * elements present in this, but not in that. {1,2,3} NOT {3,4,5} = {1,2}.
   * 
   * @param that
   * @return a T containing elements present in this, without elements in that.
   */
  public T not(T that);
}
