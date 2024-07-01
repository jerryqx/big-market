package com.qx.domain.strategy.repository;

import com.qx.domain.strategy.modle.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * @Description: 策略仓储接口
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(Long strategyId, int size, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int i);
}
