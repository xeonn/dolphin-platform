package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.impl.collections.ListMapper;

public final class DolphinConstants {

    public static final String ADD_FROM_SERVER = ListMapper.class.getName() + "_ADD_FROM_SERVER";
    public static final String DEL_FROM_SERVER = ListMapper.class.getName() + "_DEL_FROM_SERVER";
    public static final String SET_FROM_SERVER = ListMapper.class.getName() + "_SET_FROM_SERVER";
    public static final String ADD_FROM_CLIENT = ListMapper.class.getName() + "_ADD_FROM_CLIENT";
    public static final String DEL_FROM_CLIENT = ListMapper.class.getName() + "_DEL_FROM_CLIENT";
    public static final String SET_FROM_CLIENT = ListMapper.class.getName() + "_SET_FROM_CLIENT";

    private DolphinConstants() {}


}
