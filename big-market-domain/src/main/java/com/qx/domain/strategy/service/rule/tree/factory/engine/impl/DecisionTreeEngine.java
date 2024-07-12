package com.qx.domain.strategy.service.rule.tree.factory.engine.impl;

import com.qx.domain.strategy.modle.valobj.RuleLogicCheckTypeVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeNodeLineVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeNodeVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeVO;
import com.qx.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.qx.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.qx.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @Description: 决策树引擎
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {

        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = null;

        //获取基础信息
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 获取起始节点 [根节点记录了第一个要执行的规则]
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);
        while (nextNode != null) {
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckType();

            strategyAwardVO = logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(),
                    nextNode, ruleLogicCheckTypeVO.getCode());

            // 获取下个节点
            nextNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);

        }

        return strategyAwardVO;
    }

    public String nextNode(String matterValue, List<RuleTreeNodeLineVO> treeNodeLineVOList) {
        if (null == treeNodeLineVOList || treeNodeLineVOList.isEmpty()) {
            return null;
        }

        for (RuleTreeNodeLineVO nodeLine : treeNodeLineVOList) {

            if (decisionLogic(matterValue, nodeLine)) {
                return nodeLine.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");

    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {

        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
