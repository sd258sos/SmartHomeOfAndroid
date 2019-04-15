package com.example.luxianglin.smarthomeofandroid.Util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luxianglin on 2016/12/20.
 */

public class ListSocket implements Serializable {
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
