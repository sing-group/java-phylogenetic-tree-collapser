package org.sing_group.treecollapse.core.newick;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NewickTreeParserTest {

  /*
   * Examples from:
   * https://evolution.genetics.washington.edu/phylip/newicktree.html
   */

  @Parameters(name = "The input newick tree is: {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {
        "(B,(A,C,E),D);"
      },
      {
        "(,(,,),);"
      },
      {
        "(B:6.0,(A:5.0,C:3.0,E:4.0):5.0,D:11.0);"
      },
      {
        "(B:6.0,(A:5.0,C:3.0,E:4.0)Ancestor1:5.0,D:11.0);"
      },
      {
        "((raccoon:19.19959,bear:6.80041):0.846,((sea_lion:11.997,seal:12.003):7.52973,((monkey:100.8593,cat:47.14069):20.59201,weasel:18.87953):2.0946):3.87382,dog:25.46154);"
      },
      {
        "(Bovine:0.69395,(Gibbon:0.36079,(Orang:0.33636,(Gorilla:0.17147,(Chimp:0.19268,Human:0.11927):0.08386):0.06124):0.15057):0.54939,Mouse:1.2146):0.1;"
      },
      {
        "(Bovine:0.69395,(Hylobates:0.36079,(Pongo:0.33636,(G._Gorilla:0.17147,(P._paniscus:0.19268,H._sapiens:0.11927):0.08386):0.06124):0.15057):0.54939,Rodent:1.2146);"
      },
      {
        "((A,B),(C,D));"
      },
      {
        "(Alpha,Beta,Gamma,Delta,,Epsilon,,,);"
      },
      {
        "A;"
      }
    });
  }

  private final String inputNewick;

  public NewickTreeParserTest(String inputNewick) {
    this.inputNewick = inputNewick;
  }

  @Test
  public void testParser() {
    assertEquals(inputNewick, new NewickTree(inputNewick).toString());
  }
}
