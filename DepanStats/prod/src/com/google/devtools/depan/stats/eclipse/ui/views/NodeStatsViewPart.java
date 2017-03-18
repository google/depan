package com.google.devtools.depan.stats.eclipse.ui.views;

import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSelectorControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.stats.eclipse.StatsResources;
import com.google.devtools.depan.stats.eclipse.ui.StatsExtensionData;
import com.google.devtools.depan.stats.eclipse.ui.widgets.NodeStatsTableControl;
import com.google.devtools.depan.stats.jung.JungStatistics;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.eclipse.ui.views.AbstractViewDocViewPart;
import com.google.devtools.depan.view_doc.model.ExtensionDataListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.text.DecimalFormat;

/**
 * Control and display node statistics.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class NodeStatsViewPart extends AbstractViewDocViewPart {

  /////////////////////////////////////
  // UX Elements

  private EdgeMatcherSelectorControl matcherChoice;

  private NodeStatsTableControl statsTable;

  private Text rootText;

  private Text degreeText;

  private Text rankText;
  
  private ExtensionDataListener dataListener;

  /////////////////////////////////////
  // ViewPart integration

  private static final String PART_NAME = "Node Statistics";

  @Override
  public Image getTitleImage() {
    return StatsResources.IMAGE_STATSVIEW;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  @Override
  protected void createGui(Composite parent) {
    Composite gui = Widgets.buildGridContainer(parent, 1);

    Group options = setupOptions(gui);
    options.setLayoutData(Widgets.buildHorzFillData());

    Group summary = setupStatsSummary(gui);
    summary.setLayoutData(Widgets.buildHorzFillData());

    Group stats = setupNodeStats(gui);
    stats.setLayoutData(Widgets.buildGrabFillData());
  }

  @Override
  protected void disposeGui() {
  }

  @SuppressWarnings("unused")
  private Group setupOptions(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Statistics Options", 2);

    Label relSetLabel = Widgets.buildCompactLabel(result, "Edges: ");
    matcherChoice = new EdgeMatcherSelectorControl(result);
    matcherChoice.setLayoutData(Widgets.buildHorzFillData());

    Button updateBtn = Widgets.buildCompactPushButton(
        result, "Update Statistics");
    GridData updateLayout = Widgets.getLayoutData(updateBtn);
    updateLayout.horizontalSpan = 2;
    updateLayout.horizontalAlignment = SWT.TRAIL;
    updateBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        updateStats();
      }
    });

    return result;
  }

  @SuppressWarnings("unused")
  private Group setupStatsSummary(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Summary Statistics", 2);

    Label rootLabel = Widgets.buildCompactLabel(result, "Roots");
    rootText = Widgets.buildGridText(result);
    rootText.setEditable(false);

    Label degreeLabel = Widgets.buildCompactLabel(result, "Maximum Degree");
    degreeText = Widgets.buildGridText(result);
    degreeText.setEditable(false);

    Label rankLabel = Widgets.buildCompactLabel(result, "Maximum Rank");
    rankText = Widgets.buildGridText(result);
    rankText.setEditable(false);

    return result;
  }

  private Group setupNodeStats(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Node Statistics", 1);

    statsTable = new NodeStatsTableControl(result);
    statsTable.setLayoutData(Widgets.buildGrabFillData());
    return result;
  }

  protected void updateStats() {
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef =
        matcherChoice.getSelection();
    if (null == matcherRef) {
      return;
    }
    ViewEditor editor = getEditor();
    StatsExtensionData data = StatsExtensionData.getStatsData(editor);

    data.setStatsMatcherRef(matcherRef);
    data.calcJungStatistics(editor.getViewGraph());

    StatsExtensionData.setStatsData(editor, data);
  }

  private void updateControls(ViewEditor editor) {
    if (null == editor) {
      return;
    }
    NodeViewerProvider provider = editor.getNodeViewProvider();
    statsTable.setInput(provider);

    StatsExtensionData data = StatsExtensionData.getStatsData(editor);
    if (null == data) {
      return;
    }
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef =
        data.getStatsMatcherRef();

    matcherChoice.setInput(matcherRef, editor.getResourceProject());
    displayStats(data);
  }

  private void displayStats(StatsExtensionData data) {

    JungStatistics stats = data.getJungStatistics();
    statsTable.updateJungStatistics(stats);

    rootText.setText(Integer.toString(stats.getRootNodes().size()));
    degreeText.setText(
        Integer.toString(stats.getMaxDegree(stats.getRankedNodes())));
    rankText.setText(
        new DecimalFormat("#.####").format(
            stats.getMaxRank(stats.getRankedNodes())));
  }

  @Override
  protected void acquireResources() {
    ViewEditor editor = getEditor();
    dataListener = new ExtensionDataListener() {
      @Override
      public void extensionDataChanged(
          ViewExtension ext, Object instance,
          Object propertyId, Object updates) {
        displayStats((StatsExtensionData) updates);
      }
    };
    editor.addExtensionDataListener(dataListener);
    updateControls(editor);
  }

  @Override
  protected void releaseResources() {
    ViewEditor editor = getEditor();
    editor.removeExtensionDataListener(dataListener);
  }
}
