/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.config;

import com.canoo.dolphin.server.impl.ConfigurationFileLoader;

import java.util.logging.Level;

/**
 * This class defines the configuration of the Dolphin Platform. Normally the configuration is created based
 * on defaults and a property file (see {@link ConfigurationFileLoader}).
 */
public class DolphinPlatformConfiguration {

    private boolean useCrossSiteOriginFilter = true;

    private boolean garbageCollectionActive = false;

    private String dolphinPlatformServletMapping = "/dolphin";

    private Level openDolphinLogLevel = Level.SEVERE;

    public boolean isUseCrossSiteOriginFilter() {
        return useCrossSiteOriginFilter;
    }

    public void setUseCrossSiteOriginFilter(boolean useCrossSiteOriginFilter) {
        this.useCrossSiteOriginFilter = useCrossSiteOriginFilter;
    }

    public String getDolphinPlatformServletMapping() {
        return dolphinPlatformServletMapping;
    }

    public void setDolphinPlatformServletMapping(String dolphinPlatformServletMapping) {
        this.dolphinPlatformServletMapping = dolphinPlatformServletMapping;
    }

    public Level getOpenDolphinLogLevel() {
        return openDolphinLogLevel;
    }

    public void setOpenDolphinLogLevel(Level openDolphinLogLevel) {
        this.openDolphinLogLevel = openDolphinLogLevel;
    }

    public boolean isGarbageCollectionActive() {
        return garbageCollectionActive;
    }

    public void setGarbageCollectionActive(boolean garbageCollectionActive) {
        this.garbageCollectionActive = garbageCollectionActive;
    }
}
