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

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public interface CollapsingStrategy {

  public default void beforeCollapse(MutableTreeNode initial) {}

  public MutableTreeNode mergeNodes(MutableTreeNode node1, MutableTreeNode node2);

  public MutableTreeNode collapse(MutableTreeNode parent, MutableTreeNode child);

  public boolean areMergeable(MutableTreeNode node1, MutableTreeNode node2);

  public default void afterCollapse(MutableTreeNode root) {}

}
