package com.canoo.dolphin.workflow.server.activiti;

import org.springframework.stereotype.Component;

/**
 * only here, because our example hiring process needs a service with exactly this name
 */
@Component
public class ResumeService {

    public void storeResume() {
        System.out.println("hello store resume");
    }
}
