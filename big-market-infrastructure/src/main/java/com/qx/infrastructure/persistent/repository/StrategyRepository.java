package com.qx.infrastructure.persistent.repository;

import com.qx.domain.strategy.modle.entity.StrategyAwardEntity;
import com.qx.domain.strategy.modle.entity.StrategyEntity;
import com.qx.domain.strategy.modle.entity.StrategyRuleEntity;
import com.qx.domain.strategy.modle.valobj.RuleLimitTypeVO;
import com.qx.domain.strategy.modle.valobj.RuleLogicCheckTypeVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeNodeLineVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeNodeVO;
import com.qx.domain.strategy.modle.valobj.RuleTreeVO;
import com.qx.domain.strategy.modle.valobj.StrategyAwardRuleModelVO;
import com.qx.domain.strategy.modle.valobj.StrategyAwardStockKeyVO;
import com.qx.domain.strategy.repository.IStrategyRepository;
import com.qx.infrastructure.persistent.dao.IRuleTreeDao;
import com.qx.infrastructure.persistent.dao.IRuleTreeNodeDao;
import com.qx.infrastructure.persistent.dao.IRuleTreeNodeLineDao;
import com.qx.infrastructure.persistent.dao.IStrategyAwardDao;
import com.qx.infrastructure.persistent.dao.IStrategyDao;
import com.qx.infrastructure.persistent.dao.IStrategyRuleDao;
import com.qx.infrastructure.persistent.po.RuleTree;
import com.qx.infrastructure.persistent.po.RuleTreeNode;
import com.qx.infrastructure.persistent.po.RuleTreeNodeLine;
import com.qx.infrastructure.persistent.po.Strategy;
import com.qx.infrastructure.persistent.po.StrategyAward;
import com.qx.infrastructure.persistent.po.StrategyRule;
import com.qx.infrastructure.persistent.redis.IRedisService;
import com.qx.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

/**
 * @Description 策略仓储实现类
 */
@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IStrategyDao strategyDao;

    @Resource
    private IStrategyRuleDao strategyRuleDao;

    @Resource
    private IRuleTreeDao ruleTreeDao;

    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;

    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Resource
    private IRedisService redissonService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 从缓存中获取数据
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redissonService.getValue(cacheKey);
        // 缓存中有数据，直接返回
        if (null != strategyAwardEntities && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;

        // 缓存中没有数据，从数据库中获取数据
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity =
                    StrategyAwardEntity.builder().strategyId(strategyAward.getStrategyId())
                            .awardId(strategyAward.getAwardId()).awardCount(strategyAward.getAwardCount())
                            .awardCountSurplus(strategyAward.getAwardCountSurplus())
                            .awardRate(strategyAward.getAwardRate()).build();
            strategyAwardEntities.add(strategyAwardEntity);
        } redissonService.setValue(cacheKey, strategyAwardEntities); return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String strategyId, int rateRange,
                                                  Map<Integer, Integer> strategyAwardSearchRateTable) {
        // 1. 存储抽奖策略范围值，如10000，用于生成1000以内的随机数
        redissonService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        // 2. 存储概率查找表
        Map<Integer, Integer> cacheRateTable =
                redissonService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(strategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String strategyId) {
        return redissonService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);

    }

    @Override
    public Integer getStrategyAwardAssemble(String strategyId, int rateKey) {
        return redissonService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redissonService.getValue(cacheKey);
        if (null != strategyEntity) return strategyEntity;
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId); if (strategy == null) return null;
        strategyEntity =
                StrategyEntity.builder().strategyId(strategy.getStrategyId()).strategyDesc(strategy.getStrategyDesc())
                        .ruleModels(strategy.getRuleModels()).build();
        redissonService.setValue(cacheKey, strategyEntity); return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRuleReq = new StrategyRule(); strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyRuleReq);
        if (strategyRule == null) return null;
        return StrategyRuleEntity.builder().strategyId(strategyRule.getStrategyId()).awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType()).ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue()).ruleDesc(strategyRule.getRuleDesc()).build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return this.queryStrategyRuleValue(strategyId, null, ruleModel);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRuleReq = new StrategyRule(); strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel); strategyRuleReq.setAwardId(awardId);
        return strategyRuleDao.queryStrategyRuleValue(strategyRuleReq);

    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAwardReq = new StrategyAward(); strategyAwardReq.setStrategyId(strategyId);
        strategyAwardReq.setAwardId(awardId);
        String ruleModels = strategyAwardDao.queryStrategyAwardRuleModels(strategyAwardReq);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();

    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {

        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redissonService.getValue(cacheKey);
        if (null != ruleTreeVOCache) return ruleTreeVOCache;

        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 1. tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder().treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom()).ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue())).build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList =
                    ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>(); for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO =
                    RuleTreeNodeVO.builder().treeId(ruleTreeNode.getTreeId()).ruleKey(ruleTreeNode.getRuleKey())
                            .ruleDesc(ruleTreeNode.getRuleDesc()).ruleValue(ruleTreeNode.getRuleValue())
                            .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey())).build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder().treeId(ruleTree.getTreeId()).treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc()).treeRootRuleNode(ruleTree.getTreeRootRuleKey())
                .treeNodeMap(treeNodeMap).build();

        redissonService.setValue(cacheKey, ruleTreeVODB); return ruleTreeVODB;
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        if (redissonService.isExists(cacheKey)) {
            return;
        }
        redissonService.setAtomicLong(cacheKey, awardCount);
    }

    @Override
    public Boolean subtractionAwardStock(String cacheKey) {
        long surPlus = redissonService.decr(cacheKey); if (surPlus < 0) {
            // 库存小于0，恢复会0
            redissonService.setAtomicLong(cacheKey, 0); return false;
        }

        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等，也不会超卖。因为所有的可用库存key，都被加锁了。
        String lockKey = Constants.UNDERLINE + surPlus; Boolean lock = redissonService.setNx(lockKey); if (!lock) {
            log.info("策略奖品库存加锁失败 {}", lockKey);
        }
        return lock;

    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<Object> blockingQueue = redissonService.getBlockingQueue(cacheKey);
        RDelayedQueue<Object> delayedQueue = redissonService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redissonService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }


    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }
}
