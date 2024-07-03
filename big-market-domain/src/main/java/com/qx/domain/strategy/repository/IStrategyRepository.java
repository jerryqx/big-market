package com.qx.domain.strategy.repository;

import com.qx.domain.strategy.modle.entity.StrategyAwardEntity;
import com.qx.domain.strategy.modle.entity.StrategyEntity;
import com.qx.domain.strategy.modle.entity.StrategyRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * @Description: 策略仓储接口
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String strategyId, int size,
                                           Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String strategyId, int i);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);
}
