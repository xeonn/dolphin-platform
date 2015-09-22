package com.canoo.dolphin.impl;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface Constants {

    public static final String DOLPHIN_PLATFORM_PREFIX = "dolphin_platform_intern_";

    public static final String RELEASE_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "release";

    public static final String POLL_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "longPoll";

    public static final String REGISTER_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "registerController";

    public static final String DESTROY_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "destroyController";

    public static final String CALL_CONTROLLER_ACTION_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "callControllerAction";

    public static final String INIT_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "initClientContext";

    public static final String CLIENT_ID_HTTP_HEADER_NAME = DOLPHIN_PLATFORM_PREFIX + "dolphin-client-id";

}
