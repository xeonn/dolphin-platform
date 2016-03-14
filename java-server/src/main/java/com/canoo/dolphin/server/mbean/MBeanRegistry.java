package com.canoo.dolphin.server.mbean;

import com.canoo.dolphin.event.Subscription;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public class MBeanRegistry {

    private final static MBeanRegistry INSTANCE = new MBeanRegistry();

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private AtomicBoolean mbeanSupport = new AtomicBoolean(true);

    private AtomicLong idGenerator = new AtomicLong(0L);

    public Subscription register(Object mBean, MBeanDescription description) throws JMException {
        return register(mBean, description.getMBeanName(getNextId()));
    }

    public Subscription register(final Object mBean, final String name) throws JMException {
        if (mbeanSupport.get()) {
            final ObjectName objectName = new ObjectName(name);
            server.registerMBean(mBean, objectName);
            return new Subscription() {
                @Override
                public void unsubscribe() {
                    try {
                        server.unregisterMBean(objectName);
                    } catch (JMException e) {
                        throw new RuntimeException("Can not unsubscribe!", e);
                    }
                }
            };
        } else {
            return new Subscription() {
                @Override
                public void unsubscribe() {
                }
            };
        }
    }

    public boolean isMbeanSupport() {
        return mbeanSupport.get();
    }

    public void setMbeanSupport(boolean mbeanSupport) {
        this.mbeanSupport.set(mbeanSupport);
    }

    private String getNextId() {
        return idGenerator.getAndIncrement() + "";
    }

    public static MBeanRegistry getInstance() {
        return INSTANCE;
    }
}
