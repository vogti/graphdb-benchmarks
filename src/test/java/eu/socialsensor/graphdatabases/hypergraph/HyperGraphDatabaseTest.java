package eu.socialsensor.graphdatabases.hypergraph;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.Sets;
import eu.socialsensor.graphdatabases.hypergraph.vertex.Node;
import eu.socialsensor.graphdatabases.hypergraph.vertex.NodeQueries;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.atom.HGRel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HyperGraphDatabaseTest {

  private File databaseDir = new File(this.getClass().getResource("/").getPath(), "data");
  private File testData = new File(this.getClass().getResource("/test.data").getPath());
  private File resultDir = new File(this.getClass().getResource("/").getPath(), "result");

  private HyperGraphDatabase graph = null;

  @Before
  public void cleaDBSetup() throws IOException, InterruptedException {

    FileUtils.forceMkdir(databaseDir);
    FileUtils.forceMkdir(resultDir);

    graph = new HyperGraphDatabase(null, databaseDir);
    graph.open();
      graph.createGraphForSingleLoad();
      graph.singleModeLoading(testData, resultDir, 0);
  }

  @After
  public void clean() throws IOException {
    FileUtils.deleteDirectory(databaseDir);
    FileUtils.deleteDirectory(resultDir);
  }
  @Test
  public void createDatabaseForSingleMode() throws IOException {
    Assert.assertTrue("Should create a database file",
        new File(databaseDir, "00000000.jdb").exists()
    );
    Assert.assertEquals("Should match node count",
        graph.getNodeCount(), 5);

    Iterator<HGRel> it = graph.getAllEdges();
    List<String> resultRelationships = asString(it);

    Assert.assertEquals("Should contain same relationships",
        CollectionUtils.getCardinalityMap(resultRelationships),
        CollectionUtils.getCardinalityMap(getExpectedRelationships(FilterType.ALL)));
  }

  private enum FilterType {
    ALL,
    NEIGHBORS_OF_1
  }
  private List<String> getExpectedRelationships(FilterType type) {

    List<String> expectedRelationships = new ArrayList<>();
    switch (type){
      case ALL:
        expectedRelationships.add("0 -> 1");
        expectedRelationships.add("1 -> 2");
        expectedRelationships.add("2 -> 3");
        expectedRelationships.add("3 -> 4");
        expectedRelationships.add("1 -> 4");
        break;
      case NEIGHBORS_OF_1:
        expectedRelationships.add("0 -> 1");
        expectedRelationships.add("1 -> 2");
        expectedRelationships.add("1 -> 4");
        break;

    }
    return expectedRelationships;
  }

  private List<Integer> getExpectedNodeIds(){
    return  Arrays.asList(0, 1, 2, 3, 4);
  }

  @Test
  public void createDatabaseForMassiveMode() throws IOException {
    assertTrue(new File(databaseDir, "00000000.jdb").exists());
    assertEquals(graph.getNodeCount(), 5);
  }

  @Test
  public void testSingleModeLoading() throws IOException {
  }

  @Test
  public void initCommunityProperty() throws IOException {
    graph.initCommunityProperty();
  }

  @Test
  public void getOtherVertexFromEdge() throws IOException {
    Node node0  = graph.getVertex(0);
    Node node1  = graph.getVertex(1);
    HGRel rel = graph.getNeighborsOfVertex(node0).next();

    Node resutlNode = graph.getOtherVertexFromEdge(rel, node0);
    Assert.assertEquals("Should return the other node in the relationship",
        resutlNode,
        node1
        );
  }

  @Test
  public void getSrcVertexFromEdge() {
  }

  @Test
  public void getDestVertexFromEdge() {
  }

  @Test
  public void getVertex() {
  }

  @Test
  public void getAllEdges() {
  }

  @Test
  public void getNeighborsOfVertex() throws IOException {
    Iterator<HGRel> it = graph.getNeighborsOfVertex(graph.getVertex(1));
    List<String> resultNeighborRelationships = asString(it);

    Assert.assertEquals("Should retrieve all neighbor relationships of a vertex",
        CollectionUtils.getCardinalityMap(resultNeighborRelationships),
        CollectionUtils.getCardinalityMap(getExpectedRelationships(FilterType.NEIGHBORS_OF_1))
        );
  }

  private List<String> asString(Iterator<HGRel> it){
    List<String> resultRelationships = new ArrayList<>();
    HyperGraph graph = HGEnvironment.get(databaseDir.getAbsolutePath());
    while (it.hasNext()) {
      HGRel t = it.next();
      Node n0 = graph.get(t.getTargetAt(0));
      Node n1 = graph.get(t.getTargetAt(1));

      resultRelationships.add(n0.getId() + " -> " + n1.getId());
    }
    graph.close();
    return resultRelationships;
  }

  @Test
  public void edgeIteratorHasNext() {
  }

  @Test
  public void cleanupEdgeIterator() {
  }

  @Test
  public void getVertexIterator() throws IOException {
    List<Integer> nodeId = new ArrayList<>();

    Assert.assertEquals("Should retrieve all node ids",
        5,
        nodeId.size()
    );

    Assert.assertEquals("Should match node ids",
        CollectionUtils.getCardinalityMap(getExpectedNodeIds()),
        CollectionUtils.getCardinalityMap(nodeId));
  }

  @Test
  public void cleanupVertexIterator() {
  }

  @Test
  public void open() {
  }

  @Test
  public void createGraphForSingleLoad() {
  }

  @Test
  public void massiveModeLoading() {
  }

  @Test
  public void singleModeLoading() {
  }

  @Test
  public void createGraphForMassiveLoad() {
  }

  @Test
  public void shutdown() {
  }

  @Test
  public void delete() {
  }

  @Test
  public void shutdownMassiveGraph() {
  }

  @Test
  public void shortestPath() {
  }

  @Test
  public void getNodeCount() {
  }

  @Test
  public void getNeighborsIds() {
    Assert.assertEquals("With node who has just outgoing nodes",
        Sets.newHashSet(Arrays.asList(1)),
        graph.getNeighborsIds(0)
        );
    Assert.assertEquals("With node who has in and outgoing nodes",
        Sets.newHashSet(Arrays.asList(2, 4)),
        graph.getNeighborsIds(1)
    );
    Assert.assertEquals("With node who has no outgoing nodes",
        new HashSet<Integer>(),
        graph.getNeighborsIds(4)
    );
  }

  @Test
  public void getNodeWeight() {
  }

  @Test
  public void getCommunitiesConnectedToNodeCommunities() {
  }

  @Test
  public void getNodesFromCommunity() {
  }

  @Test
  public void getNodesFromNodeCommunity() {
  }

  @Test
  public void getEdgesInsideCommunity() {
  }

  @Test
  public void getCommunityWeight() {
  }

  @Test
  public void getNodeCommunityWeight() {
  }

  @Test
  public void moveNode() {
  }

  @Test
  public void getGraphWeightSum() {
  }

  @Test
  public void reInitializeCommunities() {
  }

  @Test
  public void getCommunityFromNode() {
  }

  @Test
  public void getCommunity() {
  }

  @Test
  public void getCommunitySize() {
  }

  private void setCommunities(){
    HyperGraph graph = HGEnvironment.get(databaseDir.getAbsolutePath());
    List<Node> allN = graph.getAll(NodeQueries.nodeType());
    for(Node n : allN){
      HGHandle handle = graph.getHandle(n);
      if (n.getId() < 3) n.setCommunity(1);
      else n.setCommunity(2);
      graph.replace(handle, n);
    }
  }
  @Test
  public void mapCommunities() {
    Map<Integer, List<Integer>> expected = new HashMap<>();
    expected.put(0, Arrays.asList(0, 1, 2, 3, 4));
    expected.put(1, Arrays.asList());
    Map<Integer, List<Integer>> result = graph.mapCommunities(2);

    Collections.sort(result.get(0));
    Assert.assertEquals("Should all be in community 0",
        expected,
        result
        );
    setCommunities();

    expected.clear();
    expected.put(0, Arrays.asList());
    expected.put(1, Arrays.asList(0, 1, 2));
    expected.put(2, Arrays.asList(3, 4));
    Map<Integer, List<Integer>> result2 = graph.mapCommunities(3);
    Collections.sort(result2.get(0));
    Collections.sort(result2.get(1));
    Collections.sort(result2.get(2));
    Assert.assertEquals("Should all be in community 1 and 2",
        expected,
        result2
    );
  }

  @Test
  public void nodeExists() {
  }
}