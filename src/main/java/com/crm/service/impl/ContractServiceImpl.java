package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.ContractConvert;
import com.crm.entity.*;
import com.crm.mapper.*;
import com.crm.query.ApprovalQuery;
import com.crm.query.ContractQuery;
import com.crm.query.ContractTrendQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.DateUtils;
import com.crm.utils.MailUtils;
import com.crm.vo.ContractTrendVO;
import com.crm.vo.ContractVO;
import com.crm.vo.ProductVO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static com.crm.utils.NumberUtils.generateContractNumber;



@Service
@AllArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {
    private final ProductMapper productMapper;
    private final ContractProductMapper contractProductMapper;
    private final ApprovalMapper approvalMapper;
    private final SysManagerMapper sysManagerMapper; // 新增：管理员表Mapper（需注入）
    private final MailUtils mailUtils; // 新增：邮件工具类

    @Override
    public PageResult<ContractVO> getPage(ContractQuery query) {
        Page<ContractVO> page = new Page<>(query.getPage(), query.getLimit());
//        条件查询
        MPJLambdaWrapper<Contract> wrapper = new MPJLambdaWrapper<>();
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Contract::getName, query.getName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Contract::getStatus, query.getStatus());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(Contract::getCustomerId, query.getCustomerId());
        }
        if (StringUtils.isNotBlank(query.getNumber())) {
            wrapper.like(Contract::getNumber, query.getNumber());
        }
        // 只查询目前登录的员工签署的合同信息
        Integer managerId = SecurityUser.getManagerId();
        wrapper.selectAll(Contract.class)
                .selectAs(Customer::getName, ContractVO::getCustomerName)
                .leftJoin(Customer.class, Customer::getId, Contract::getCustomerId)
                .eq(Contract::getOwnerId, managerId).orderByDesc(Contract::getCreateTime);
        Page<ContractVO> result = baseMapper.selectJoinPage(page, ContractVO.class, wrapper);
//        查询合同签署的商品信息
        if (!result.getRecords().isEmpty()) {
            result.getRecords().forEach(contractVO -> {
                List<ContractProduct> contractProducts = contractProductMapper.selectList(new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractVO.getId()));
                contractVO.setProducts(ContractConvert.INSTANCE.toProductVOList(contractProducts));
            });
        }
        // 关联查询客户表和管理员表（获取创建人邮箱）
        wrapper.selectAll(Contract.class)
                .selectAs(Customer::getName, ContractVO::getCustomerName)
                .selectAs(SysManager::getNickname, ContractVO::getCreaterNickname) // 销售昵称
                .selectAs(SysManager::getEmail, ContractVO::getCreaterEmail) // 销售邮箱
                .leftJoin(Customer.class, Customer::getId, Contract::getCustomerId)
                .leftJoin(SysManager.class, SysManager::getId, Contract::getCreaterId) // 关联创建人
                .eq(Contract::getOwnerId, SecurityUser.getManagerId())
                .orderByDesc(Contract::getCreateTime);

        return new PageResult<>(result.getRecords(), page.getTotal());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(ContractVO contractVO) {

        boolean isNew = contractVO.getId() == null;
        // 校验合同名称重复
        if (isNew && baseMapper.exists(new LambdaQueryWrapper<Contract>().eq(Contract::getName, contractVO.getName()))) {
            throw new ServerException("合同名称已存在，请勿重复添加");
        }
        // 转换并保存合同
        Contract contract = ContractConvert.INSTANCE.toContract(contractVO);
        contract.setCreaterId(SecurityUser.getManagerId());
        contract.setOwnerId(SecurityUser.getManagerId());
        if (isNew) {
            contract.setNumber(generateContractNumber());
            baseMapper.insert(contract);
        } else {
            Contract dbContract = baseMapper.selectById(contract.getId());
            if (dbContract == null) throw new ServerException("合同不存在");
            if (dbContract.getStatus() == 1) throw new ServerException("该合同已审核通过，请勿修改");
            baseMapper.updateById(contract);
        }
        if (contract.getReceivedAmount() == null) {
            contract.setReceivedAmount(BigDecimal.ZERO);
        }
        // 处理合同商品明细
        handleContractProducts(contract.getId(), contractVO.getProducts());

    }


    @Override
    public void startApproval(IdQuery idQuery) {
        Contract contract = baseMapper.selectById(idQuery.getId());
        if (contract == null) {
            throw new ServerException("合同不存在");
        }
        if (contract.getStatus() != 0) {
            throw new ServerException("该合同已审核通过，请勿重复提交");
        }
        contract.setStatus(1);
        baseMapper.updateById(contract);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void approvalContract(ApprovalQuery query) {
//        Contract contract = baseMapper.selectById(query.getId());
//        if (contract == null) {
//            throw new ServerException("合同不存在");
//        }

//        if (contract.getStatus() != 1) {
//            throw new ServerException("合同还未发起审核或已审核，请勿重复提交");
//        }
//        // 添加审核内容，判断审核状态
//        String approvalContent = query.getType() == 0 ? "合同审核通过" : "合同审核未通过";
//        Integer contractStatus = query.getType() == 0 ? 2 : 3;
//        Approval approval = new Approval();
//        approval.setType(0);
//        approval.setStatus(query.getType());
//        approval.setCreaterId(SecurityUser.getManagerId());
//        approval.setContractId(contract.getId());
//        approval.setComment(approvalContent);
//        approvalMapper.insert(approval);
//        contract.setStatus(contractStatus);
//        baseMapper.updateById(contract);
//        approval.setComment(query.getComment()); // 保存前端传入的审核内容（关键修改）
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalContract(ApprovalQuery query) {
        Contract contract = baseMapper.selectById(query.getId());
        if (contract == null) {
            throw new ServerException("合同不存在");
        }

        // 校验合同状态（必须是“待审核”状态，即status=1）
        if (contract.getStatus() != 1) {
            throw new ServerException("合同还未发起审核或已审核，请勿重复提交");
        }

        // 审核状态处理（0=通过，1=拒绝）
        Integer contractStatus = query.getType() == 0 ? 2 : 3; // 2=通过，3=未通过（与枚举匹配）

        // 保存审核记录（使用前端传入的审核内容）
        Approval approval = new Approval();
        approval.setType(0); // 合同审核类型
        approval.setStatus(query.getType());
        approval.setCreaterId(SecurityUser.getManagerId()); // 审核人ID
        approval.setContractId(contract.getId());
        approval.setComment(query.getComment()); // 前端传入的审核内容（关键）
        approvalMapper.insert(approval);

        // 更新合同状态
        contract.setStatus(contractStatus);
        baseMapper.updateById(contract);

        // ===== 新增：审核通过时发送邮件通知销售 =====
        if (query.getType() == 0) { // 0=审核通过
            // 1. 查询创建合同的销售信息（通过contract.createrId关联sys_manager）
            SysManager seller = sysManagerMapper.selectById(contract.getCreaterId());
            if (seller == null) {
                throw new ServerException("创建合同的销售信息不存在");
            }
            // 2. 校验销售邮箱是否存在
            if (seller.getEmail() == null || seller.getEmail().trim().isEmpty()) {
                throw new ServerException("销售邮箱未设置，无法发送通知");
            }
            // 3. 发送邮件
            mailUtils.sendContractApprovedNotice(
                    seller.getEmail(),
                    contract.getName(),
                    contract.getNumber()
            );
        }
    }

    @Autowired
    private ContractMapper contractMapper;

    @Override
    public Map<String, List> getContractTrendData(ContractTrendQuery query) {
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<ContractTrendVO> tradeStatistics = new ArrayList<>();

        // 维度判断逻辑不变，确保调用正确的 Mapper 方法
        if ("day".equals(query.getTransactionType())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = now.truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime startTime = now.withHour(0).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String> timeRange = new ArrayList<>();
            timeRange.add(formatter.format(startTime));
            timeRange.add(formatter.format(endTime));
            query.setTimeRange(timeRange);

            query.setTimeFormat("%H");
            tradeStatistics = baseMapper.getTradeStatisticsByDay(query); // 调用日维度 SQL

            // 生成与 SQL 返回格式一致的 timeList（00-23，字符串类型）
            timeList = DateUtils.getHourData(timeRange);
            if (timeList.isEmpty() || !timeList.get(0).matches("\\d{2}")) {
                timeList = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    timeList.add(String.format("%02d", i));
                }
            }

        } else if ("monthrange".equals(query.getTransactionType())) {
            query.setTimeFormat("%Y-%m");
            timeList = DateUtils.getMonthInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatistics(query); // 调用通用 SQL

        } else if ("week".equals(query.getTransactionType())) {
            // 生成与 SQL 返回格式一致的 timeList（周数，如 "45"，字符串类型）
            timeList = DateUtils.getWeekInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByWeek(query); // 调用周维度 SQL

        } else {
            query.setTimeFormat("%Y-%m-%d");
            timeList = DateUtils.getDatesInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatistics(query); // 调用通用 SQL
        }

        // 核心：遍历 tradeStatistics 时，使用 ContractTrendVO 的 getTradeTime() 和 getTradeCount()
        Map<String, Integer> dataMap = new HashMap<>();
        // 先判断集合不为 null，避免 NPE
        if (tradeStatistics != null) {
            for (ContractTrendVO vo : tradeStatistics) {
                // 过滤 null 对象，且确保 tradeTime 和 tradeCount 不为 null
                if (vo != null && vo.getTradeTime() != null && vo.getTradeCount() != null) {
                    dataMap.put(vo.getTradeTime(), vo.getTradeCount());
                }
            }
        }

        // 填充 countList（无数据补 0）
        countList.clear();
        for (String time : timeList) {
            countList.add(dataMap.getOrDefault(time, 0));
        }

        Map<String, List> result = new HashMap<>();
        result.put("timeList", timeList);
        result.put("countList", countList);
        return result;
    }
    // 复用已有的周范围生成方法（与客户模块保持一致）
    private List<String> getWeekInRange(String start, String end) {
        return DateUtils.getDatesInRange(start, end);
    }


    // 生成周时间标签（yyyy-MM-dd）
    private List<String> getWeekLabels(String startDate, String endDate) {
        // 实现参考DateUtils.getDatesInRange，返回日期列表
        return DateUtils.getDatesInRange(startDate, endDate);
    }

    // 生成月时间标签（yyyy-MM）
    private List<String> getMonthLabels(String startDate, String endDate) {
        List<String> labels = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LocalDate current = start;
        while (!current.isAfter(end)) {
            labels.add(current.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            current = current.plusMonths(1);
        }
        return labels;
    }

    private void handleContractProducts(Integer contractId, List<ProductVO> newProductList) {
        if (newProductList == null) return;

        List<ContractProduct> oldProducts = contractProductMapper.selectList(
                new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractId)
        );

        // === 1. 新增商品 ===
        List<ProductVO> newAdded = newProductList.stream()
                .filter(np -> oldProducts.stream().noneMatch(op -> op.getPId().equals(np.getPId())))
                .toList();
        for (ProductVO p : newAdded) {
            Product product = checkAndGetProduct(p.getPId(), p.getCount());
            decreaseStock(product, p.getCount());
            ContractProduct cp = buildContractProduct(contractId, product, p.getCount());
            contractProductMapper.insert(cp);
        }

        // === 2. 修改数量 ===
        List<ProductVO> changed = newProductList.stream()
                .filter(np -> oldProducts.stream()
                        .anyMatch(op -> op.getPId().equals(np.getPId()) && !op.getCount().equals(np.getCount())))
                .toList();
        for (ProductVO p : changed) {
            ContractProduct old = oldProducts.stream()
                    .filter(op -> op.getPId().equals(p.getPId()))
                    .findFirst().orElseThrow();

            Product product = checkAndGetProduct(p.getPId(), 0);
            int diff = p.getCount() - old.getCount();

            // 库存调整
            if (diff > 0) decreaseStock(product, diff);
            else if (diff < 0) increaseStock(product, -diff);

            // 更新合同商品
            old.setCount(p.getCount());
            old.setPrice(product.getPrice());
            old.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(p.getCount())));
            contractProductMapper.updateById(old);
        }

        // === 3. 删除商品 ===
        List<ContractProduct> removed = oldProducts.stream()
                .filter(op -> newProductList.stream().noneMatch(np -> np.getPId().equals(op.getPId())))
                .toList();
        for (ContractProduct rm : removed) {
            Product product = productMapper.selectById(rm.getPId());
            if (product != null) increaseStock(product, rm.getCount());
            contractProductMapper.deleteById(rm.getId());
        }
    }


    private Product checkAndGetProduct(Integer productId, int needCount) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new ServerException("商品不存在");
        if (needCount > 0 && product.getStock() < needCount) {
            throw new ServerException("商品库存不足");
        }
        return product;
    }

    private void decreaseStock(Product product, int count) {
        product.setStock(product.getStock() - count);
        product.setSales(product.getSales() + count);
        productMapper.updateById(product);
    }

    private void increaseStock(Product product, int count) {
        product.setStock(product.getStock() + count);
        product.setSales(product.getSales() - count);
        productMapper.updateById(product);
    }

    private ContractProduct buildContractProduct(Integer contractId, Product product, int count) {
        ContractProduct cp = new ContractProduct();
        cp.setCId(contractId);
        cp.setPId(product.getId());
        cp.setPName(product.getName());
        cp.setCount(count);
        cp.setPrice(product.getPrice());
        cp.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(count)));
        return cp;
    }


}