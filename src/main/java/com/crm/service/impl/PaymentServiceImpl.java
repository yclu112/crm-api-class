package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.entity.*;
import com.crm.enums.PaymentMethodEnum;
import com.crm.mapper.ApprovalMapper;
import com.crm.mapper.ContractMapper;
import com.crm.mapper.PaymentMapper;
import com.crm.mapper.SysManagerMapper;
import com.crm.query.ApprovalQuery;
import com.crm.query.IdQuery;
import com.crm.query.PaymentQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.PaymentService;
import com.crm.utils.MailUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Slf4j
@Service
@AllArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private final ApprovalMapper approvalMapper;
    private final MailUtils mailUtils;
    private final SysManagerMapper sysManagerMapper;

    @Override
    public PageResult<Payment> getPage(PaymentQuery query) {
        Page<Payment> page = new Page<>(query.getPage(), query.getLimit());
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();

        if (query.getContractId() != null) {
            wrapper.eq(Payment::getContractId, query.getContractId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Payment::getStatus, query.getStatus());
        }

        wrapper.eq(Payment::getDeleteFlag, 0);
        wrapper.orderByDesc(Payment::getCreateTime);

        IPage<Payment> result = this.baseMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void createPayment(Payment payment) {
//        Integer contractId = payment.getContractId();
//        if (contractId == null) {
//            throw new ServerException("合同ID不能为空");
//        }
//
//        Contract contract = contractMapper.selectById(contractId);
//        if (contract == null) {
//            throw new ServerException("关联的合同不存在");
//        }
//        if (contract.getStatus() != 2) {
//            throw new ServerException("只有审核通过的合同才能创建回款单");
//        }
//
//        payment.setCustomerId(contract.getCustomerId());
//        payment.setContractNumber(contract.getNumber());
//        payment.setContractName(contract.getName());
//        payment.setCreaterId(SecurityUser.getManagerId());
//        payment.setStatus(1);
//        payment.setDeleteFlag(0);
//        payment.setCreateTime(LocalDateTime.now());
//        payment.setUpdateTime(LocalDateTime.now());
//
//        this.baseMapper.insert(payment);
//    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void approvalPayment(ApprovalQuery query) {
//        Payment payment = this.baseMapper.selectById(query.getId());
//        if (payment == null) {
//            throw new ServerException("回款单不存在");
//        }
//        if (payment.getStatus() != 1) {
//            throw new ServerException("该回款单状态不正确，无法审核");
//        }
//        if (query.getType() == 2 && StringUtils.isEmpty(query.getComment())) {
//            throw new ServerException("拒绝审核时必须填写拒绝原因");
//        }
//
//        Approval approval = new Approval();
//        approval.setType(1);
//        approval.setCreaterId(SecurityUser.getManagerId());
//        approval.setContractId(payment.getContractId());
//        approval.setPaymentId(payment.getId());
//        approval.setStatus(query.getType() == 1 ? 1 : 2);
//        approval.setComment(query.getComment());
//        approval.setCreateTime(LocalDateTime.now());
//        approvalMapper.insert(approval);
//
//        payment.setStatus(query.getType() == 1 ? 2 : 3);
//        payment.setUpdateTime(LocalDateTime.now());
//        this.baseMapper.updateById(payment);
//
//        if (query.getType() == 1) {
//            contractMapper.updateReceivedAmount(payment.getContractId(), payment.getAmount());
//        }
//    }
//@Override
//@Transactional(rollbackFor = Exception.class)
//public void approvalPayment(ApprovalQuery query) {
//    // 1. 根据ID查询回款单信息
//    Payment payment = this.baseMapper.selectById(query.getId());
//    if (payment == null) {
//        throw new ServerException("回款单不存在");
//    }
//
//    // 2. 校验回款单当前状态是否为“待审核”（假设状态 1 为待审核）
//    if (payment.getStatus() != 1) {
//        throw new ServerException("该回款单状态不正确，无法审核");
//    }
//
//    // 3. 审核不通过时的特殊校验
//    if (query.getType() == 2) { // 如果是拒绝审核
//        if (query.getComment() == null || query.getComment().trim().isEmpty()) {
//            throw new ServerException("拒绝审核时必须填写拒绝原因");
//        }
//        if (query.getActualAmount() == null || query.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new ServerException("拒绝审核时必须填写有效的实际回款金额（大于0）");
//        }
//        if (query.getActualAmount().compareTo(payment.getAmount()) > 0) {
//            throw new ServerException("实际回款金额不能超过应回款金额");
//        }
//    }
//
//    // 4. 构建并保存审核记录（Approval表）
//    Approval approval = new Approval();
//    approval.setType(1); // 假设 1 代表是回款审核
//    approval.setCreaterId(SecurityUser.getManagerId()); // 获取当前操作用户ID
//    approval.setContractId(payment.getContractId()); // 关联的合同ID
//    approval.setPaymentId(payment.getId()); // 关联的回款单ID
//    approval.setStatus(query.getType()); // 直接使用query中的状态 (1=通过，2=拒绝)
//    approval.setComment(query.getComment()); // 审核备注/原因
//    approval.setCreateTime(LocalDateTime.now()); // 创建时间
//    approvalMapper.insert(approval); // 保存到数据库
//
//    // 5. 更新回款单状态（Payment表）
//    // 假设 2 = 已通过, 3 = 已拒绝
//    payment.setStatus(query.getType() == 1 ? 2 : 3);
//    payment.setUpdateTime(LocalDateTime.now()); // 更新时间
//    this.baseMapper.updateById(payment); // 保存更新
//
//    // 6. 联动更新合同的已回款金额（Contract表）
//    BigDecimal amountToAdd;
//    if (query.getType() == 1) {
//        // 6.1 审核通过：累加全部应回款金额
//        amountToAdd = payment.getAmount();
//    } else {
//        // 6.2 审核不通过：累加用户输入的实际回款金额
//        amountToAdd = query.getActualAmount();
//    }
//    contractMapper.updateReceivedAmount(payment.getContractId(), amountToAdd);
//}

    @Resource
    private PaymentMapper paymentMapper;

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void approvalPayment(ApprovalQuery query) {
//        log.info("开始审核回款单, ID: {}, 审核类型: {}", query.getId(), query.getType() == 0 ? "通过" : "拒绝");
//
//        // 1. 校验传入的ID是否有效
//        if (query.getId() == null) {
//            throw new ServerException("回款单ID不能为空");
//        }
//
//        // 2. 根据ID查询数据库中的原始回款记录
//        Payment payment = baseMapper.selectById(query.getId());
//        if (payment == null) {
//            throw new ServerException("回款记录不存在或已被删除");
//        }
//
//        // 3. 校验回款单当前状态是否为“待审核” (假设 1 代表待审核)
//        if (payment.getStatus() != 1) {
//            String statusDesc = payment.getStatus() == 2 ? "已通过" : (payment.getStatus() == 3 ? "已拒绝" : "未知");
//            throw new ServerException(String.format("该回款单状态为【%s】，无法重复审核", statusDesc));
//        }
//
//        // 4. 校验审核类型是否有效
//        if (query.getType() == null || (query.getType() != 0 && query.getType() != 1)) {
//            throw new ServerException("无效的审核类型，请选择“通过”或“拒绝”");
//        }
//
//        // 5. 【修改点】拒绝审核时必须填写备注，通过审核时可以不填
//        if (query.getType() == 1 && !StringUtils.hasText(query.getComment())) {
//            throw new ServerException("拒绝审核时必须填写拒绝原因");
//        }
//
//        // 6. 根据审核类型（通过/拒绝）执行不同逻辑
//        if (query.getType() == 0) { // 6.1 如果是“审核通过”
//
//            // 6.1.1 校验支付方式是否选择
//            if (query.getPaymentMethod() == null) {
//                throw new ServerException("审核通过时，支付方式不能为空");
//            }
//
//            // 使用枚举校验支付方式的有效性
//            PaymentMethodEnum methodEnum = PaymentMethodEnum.getByCode(query.getPaymentMethod());
//            if (methodEnum == null) {
//                throw new ServerException("无效的支付方式");
//            }
//            payment.setPaymentMethod(methodEnum.getCode());
//
//            log.info("回款单 {} 支付方式为: {}", query.getId(), methodEnum.getDesc());
//
//            // 6.1.2 校验支付时间是否选择
//            if (!StringUtils.hasText(query.getPaymentTime())) {
//                throw new ServerException("审核通过时，支付时间不能为空");
//            }
//// 设置支付时间（显式指定格式）
//            try {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                payment.setPaymentTime(LocalDateTime.parse(query.getPaymentTime(), formatter));
//            } catch (Exception e) {
//                log.error("支付时间格式错误: {}", query.getPaymentTime(), e);
//                throw new ServerException("支付时间格式错误，请使用yyyy-MM-dd HH:mm:ss格式");
//            }
//
//            // 6.1.3 更新回款单状态为“审核通过” (假设 2 代表审核通过)
//            payment.setStatus(2);
//            SysManager PaymentCreator = sysManagerMapper.selectById(payment.getCreaterId());
//            if (PaymentCreator != null && PaymentCreator.getEmail() != null && !PaymentCreator.getEmail().trim().isEmpty()) {
//                // 3. 调用MailUtils发送回款审核通过邮件
//                mailUtils.sendPaymentApprovedNotice(
//                        PaymentCreator.getEmail(),
//                        payment.getContractName(),
//                        payment.getNumber()
//                );
//
//            } else { // 6.2 如果是“审核拒绝”
//
//                // 拒绝时，清空可能存在的支付信息
//                payment.setPaymentMethod(null);
//                payment.setPaymentTime(null);
//
//                // 6.2.1 更新回款单状态为“审核拒绝” (假设 3 代表审核拒绝)
//                payment.setStatus(3);
//            }
//
//            // 7. 设置审核备注 (如果不为空)
//            // 注意：你的 Payment 实体中没有 comment 字段。
//            // 为了让逻辑完整，这里假设你会添加它。如果不添加，这步可以省略。
//            // if (StringUtils.hasText(query.getComment())) {
//            //     payment.setComment(query.getComment());
//            // }
//
//            // 8. 将更新后的回款单信息保存回数据库
//            baseMapper.updateById(payment);
//
//            log.info("回款单 {} 审核完成, 新状态: {}", query.getId(),
//                    payment.getStatus() == 2 ? "已通过" : (payment.getStatus() == 3 ? "已拒绝" : "未知"));
//        }
//
//    }
@Override
@Transactional(rollbackFor = Exception.class)
public void approvalPayment(ApprovalQuery query) {
    log.info("===== 开始审核回款单 =====");
    log.info("审核参数：回款单ID={}, 审核类型={}（0=通过/1=拒绝）", query.getId(), query.getType());

    // 1. 基础参数校验
    if (query.getId() == null) {
        throw new ServerException("回款单ID不能为空");
    }

    // 2. 查询回款单（确保存在且未删除）
    Payment payment = baseMapper.selectById(query.getId());
    if (payment == null) {
        throw new ServerException("回款记录不存在或已被删除");
    }
    log.info("查询到回款单：ID={}, 合同ID={}, 应回款金额={}, 当前状态={}",
            payment.getId(), payment.getContractId(), payment.getAmount(), payment.getStatus());

    // 3. 校验状态（仅待审核状态可操作）
    if (payment.getStatus() != 1) {
        String statusDesc = payment.getStatus() == 2 ? "已通过" : (payment.getStatus() == 3 ? "已拒绝" : "未知");
        throw new ServerException(String.format("该回款单状态为【%s】，无法重复审核", statusDesc));
    }

    // 4. 校验审核类型
    if (query.getType() == null || (query.getType() != 0 && query.getType() != 1)) {
        throw new ServerException("无效的审核类型，请选择“通过”或“拒绝”");
    }

    // 5. 拒绝时必须填写备注
    if (query.getType() == 1 && !StringUtils.hasText(query.getComment())) {
        throw new ServerException("拒绝审核时必须填写拒绝原因");
    }

    // 6. 查询关联合同（确保合同存在）
    Contract contract = contractMapper.selectById(payment.getContractId());
    if (contract == null) {
        throw new ServerException("关联的合同不存在，无法审核");
    }
    BigDecimal contractTotal = contract.getAmount() == null ? BigDecimal.ZERO : contract.getAmount();
    BigDecimal contractReceived = contract.getReceivedAmount() == null ? BigDecimal.ZERO : contract.getReceivedAmount();
    log.info("关联合同信息：合同ID={}, 合同编号={}, 总金额={}, 当前已收款={}",
            contract.getId(), contract.getNumber(), contractTotal, contractReceived);

    BigDecimal amountToAddToContract = BigDecimal.ZERO;

    // 7. 审核通过逻辑
    if (query.getType() == 0) {
        // 7.1 校验支付方式
        if (query.getPaymentMethod() == null) {
            throw new ServerException("审核通过时，支付方式不能为空");
        }
        PaymentMethodEnum methodEnum = PaymentMethodEnum.getByCode(query.getPaymentMethod());
        if (methodEnum == null) {
            throw new ServerException("无效的支付方式");
        }
        payment.setPaymentMethod(methodEnum.getCode());
        log.info("回款单支付方式：{}（编码：{}）", methodEnum.getDesc(), methodEnum.getCode());

        // 7.2 校验支付时间
        if (!StringUtils.hasText(query.getPaymentTime())) {
            throw new ServerException("审核通过时，支付时间不能为空");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            payment.setPaymentTime(LocalDateTime.parse(query.getPaymentTime(), formatter));
            log.info("回款单支付时间：{}", payment.getPaymentTime());
        } catch (Exception e) {
            log.error("支付时间格式错误: {}", query.getPaymentTime(), e);
            throw new ServerException("支付时间格式错误，请使用yyyy-MM-dd HH:mm:ss格式");
        }

        // 7.3 核心：获取本次要累加的金额（直接取回款单金额，不计算剩余）
        BigDecimal currentPaymentAmount = payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount();
        if (currentPaymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServerException("回款单金额必须大于0");
        }

        // 7.4 校验：累加后不超过合同总金额
        if (contractReceived.add(currentPaymentAmount).compareTo(contractTotal) > 0) {
            throw new ServerException(String.format("本次回款后已收款将超过合同总金额（总金额：%s，已收：%s，本次：%s）",
                    contractTotal, contractReceived, currentPaymentAmount));
        }

        // 7.5 更新回款单状态和累加金额
        payment.setStatus(2); // 已通过
        amountToAddToContract = currentPaymentAmount;
        log.info("审核通过 - 本次累加金额：{}", amountToAddToContract);

        // 7.6 发送审核通过邮件（可选）
        if (payment.getCreaterId() != null) {
            SysManager paymentCreator = sysManagerMapper.selectById(payment.getCreaterId());
            if (paymentCreator != null && StringUtils.hasText(paymentCreator.getEmail())) {
                mailUtils.sendPaymentApprovedNotice(
                        paymentCreator.getEmail(),
                        payment.getContractName(),
                        payment.getNumber()
                );
                log.info("已向创建人{}（邮箱：{}）发送审核通过邮件",
                        paymentCreator.getNickname(), paymentCreator.getEmail());
            }
        }

    } else { // 8. 审核拒绝逻辑
        // 8.1 校验实际回款金额
        if (query.getActualAmount() == null || query.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServerException("拒绝审核时，实际回款金额必须大于0");
        }
        BigDecimal paymentAmount = payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount();
        if (query.getActualAmount().compareTo(paymentAmount) > 0) {
            throw new ServerException("实际回款金额不能超过应回款金额");
        }

        // 8.2 清空支付信息
        payment.setPaymentMethod(null);
        payment.setPaymentTime(null);

        // 8.3 更新回款单状态和累加金额（累加实际输入金额）
        payment.setStatus(3); // 已拒绝
        amountToAddToContract = query.getActualAmount();
        log.info("审核拒绝 - 本次累加金额（实际回款）：{}", amountToAddToContract);
    }

    // 9. 更新回款单信息到数据库
    payment.setUpdateTime(LocalDateTime.now());
    baseMapper.updateById(payment);
    log.info("回款单状态更新完成：ID={}, 新状态={}",
            payment.getId(), payment.getStatus() == 2 ? "已通过" : "已拒绝");

    // 10. 核心：联动累加合同已收款金额（强制累加，不替换）
    if (amountToAddToContract.compareTo(BigDecimal.ZERO) > 0) {
        // 直接调用原生SQL累加方法，绕开实体更新问题
        contractMapper.updateReceivedAmount(payment.getContractId(), amountToAddToContract);
        log.info("已调用合同累加SQL：合同ID={}, 累加金额={}", payment.getContractId(), amountToAddToContract);

        // 验证：更新后立即查询合同，确认累加结果
        Contract updatedContract = contractMapper.selectById(payment.getContractId());
        BigDecimal newReceivedAmount = updatedContract.getReceivedAmount() == null ? BigDecimal.ZERO : updatedContract.getReceivedAmount();
        log.info("✅ 合同已收款累加成功！");
        log.info("合同ID：{}", updatedContract.getId());
        log.info("累加前已收：{}", contractReceived);
        log.info("本次累加：{}", amountToAddToContract);
        log.info("累加后已收：{}", newReceivedAmount);
        log.info("合同总金额：{}", contractTotal);
    } else {
        log.warn("❌ 未执行合同累加：本次累加金额为{}（≤0）", amountToAddToContract);
    }

    log.info("===== 回款单审核流程全部完成 =====");
}

}