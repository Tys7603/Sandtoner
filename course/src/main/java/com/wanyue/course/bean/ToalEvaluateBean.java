package com.wanyue.course.bean;

import java.util.List;

public class ToalEvaluateBean {
   private float totalStar;
   private List<EvaluateBean>list;

    public float getTotalStar() {
        return totalStar;
    }
    public void setTotalStar(float totalStar) {
        this.totalStar = totalStar;
    }
    public List<EvaluateBean> getList() {
        return list;
    }
    public void setList(List<EvaluateBean> list) {
        this.list = list;
    }
}
