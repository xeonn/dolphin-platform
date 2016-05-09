package com.canoo.dolphin.server.impl;

/**
 * A helper class that can be used to define some feature flags for unstable features that can be activated for tests.
 */
public class UnstableFeatureFlags {

    /**
     * Defines if the DOlphin Bean garbage Collection should be active
     */
    private static boolean USE_GC = false;

    public static synchronized boolean isUseGc() {
        return USE_GC;
    }

    public static synchronized void setUseGc(boolean useGc) {
        USE_GC = useGc;
    }
}
