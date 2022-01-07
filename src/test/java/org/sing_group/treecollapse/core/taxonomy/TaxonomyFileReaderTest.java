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
package org.sing_group.treecollapse.core.taxonomy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.sing_group.treecollapse.core.TaxonomyCollapseTreeTest;
import org.sing_group.treecollapse.core.newick.NewickTree;
import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

@RunWith(Parameterized.class)
public class TaxonomyFileReaderTest {

  @Parameters(name = "Taxonomy file: {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {
        new File("src/test/resources/taxonomy/taxonomy_1.txt"),
        TaxonomyCollapseTreeTest.taxonomy()
      },
      {
        new File("src/test/resources/taxonomy/taxonomy_2.txt"),
        TaxonomyCollapseTreeTest.flatTaxonomy(),
      }
    });
  }

  private File taxonomyFile;
  private MutableTreeNode expectedTaxonomy;

  public TaxonomyFileReaderTest(File taxonomyFile, TreeNode expectedTaxonomy) {
    this.taxonomyFile = taxonomyFile;
    this.expectedTaxonomy = new MutableTreeNode(expectedTaxonomy);
  }

  @Test
  public void taxonomyTest() throws IOException {
    MutableTreeNode actual = TaxonomyFileReader.read(taxonomyFile);

    assertEquals(
      new NewickTree(expectedTaxonomy).toString(),
      new NewickTree(actual).toString()
    );
  }
}
