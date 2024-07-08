package com.qx.domain.strategy.service.rule;

import com.qx.domain.strategy.modle.entity.RuleActionEntity;
import com.qx.domain.strategy.modle.entity.RuleMatterEntity;

public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity>{

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
