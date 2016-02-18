package com.canoo.dolphin.impl.codec;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.NamedCommand;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestOptimizedJsonCodec {

    @Test
    public void shouldEncodeEmptyList() {
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>emptyList());
        assertThat(actual, is("[]"));
    }

    @Test
    public void shouldEncodeSingleCreatePresentationModelCommand() {
        final Command command = createCPMCommand();
        final String actual = new OptimizedJsonCodec().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createCPMCommandString() + "]"));
    }

    @Test
    public void shouldEncodeSingleNamedCommand() {
        final Command command = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createNamedCommandString() + "]"));
    }

    @Test
    public void shouldEncodeTwoCustomCodecCommands() {
        final Command command = createCPMCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(command, command));
        final String expected = createCPMCommandString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeTwoStandardCodecCommands() {
        final Command command = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(command, command));
        final String expected = createNamedCommandString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeCustomCodecCommandAndStandardCodecCommand() {
        final Command customCodecCommand = createCPMCommand();
        final Command standardCodecCommand = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(customCodecCommand, standardCodecCommand));
        final String customCodecCommandString = createCPMCommandString();
        final String standardCodecCommandString = createNamedCommandString();
        assertThat(actual, is("[" + customCodecCommandString + "," + standardCodecCommandString + "]"));
    }

    @Test
    public void shouldEncodeStandardCodecCommandAndCustomCodecCommand() {
        final Command customCodecCommand = createCPMCommand();
        final Command standardCodecCommand = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(standardCodecCommand, customCodecCommand));
        final String customCodecCommandString = createCPMCommandString();
        final String standardCodecCommandString = createNamedCommandString();
        assertThat(actual, is("[" + standardCodecCommandString + "," + customCodecCommandString + "]"));
    }



    private static CreatePresentationModelCommand createCPMCommand() {
        final CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("05ee43b7-a884-4d42-9fc5-00b083664eed");
        command.setClientSideOnly(false);
        command.setPmType("com.canoo.icos.casemanager.model.casedetails.CaseInfoBean");

        final Map<String, Object> sourceSystem = new HashMap<>();
        sourceSystem.put("propertyName", "@@@ SOURCE_SYSTEM @@@");
        sourceSystem.put("id", "3204S");
        sourceSystem.put("qualifier", null);
        sourceSystem.put("value", "server");
        sourceSystem.put("baseValue", "server");
        sourceSystem.put("tag", "VALUE");

        final Map<String, Object> caseDetailsLabel = new HashMap<>();
        caseDetailsLabel.put("propertyName", "caseDetailsLabel");
        caseDetailsLabel.put("id", "3205S");
        caseDetailsLabel.put("qualifier", null);
        caseDetailsLabel.put("value", null);
        caseDetailsLabel.put("baseValue", null);
        caseDetailsLabel.put("tag", "VALUE");

        final Map<String, Object> caseIdLabel = new HashMap<>();
        caseIdLabel.put("propertyName", "caseIdLabel");
        caseIdLabel.put("id", "3206S");
        caseIdLabel.put("qualifier", null);
        caseIdLabel.put("value", null);
        caseIdLabel.put("baseValue", null);
        caseIdLabel.put("tag", "VALUE");

        final Map<String, Object> statusLabel = new HashMap<>();
        statusLabel.put("propertyName", "statusLabel");
        statusLabel.put("id", "3207S");
        statusLabel.put("qualifier", null);
        statusLabel.put("value", null);
        statusLabel.put("baseValue", null);
        statusLabel.put("tag", "VALUE");

        final Map<String, Object> status = new HashMap<>();
        status.put("propertyName", "status");
        status.put("id", "3208S");
        status.put("qualifier", null);
        status.put("value", null);
        status.put("baseValue", null);
        status.put("tag", "VALUE");

        command.setAttributes(Arrays.asList(sourceSystem, caseDetailsLabel, caseIdLabel, statusLabel, status));

        return command;
    }

    private static String createCPMCommandString() {
        return
            "{" +
                "\"id\":\"CreatePresentationModelCommand\"," +
                "\"p\":\"05ee43b7-a884-4d42-9fc5-00b083664eed\"," +
                "\"t\":\"com.canoo.icos.casemanager.model.casedetails.CaseInfoBean\"," +
                "\"a\":[" +
                    "{" +
                        "\"n\":\"@@@ SOURCE_SYSTEM @@@\"," +
                        "\"i\":\"3204S\"," +
                        "\"v\":\"server\"" +
                    "},{" +
                        "\"n\":\"caseDetailsLabel\"," +
                        "\"i\":\"3205S\"" +
                    "},{" +
                        "\"n\":\"caseIdLabel\"," +
                        "\"i\":\"3206S\"" +
                    "},{" +
                        "\"n\":\"statusLabel\"," +
                        "\"i\":\"3207S\"" +
                    "},{" +
                        "\"n\":\"status\"," +
                        "\"i\":\"3208S\"" +
                    "}" +
            "]}";
    }

    private static NamedCommand createNamedCommand() {
        return new NamedCommand("dolphin_platform_intern_registerController");
    }

    private static String createNamedCommandString() {
        return "{\"id\":\"dolphin_platform_intern_registerController\",\"className\":\"org.opendolphin.core.comm.NamedCommand\"}";
    }
}
