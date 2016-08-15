package com.canoo.dolphin.test.scopes;

import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.test.SpringTestNGControllerTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

@SpringApplicationConfiguration(classes = ScopesConfig.class)
public class ScopeTestControllerTest extends SpringTestNGControllerTest {

    @Inject
    private RequestService requestService;

    @Inject
    private ClientService clientService;

    @Inject
    private SessionService sessionService;

    @Inject
    private SingletonService singletonService;

    @Test
    public void testAllScopes() {
        ControllerUnderTest<ScopeModel> controller = createController("ScopeTestController");

        Assert.assertTrue(requestService.getId() != null);
        Assert.assertTrue(controller.getModel().requestServiceIdProperty().get() != null);
        Assert.assertTrue(requestService.getId().equals(controller.getModel().requestServiceIdProperty().get()));

        Assert.assertTrue(clientService.getId() != null);
        Assert.assertTrue(controller.getModel().clientServiceIdProperty().get() != null);
        Assert.assertTrue(clientService.getId().equals(controller.getModel().clientServiceIdProperty().get()));

        Assert.assertTrue(sessionService.getId() != null);
        Assert.assertTrue(controller.getModel().sessionServiceIdProperty().get() != null);
        Assert.assertTrue(sessionService.getId().equals(controller.getModel().sessionServiceIdProperty().get()));

        Assert.assertTrue(singletonService.getId() != null);
        Assert.assertTrue(controller.getModel().singletonServiceIdProperty().get() != null);
        Assert.assertTrue(singletonService.getId().equals(controller.getModel().singletonServiceIdProperty().get()));

        controller.destroy();
    }
}
