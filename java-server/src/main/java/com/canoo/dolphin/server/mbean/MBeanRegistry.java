package com.canoo.dolphin.server.mbean;

import com.canoo.dolphin.event.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A general MBean registry
 */
public class MBeanRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MBeanRegistry.class);

    private final static MBeanRegistry INSTANCE = new MBeanRegistry();

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private AtomicBoolean mbeanSupport = new AtomicBoolean(true);

    private AtomicLong idGenerator = new AtomicLong(0L);

    /**
     * Register the given MBean based on the given description
     * @param mBean the bean
     * @param description the description
     * @return the subscription that can be used to unregister the bean
     */
    public Subscription register(Object mBean, MBeanDescription description) {
        return register(mBean, description.getMBeanName(getNextId()));
    }

    /**
     * Register the given MBean based on the given name
     * @param mBean the bean
     * @param name the name
     * @return the subscription that can be used to unregister the bean
     */
    public Subscription register(final Object mBean, final String name){
        try {
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
            }
        } catch (Exception e) {
            LOG.warn("Can not register MBean!", e);
        }
        return new Subscription() {
            @Override
            public void unsubscribe() {
            }
        };
    }

    /**
     * Returns true if MBean registry is supported / active
     * @return true if MBean registry is supported / active
     */
    public boolean isMbeanSupport() {
        return mbeanSupport.get();
    }

    /**
     * setter for the MBean support
     * @param mbeanSupport new state of MBean support
     */
    public void setMbeanSupport(boolean mbeanSupport) {
        this.mbeanSupport.set(mbeanSupport);
    }

    private String getNextId() {
        return idGenerator.getAndIncrement() + "";
    }

    /**
     * Returns the single instance
     * @return the single instance
     */
    public static MBeanRegistry getInstance() {
        return INSTANCE;
    }
}
