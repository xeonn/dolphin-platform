package org.opendolphin.core.client;

/**
 * Created by hendrikebbers on 20.12.16.
 */
public class ApplyToAble {

    private ClientDolphin dolphin;

    private ClientPresentationModel source;

    public ApplyToAble(ClientDolphin dolphin, ClientPresentationModel source) {
        this.dolphin = dolphin;
        this.source = source;
    }

    void to(ClientPresentationModel target) {
        target.syncWith(source);
        // at this point, all notifications about value and meta-inf changes
        // have been sent and that way the server is synchronized
    }

    public ClientDolphin getDolphin() {
        return dolphin;
    }

    public void setDolphin(ClientDolphin dolphin) {
        this.dolphin = dolphin;
    }

    public ClientPresentationModel getSource() {
        return source;
    }

    public void setSource(ClientPresentationModel source) {
        this.source = source;
    }
}
