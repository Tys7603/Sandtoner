package com.wanyue.course.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class CourseLevel0Bean extends AbstractExpandableItem<CourseLevel1Bean> implements MultiItemEntity {
    public static final int LEVEL_TYPE=0;

    /**
     * id : 3
     * pid : 2
     * type : 1
     * name : 1.数据库简介
     * des : 图文
     * url :
     * istrial : 1
     * islive : 0
     * starttime : 0
     * list : []
     * status : 0
     * time_date :
     */

    private String id;
    private String pid;
    private String type;
    private String name;
    private String des;
    private String url;
    private int istrial;
    private int islive;
    private String starttime;
    private String status;
    private String time_date;

    private List<CourseLevel1Bean>list;


    @Override
    public int getLevel() {
        return LEVEL_TYPE;
    }

    @Override
    public int getItemType() {
        return LEVEL_TYPE;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIstrial() {
        return istrial;
    }

    public void setIstrial(int istrial) {
        this.istrial = istrial;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime_date() {
        return time_date;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }

    public List<CourseLevel1Bean> getList() {
        return list;
    }

    public void setList(List<CourseLevel1Bean> list) {
        this.list = list;
    }

    public void format(){
        setSubItems(list);
    }
}
