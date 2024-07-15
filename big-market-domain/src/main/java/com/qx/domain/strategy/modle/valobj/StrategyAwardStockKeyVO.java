package com.qx.domain.strategy.modle.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 策略奖品库存Key标识值对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyAwardStockKeyVO {

    private Long strategyId;

    private Integer awardId;


}
