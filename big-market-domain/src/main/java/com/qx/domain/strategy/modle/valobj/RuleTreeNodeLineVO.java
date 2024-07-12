package com.qx.domain.strategy.modle.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 决策树节点连线
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RuleTreeNodeLineVO {

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则 Key 节点 From
     */
    private String ruleNodeFrom;

    /**
     * 规则 Key 节点 To
     */
    private String ruleNodeTo;

    /**
     * 限定类型
     */
    private RuleLimitTypeVO ruleLimitType;

    /**
     * 限定值(到下个节点)
     */
    private RuleLogicCheckTypeVO ruleLimitValue;
}
