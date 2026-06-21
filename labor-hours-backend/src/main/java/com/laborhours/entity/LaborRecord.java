package com.laborhours.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("labor_record")
public class LaborRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("record_no")
    private String recordNo;

    @TableField("worker_id")
    private Long workerId;

    @TableField("work_order_id")
    private Long workOrderId;

    @TableField("order_id")
    private Long orderId;

    @TableField("process_id")
    private Long processId;

    @TableField("labor_type")
    private Integer laborType;

    @TableField("report_type")
    private Integer reportType;

    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @TableField("work_hours")
    private BigDecimal workHours;

    @TableField("completed_qty")
    private Integer completedQty;

    @TableField("unit_hour")
    private BigDecimal unitHour;

    @TableField("is_overtime")
    private Integer isOvertime;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
