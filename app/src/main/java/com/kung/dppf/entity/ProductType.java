package com.kung.dppf.entity;

import android.view.View;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProductType {
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long _id;

    @Unique
    private String TypeName; //名称
    //删除标记
    private String delFlag; //删除标记,0正常,1删除
    //修改日期
    private String updateTime;

    @Transient
    private String index;
    @Transient
    private String indexName;
    @Transient
    private int showSelect = View.GONE;
    @Transient int showModify = View.VISIBLE;

    @Generated(hash = 1870438769)
    public ProductType(Long _id, String TypeName, String delFlag,
            String updateTime) {
        this._id = _id;
        this.TypeName = TypeName;
        this.delFlag = delFlag;
        this.updateTime = updateTime;
    }
    @Generated(hash = 2067609092)
    public ProductType() {
    }

    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    
    public String getTypeName() {
        return this.TypeName;
    }
    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }
    public String getDelFlag() {
        return this.delFlag;
    }
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
    public String getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public int getShowSelect() {
        return showSelect;
    }

    public void setShowSelect(int showSelect) {
        this.showSelect = showSelect;
    }

    public int getShowModify() {
        return showModify;
    }

    public void setShowModify(int showModify) {
        this.showModify = showModify;
    }
}
