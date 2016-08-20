package com.google.devtools.depan.cmd.setops;

import static org.junit.Assert.assertEquals;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListDocXmlPersist;
import com.google.devtools.depan.test.TestUtils;

import com.google.common.collect.Lists;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Ignore // trouble in Maven build
public class SubtractCommandTest {

  @Rule
  public TemporaryFolder wksp = new TemporaryFolder();

  @Test
  public void testSubtractCommand() throws IOException {
    GraphDocument base = TestUtils.buildTestDoc(5);
    GraphDocument minus = TestUtils.buildTestDoc(4);
    File baseFile = writeGraphDoc("base.dgi", base);
    File minusFile = writeGraphDoc("minus.dgi", minus);
    SubtractCommand cmd = new SubtractCommand();
    cmd.setArgs(buildArgs("diff.dni", baseFile, minusFile));

    cmd.exec();

    File diffFile = new File(baseFile.getParentFile(), "diff.dni");
    NodeListDocXmlPersist persist =
        NodeListDocXmlPersist.buildForLoad(diffFile);
    NodeListDocument diff = persist.load(diffFile.toURI());

    Collection<GraphNode> testNodes = diff.getNodes();
    assertEquals(1, testNodes.size());
    assertEquals("node 4", testNodes.iterator().next().getId());

    assertEquals(0, diff.getDependencyModel().getNodeContribs().size());
    assertEquals(0, diff.getDependencyModel().getRelationContribs().size());
  }

  @Test
  public void testSubtractNothing() throws IOException {
    GraphDocument base = TestUtils.buildTestDoc(5);
    File baseFile = writeGraphDoc("base.dgi", base);
    SubtractCommand cmd = new SubtractCommand();
    cmd.setArgs(buildArgs("diff.dni", baseFile));

    cmd.exec();

    File diffFile = new File(baseFile.getParentFile(), "diff.dni");
    NodeListDocXmlPersist persist =
        NodeListDocXmlPersist.buildForLoad(diffFile);
    NodeListDocument diff = persist.load(diffFile.toURI());

    Collection<GraphNode> testNodes = diff.getNodes();
    assertEquals(5, testNodes.size());

    assertEquals(0, diff.getDependencyModel().getNodeContribs().size());
    assertEquals(0, diff.getDependencyModel().getRelationContribs().size());
  }

  private File writeGraphDoc(String name, GraphDocument graphDoc)
      throws IOException {
    File result = wksp.newFile(name);
    GraphModelXmlPersist persist = GraphModelXmlPersist.build(false);
    persist.save(result.toURI(), graphDoc);
    return result;
  }

  private List<String> buildArgs(
      String dest, File baseFile, File... minuses) {
    List<String> result = Lists.newArrayList();
    result.add("subtract");
    result.add(dest);
    result.add(baseFile.getPath());
    for (File minusFile : minuses) {
      result.add(minusFile.getPath());
    }
    return result;
  }
}
