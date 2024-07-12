package com.qx.domain.strategy.service.rule.tree.factory.engine;

import com.qx.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @description: 决策树引擎接口
 */
public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);

}
