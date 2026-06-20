package com.laborhours.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_worker")
public class SysWorker {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("worker_no")
    private String workerNo;

    @TableField("worker_name")
    private String workerName;

    @TableField("gender")
    private Integer gender;

    @TableField("phone")
    private String phone;

    @TableField("team_id")
    private Long teamId;

    @TableField("position")
    private String position;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
