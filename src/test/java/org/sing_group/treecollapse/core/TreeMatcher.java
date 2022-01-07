/*-
 * #%L
 * treecollapse
 * %%
 * Copyright (C) 2021 - 2022 Daniel Glez-Peña, Hugo López-Fernández, Cristina Vieira, Jorge Vieira
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sing_group.treecollapse.core;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TreeMatcher extends TypeSafeMatcher<TreeNode>{

  private TreeNode expected;
  
  
  private TreeMatcher(TreeNode expected) {
    super();
    this.expected = expected;
  }

  @Override
  public void describeTo(Description description) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected boolean matchesSafely(TreeNode actual) {
    return areEqual(this.expected, actual);
  }
  
  private boolean areEqual(TreeNode expected, TreeNode actual) {
    if (expected.getName() == null && actual.getName() == null)
      return true;
    else if (expected.getName() == null || actual.getName() == null) {
      return false;
    }
    else if (!expected.getName().equals(actual.getName())) {
      return false;
    } else if (expected.getChildren().size() != actual.getChildren().size()){
      return false;
    } else {
      for(int i = 0; i < expected.getChildren().size(); i++) {
        if (!areEqual(expected.getChildren().get(i), actual.getChildren().get(i)) ) {
          return false;
        }
      }
      return true;
    }
  }
  
  @Factory
  public static TreeMatcher equalTo(TreeNode expected) {
    return new TreeMatcher(expected);
  }

}
