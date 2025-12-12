package com.crm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {

    @Autowired
    private JavaMailSender mailSender;

    // 发件人邮箱（从配置文件读取）
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送合同审核通过通知
     * @param toEmail 收件人邮箱（销售的邮箱）
     * @param contractName 合同名称
     * @param contractNumber 合同编号
     */
    public void sendContractApprovedNotice(String toEmail, String contractName, String contractNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // 发件人
        message.setTo(toEmail); // 收件人
        message.setSubject("【CRM系统】合同审核通过通知"); // 主题
        message.setText(String.format(
                "您好！\n您创建的合同已审核通过：\n合同名称：%s\n合同编号：%s\n请及时登录系统查看详情。",
                contractName, contractNumber
        )); // 内容
        mailSender.send(message);
    }
    // 新增：审核不通过通知
    public void sendContractRejectedNotice(String toEmail, String contractName, String contractNumber, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【CRM系统】合同审核不通过通知");
        message.setText(String.format(
                "您好！\n您创建的合同《%s》（编号：%s）审核未通过，原因：%s\n请登录系统查看详情并处理。",
                contractName, contractNumber, reason
        ));
        mailSender.send(message);
    }

    /**
     * 发送回款审核通过通知
     * @param toEmail 收件人邮箱（销售的邮箱）
     * @param contractName 关联的合同名称
     * @param paymentNumber 回款单编号
     */
    public void sendPaymentApprovedNotice(String toEmail, String contractName, Integer paymentNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // 发件人
        message.setTo(toEmail); // 收件人
        message.setSubject("【CRM系统】回款审核通过通知"); // 主题
        message.setText(String.format(
                "您好！\n您提交的回款单已审核通过：\n关联合同：%s\n回款编号：%s\n请及时登录系统查看详情。",
                contractName, paymentNumber
        )); // 内容
        mailSender.send(message);
    }
    /**
     * 发送回款审核不通过通知
     * @param toEmail 收件人邮箱（销售的邮箱）
     * @param contractName 关联的合同名称
     * @param paymentNumber 回款单编号
     * @param rejectReason 审核不通过原因（必填，明确告知驳回理由）
     */
    public void sendPaymentRejectedNotice(String toEmail, String contractName, Integer paymentNumber, String rejectReason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // 发件人
        message.setTo(toEmail); // 收件人
        message.setSubject("【CRM系统】回款审核不通过通知"); // 主题
        // 邮件内容：明确告知驳回结果+原因+操作指引
        message.setText(String.format(
                "您好！\n您提交的回款单审核未通过：\n关联合同：%s\n回款编号：%s\n驳回原因：%s\n请您核对信息后重新提交审核。",
                contractName, paymentNumber, rejectReason
        ));
        mailSender.send(message);
    }
}