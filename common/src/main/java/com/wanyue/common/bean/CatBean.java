package com.wanyue.common.bean;

public class CatBean implements ExportNamer {
    private String id;
    private String name;
    private boolean isChecked;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String exportName() {
        return name;
    }

    @Override
    public String exportId() {
        return id;
    }


    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
