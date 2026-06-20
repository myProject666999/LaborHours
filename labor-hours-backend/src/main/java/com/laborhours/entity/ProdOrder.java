package com.laborhours.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("prod_order")
public class ProdOrder {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("order_name")
    private String orderName;

    @TableField("product_name")
    private String productName;

    @TableField("product_spec")
    private String productSpec;

    @TableField("plan_qty")
    private Integer planQty;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("plan_start_date")
    private LocalDate planStartDate;

    @TableField("plan_end_date")
    private LocalDate planEndDate;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
