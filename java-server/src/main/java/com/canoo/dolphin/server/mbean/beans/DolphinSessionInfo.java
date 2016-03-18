/**
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
package com.canoo.dolphin.server.mbean.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hendrikebbers on 15.03.16.
 */
public class DolphinSessionInfo implements DolphinSessionInfoMBean {

    private String dolphinSessionId;

    public DolphinSessionInfo(String dolphinSessionId) {
        this.dolphinSessionId = dolphinSessionId;
    }

    @Override
    public String getDolphinSessionId() {
        return dolphinSessionId;
    }

    @Override
    public Set<String> getAttributesNames() {
        Set<String> dummySet = new HashSet<>();
        dummySet.add("Currently");
        dummySet.add("Not");
        dummySet.add("Supported");
        return dummySet;
    }

    @Override
    public Object getAttribute(String name) {
        return "Currently not supported";
    }
}
