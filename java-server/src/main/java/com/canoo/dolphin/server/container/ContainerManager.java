package com.canoo.dolphin.server.container;

/**
 * <p>
 * This interface defines the platform / container specific controller management.
 * By default Dolphin Platform provides 2 implementations of this interface for Spring and JavaEE that
 * can be found in the specific modules. If you want to add the support for a different platform you need to
 * provide a custom implementation of this interface.
 * </p>
 * <p>
 * Here is a short overview how the architecture is defined:
 * <br>
 * <center><img src="doc-files/platform-impl.png" alt="model is synchronized between client and server"></center>
 * </p>
 */
public interface ContainerManager {

    <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector);

    void destroyController(Object instance);

}
