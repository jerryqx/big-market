package com.qx.domain.strategy.service.rule.chain;

/**
 * @Description:  责任链装配
 */
public interface ILogicChainArmory {

    ILogicChain next();

    ILogicChain appendNext(ILogicChain next);
}
