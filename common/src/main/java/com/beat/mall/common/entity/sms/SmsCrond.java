package com.beat.mall.common.entity.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsCrond {
    public static final short STATUS_PENDING = 0;
    public static final short STATUS_SENDING = 1;
    public static final short STATUS_SUCCESS = 2;
    public static final short STATUS_RETRY_WAIT = 3;
    public static final short STATUS_FAILED_FINAL = 4;

    private Long id;
    private String phone;
    /** 任务创建时生成的验证码，不在异步提交响应中返回。 */
    private String content;
    private Short status;
    private Short retryCount;
    private Integer nextRetryTime;
    /** 当前集群中认领该任务的节点标识。 */
    private String lockOwner;
    /** 单次批量认领的唯一令牌，用于防止旧工作线程覆盖新认领结果。 */
    private String claimToken;
    private Integer claimTime;
    private Integer leaseExpireTime;
    private String errorMessage;
    private Integer sendTime;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
