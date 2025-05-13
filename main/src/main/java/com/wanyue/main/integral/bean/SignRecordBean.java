package com.wanyue.main.integral.bean;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;


public class SignRecordBean {
    private String code;
    private String todayReward;


    @SerializedName("config")
    @JSONField(name = "config")
    private List<WeekSignBean>weekSign;


    @SerializedName("issign")
    @JSONField(name = "issign")
    private int isSign;


    @SerializedName("days")
    @JSONField(name = "days")
    private int totalDay;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTodayReward() {
        int id=getTotalDay();
        if(getIsSign()==0){
           id=id++;
        }

      WeekSignBean weekSignBean=  getCurrentWeekSign(id);
        if(weekSignBean!=null){
           todayReward= weekSignBean.getReward();
        }
        return todayReward;
    }

    public void setTodayReward(String todayReward) {
        this.todayReward = todayReward;
    }

    public List<WeekSignBean> getWeekSign() {
        return weekSign;
    }

    public void formatWeekSign(){
        if(weekSign==null ){
           return;
        }
        List<WeekSignBean>list=new ArrayList<>();
        int size= ListUtil.size(weekSign);
        for(int i=0;i<size;i++){
            WeekSignBean weekSignBean=weekSign.get(i);
            if (weekSignBean.getDay() != null && weekSignBean.getDay().contains("sky")) {
                weekSignBean.setDay(weekSignBean.getDay().replace("sky", "day"));
            }
            WeekSignBean nextSign=ListUtil.safeGetData(weekSign,i+1);

            list.add(weekSignBean);
            if(i<size-1){
               WeekSignBean line=new WeekSignBean();
               line.setType(WeekSignBean.LINE);
               if(nextSign!=null){
                  line.setId(nextSign.getId());
               }
               list.add(line);
            }
        }
        weekSign=list;
    }

    public void setWeekSign(List<WeekSignBean> weekSign) {
        this.weekSign = weekSign;
    }

    public int getIsSign() {
        return isSign;
    }

    public void setIsSign(int isSign) {
        this.isSign = isSign;
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }

    public WeekSignBean getCurrentWeekSign(int id) {
        int size=ListUtil.size(weekSign);
       if(size<=0){
          return null;
       }
       WeekSignBean weekSignBean=null;
       for(int i=0;i<size;i++){
          WeekSignBean temp=weekSign.get(i);
           if(id==temp.getId()){
               weekSignBean=temp;
              break;
           }
       }

       if(weekSignBean==null){
          weekSignBean=ListUtil.safeGetData(weekSign,0);
       }

       return weekSignBean;
    }
}
