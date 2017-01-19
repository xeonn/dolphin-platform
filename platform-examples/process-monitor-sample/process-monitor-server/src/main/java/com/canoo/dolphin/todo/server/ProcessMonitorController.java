/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.samples.processmonitor.model.ProcessBean;
import com.canoo.dolphin.samples.processmonitor.model.ProcessListBean;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.DolphinSession;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.canoo.dolphin.samples.processmonitor.ProcessMonitorConstants.CONTROLLER_NAME;
import java.util.function.Consumer;

@DolphinController(CONTROLLER_NAME)
public class ProcessMonitorController {

    private static DecimalFormat format = new DecimalFormat("0.00");

    @Inject
    private BeanManager beanManager;

    @Inject
    private DolphinSession session;

    @Inject
    private BackgroundTaskRunner backgroundTaskRunner;

    @DolphinModel
    private ProcessListBean processList;

    private Future<Void> executor;

    private OperatingSystem os;

    private GlobalMemory memory;

    @PostConstruct
    public void onInit() {
        SystemInfo si = new SystemInfo();
        os = si.getOperatingSystem();
        memory = si.getHardware().getMemory();
        update();
    }

    @PreDestroy
    public void onDestroy() {
        Optional.ofNullable(executor).ifPresent(new Consumer<Future<Void>>() {
            @Override
            public void accept(Future<Void> e) {
                e.cancel(true);
            }
        });
    }


    private void update() {
        List<OSProcess> procs = Arrays.asList(os.getProcesses(10, OperatingSystem.ProcessSort.CPU));
        for (OSProcess process : procs) {
            ProcessBean bean = null;
            if(processList.getItems().size() <= procs.indexOf(process)) {
                bean = beanManager.create(ProcessBean.class);
                processList.getItems().add(bean);
            } else {
                bean = processList.getItems().get(procs.indexOf(process));
            }
            bean.setProcessID(new Integer(process.getProcessID()).toString());
            bean.setCpuPercentage(format.format(100d * (process.getKernelTime() + process.getUserTime()) / process.getUpTime()));
            bean.setMemoryPercentage(format.format(100d * process.getResidentSetSize() / memory.getTotal()));
            bean.setName(process.getName());
        }
        backgroundTaskRunner.setTask(new Runnable() {
            @Override
            public void run() {
                session.runLater(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        });
    }

}
