package com.jxdinfo.doc.manager.componentmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 复用登记与组件的管理表
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@TableName("multiplex_project_component")
public class MultiplexProjectComponent extends Model<MultiplexProject> {
    private static final long serialVersionUID = 1L;
    @TableId("multiplex_id")
    private  String multiplexId;

    /**
     * 项目ID
     */
    @TableField("project_id")
    private String projectId;

    public String getMultiplexId() {
        return multiplexId;
    }

    public void setMultiplexId(String multiplexId) {
        this.multiplexId = multiplexId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    /**
     * ID
     */
    @TableField("show_order")
    private int showOrdet;



    /**
     * 组件ID
     */
    @TableField("component_id")
    private String componentId;
    /**
     * 组件ID
     */
    @TableField("project_economize")
    private String economize;
    @Override
    protected Serializable pkVal() {
        return multiplexId;
    }

    public String getEconomize() {
        return economize;
    }

    public void setEconomize(String economize) {
        this.economize = economize;
    }

    public int getShowOrdet() {
        return showOrdet;
    }

    public void setShowOrdet(int showOrdet) {
        this.showOrdet = showOrdet;
    }
}
