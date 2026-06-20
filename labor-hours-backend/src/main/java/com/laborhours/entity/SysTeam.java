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
@TableName("sys_team")
public class SysTeam {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("team_no")
    private String teamNo;

    @TableField("team_name")
    private String teamName;

    @TableField("leader_id")
    private Long leaderId;

    @TableField("workshop")
    private String workshop;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
