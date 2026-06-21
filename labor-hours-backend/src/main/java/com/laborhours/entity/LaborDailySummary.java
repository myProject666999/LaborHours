package com.laborhours.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("labor_daily_summary")
public class LaborDailySummary {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("summary_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate summaryDate;

    @TableField("worker_id")
    private Long workerId;

    @TableField("order_id")
    private Long orderId;

    @TableField("process_id")
    private Long processId;

    @TableField("direct_hours")
    private BigDecimal directHours;

    @TableField("indirect_hours")
    private BigDecimal indirectHours;

    @TableField("overtime_hours")
    private BigDecimal overtimeHours;

    @TableField("total_hours")
    private BigDecimal totalHours;

    @TableField("completed_qty")
    private Integer completedQty;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
