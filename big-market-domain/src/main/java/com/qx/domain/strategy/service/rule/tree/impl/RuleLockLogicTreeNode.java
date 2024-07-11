package com.qx.domain.strategy.service.rule.tree.impl;

import com.qx.domain.strategy.modle.valobj.RuleLogicCheckTypeVO;
import com.qx.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.qx.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();

    }
}
