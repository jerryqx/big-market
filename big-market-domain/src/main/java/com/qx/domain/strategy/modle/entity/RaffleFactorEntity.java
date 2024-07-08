package com.qx.domain.strategy.modle.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 抽奖因子实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleFactorEntity {


    /** 用户ID */
    private String userId;
    /** 策略ID */
    private Long strategyId;

    private Integer awardId;
}
