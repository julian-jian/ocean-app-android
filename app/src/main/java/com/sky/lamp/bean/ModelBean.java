package com.sky.lamp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelBean implements Serializable {
    public List<CommandLightMode> lightModes = new ArrayList<>();
}