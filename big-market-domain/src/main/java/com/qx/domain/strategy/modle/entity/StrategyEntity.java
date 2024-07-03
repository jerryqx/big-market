package com.qx.domain.strategy.modle.entity;

import com.qx.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 策略实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖策略描述 */
    private String strategyDesc;
    /** 抽奖规则模型 rule_weight,rule_blacklist */
    private String ruleModels;

    public String[] ruleModels() {
        if (StringUtils.isNotBlank(this.ruleModels)) {
            return ruleModels.split(Constants.SPLIT);
        }
        return null;
    }

    public String getRuleWeight() {
        String[] ruleModels = this.ruleModels();
        if (ruleModels != null && ruleModels.length > 0) {
            for (String ruleModel : ruleModels) {
                if ("rule_weight".equals(ruleModel)) {
                    return ruleModel;
                }
            }
        }
        return null;
    }

}
