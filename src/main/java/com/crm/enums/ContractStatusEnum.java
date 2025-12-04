//package com.crm.enums;
//
//public enum ContractStatusEnum {
//    DRAFT(0, "草稿"),
//    PENDING_APPROVAL(1, "待审核"),
//    APPROVED(2, "审核通过"),
//    REJECTED(3, "审核拒绝");
//
//    private final Integer code;
//    private final String desc;
//
//    ContractStatusEnum(Integer code, String desc) {
//        this.code = code;
//        this.desc = desc;
//    }
//
//    // getter方法
//    public Integer getCode() { return code; }
//    public String getDesc() { return desc; }
//}
package com.crm.enums;

import lombok.Getter;

/**
 * 合同状态枚举
 * 内部包含了合同自身状态和审核操作相关的枚举
 */
public class ContractStatusEnum {

    /**
     * 合同自身状态
     */
    @Getter
    public enum Status {
        DRAFT(0, "草稿"),
        PENDING_APPROVAL(1, "待审核"),
        APPROVED(2, "审核通过"),
        REJECTED(3, "审核拒绝");

        private final Integer code;
        private final String desc;

        Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 审核操作类型 (用于 Approval 表的 type 字段)
     * 区分是合同审核还是回款审核
     */
    @Getter
    public enum AuditType {
        CONTRACT_AUDIT(0, "合同审核"),
        PAYMENT_AUDIT(1, "回款审核");

        private final Integer code;
        private final String desc;

        AuditType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 审核结果 (用于 Approval 表的 status 字段)
     */
    @Getter
    public enum AuditResult {
        PASS(0, "通过"),
        REJECT(1, "拒绝");

        private final Integer code;
        private final String desc;

        AuditResult(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}