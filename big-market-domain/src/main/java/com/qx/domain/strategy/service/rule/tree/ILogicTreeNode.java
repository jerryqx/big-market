package com.qx.domain.strategy.service.rule.tree;

import com.qx.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @Description 规则树节点接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue);

}
