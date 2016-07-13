package org.opendolphin.core.client.comm;

import java.util.List;
import java.util.Map;

/**
 * interface for data point handler
 */
public interface OnFinishedData extends OnFinishedHandler {

    void onFinishedData(List<Map> data);

}
