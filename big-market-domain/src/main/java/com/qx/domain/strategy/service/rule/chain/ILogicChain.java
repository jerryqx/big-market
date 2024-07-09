package com.qx.domain.strategy.service.rule.chain;

/**
 * @description: 抽奖策略规则责任链接口
 */
public interface ILogicChain extends ILogicChainArmory{

    /**
     * 责任链模式的逻辑处理方法
     * @param userId 用户id
     * @param strategyId 策略id
     * @return 返回奖品id
     */
    Integer logic(String userId,  Long strategyId);
}
