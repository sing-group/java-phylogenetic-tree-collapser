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

import static java.util.Arrays.asList;
import static org.sing_group.treecollapse.core.TaxonomyCollapseTreeTest.getSequenceToSpecieMapping;
import static org.sing_group.treecollapse.core.TaxonomyCollapseTreeTest.taxonomy;

import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sing_group.treecollapse.core.exception.TaxonomyCollapsingException;
import org.sing_group.treecollapse.core.newick.NewickTree;
import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class BranchLengthAveragingTaxonomyCollapsingStrategyTest {

  @Test
  public void collapseTwoNodesTest1() {
    testCollapse(
      "(s1.1:4.0,(s2.1:2.0,s3.1:1.0,s5.1:1.0):4.0,s4.1:2.0)root;",
      "(s1.1:4.0,(s5.1:1.0,T1.2_1_2:1.5):4.0,s4.1:2.0)root;"
    );
  }

  @Test
  public void collapseTwoNodesTest2() {
    testCollapse(
      "(s1.1:4.0,(s2.1:2.0,s3.1:1.0):4.0,s4.1:2.0)root;",
      "(s1.1:4.0,s4.1:2.0,T1.2_1_2:5.5)root;"
    );
  }

  @Test(expected = TaxonomyCollapsingException.class)
  public void collapseWithoutBranchLengths() {
    testCollapse(
      "(s1.1,(s2.1,s3.1),s4.1)root;",
      "(s1.1:4.0,s4.1:2.0,T1.2_2:5.5)root;"
    );
  }

  private void testCollapse(String newickTreeString, String expectedCollapsedNewickTreeString) {
    Assert.assertEquals(expectedCollapsedNewickTreeString, collapse(newickTreeString));
  }

  private String collapse(String newickTreeString) {
    NewickTree newickTree = new NewickTree(newickTreeString);

    MutableTreeNode root = newickTree.getRoot();

    Map<String, String> sequenceToSpecieMapping = getSequenceToSpecieMapping(root);

    MutableTreeNode collapsed =
      new TreeCollapser().collapseTree(
        root,
        new BranchLengthAveragingTaxonomyCollapsingStrategy(
          sequenceToSpecieMapping, taxonomy(), new HashSet<String>(asList("T1.2", "T2"))
        )
      );

    return new NewickTree(collapsed).toString();
  }
}
