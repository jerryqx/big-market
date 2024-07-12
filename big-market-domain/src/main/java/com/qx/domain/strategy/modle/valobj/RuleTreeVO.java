package com.qx.domain.strategy.modle.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Description: 规则树对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleTreeVO {

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则树名称
     */
    private String treeName;

    /**
     * 规则树描述
     */
    private String treeDesc;

    /**
     * 规则根节点
     */
    private String treeRootRuleNode;

    /**
     * 规则节点
     */
    private Map<String, RuleTreeNodeVO> treeNodeMap;

}
