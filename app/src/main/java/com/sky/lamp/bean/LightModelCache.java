package com.sky.lamp.bean;

import java.io.Serializable;
import java.util.HashMap;

public class LightModelCache implements Serializable {
    public HashMap<String,CommandLightMode> map = new HashMap<>();
}
