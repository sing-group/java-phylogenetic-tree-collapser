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
package org.sing_group.treecollapse.core.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MutableTreeNode implements TreeNode {

  private String name = null;
  private List<MutableTreeNode> children = new LinkedList<>();
  private Map<String, Object> attributes = new HashMap<>();

  private MutableTreeNode parent = null;

  public MutableTreeNode(TreeNode original) {
    this.name = original.getName();
    original.getChildren().forEach((child) -> {
      MutableTreeNode mChild = new MutableTreeNode(child);
      this.children.add(mChild);
      mChild.parent = this;

    });
    
    if (original instanceof MutableTreeNode) {
      MutableTreeNode mtnOriginal = (MutableTreeNode) original;
      for (Map.Entry<String, Object> entry: mtnOriginal.getAttributes().entrySet()) {
        this.setAttribute(entry.getKey(), entry.getValue());
      }
    }
  }

  public MutableTreeNode(String name) {
    this.name = name;
  }

  public void addChild(MutableTreeNode node) {
    this.children.add(node);
    node.parent = this;
  }

  public void removeChild(MutableTreeNode node) {
    this.children.remove(node);
    node.parent = null;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public List<MutableTreeNode> getChildren() {
    return this.children;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public void setAttribute(String key, Object value) {
    this.attributes.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }

  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) this.getAttributes().get(key);
  }

  public MutableTreeNode getParent() {
    return parent;
  }

  @Override
  public String toString() {
    return this.getName() + " " + this.getAttributes();
  }
}
