package fr.jeremyhurel.models.strategies;

import java.util.List;

import fr.jeremyhurel.models.ClusterTree.ClusterNode;

public interface ClusteringStrategy {

    List<ClusterNode> cut(ClusterNode root);

    String getStrategyName();
}
