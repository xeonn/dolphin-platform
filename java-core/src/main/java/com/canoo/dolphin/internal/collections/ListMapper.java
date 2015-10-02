package com.canoo.dolphin.internal.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.internal.info.PropertyInfo;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface ListMapper {

    void processEvent(PropertyInfo observableListInfo, String sourceId, ListChangeEvent<?> event);
}
