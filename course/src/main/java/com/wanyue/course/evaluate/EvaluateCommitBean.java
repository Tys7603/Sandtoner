package com.wanyue.course.evaluate;

import com.wanyue.common.bean.commit.CommitEntity;

public class EvaluateCommitBean extends CommitEntity {
    private int position=-1;
    private String content;

    @Override
    public boolean observerCondition() {
        return isSelectStar()||isSelectContent();
    }
    public boolean isSelectStar(){
        return position>-1;
    }
    public boolean isSelectContent(){
        return fieldNotEmpty(content);
    }


    public boolean isEdit(){
        return isSelectStar()||isSelectContent();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        observer();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        observer();
    }
}
