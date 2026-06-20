package com.laborhours.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("prod_work_order")
public class ProdWorkOrder {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("work_order_no")
    private String workOrderNo;

    @TableField("order_id")
    private Long orderId;

    @TableField("process_id")
    private Long processId;

    @TableField("plan_qty")
    private Integer planQty;

    @TableField("completed_qty")
    private Integer completedQty;

    @TableField("standard_hour")
    private BigDecimal standardHour;

    @TableField("plan_start_time")
    private LocalDateTime planStartTime;

    @TableField("plan_end_time")
    private LocalDateTime planEndTime;

    @TableField("actual_start_time")
    private LocalDateTime actualStartTime;

    @TableField("actual_end_time")
    private LocalDateTime actualEndTime;

    @TableField("assign_worker_id")
    private Long assignWorkerId;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
