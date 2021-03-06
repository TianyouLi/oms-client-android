/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: Apache-2.0
 */
package oms.test.p2p.apitest;

import static oms.test.p2p.util.P2PAction.connect;
import static oms.test.p2p.util.P2PAction.createPeerClient;
import static oms.test.p2p.util.P2PAction.send;
import static oms.test.util.CommonAction.createDefaultCapturer;
import static oms.test.util.CommonAction.createLocalStream;
import static oms.test.util.Config.MESSAGE;
import static oms.test.util.Config.P2P_SERVER;
import static oms.test.util.Config.TIMEOUT;
import static oms.test.util.Config.USER1_NAME;
import static oms.test.util.Config.USER2_NAME;

import oms.p2p.Publication;
import oms.test.p2p.util.P2PClientObserver;
import oms.test.util.TestCallback;
import oms.test.util.TestObserver;

public class StabilityTest extends TestBase {

    public void testPublish_200Times() {
        user1 = createPeerClient(null);
        user2 = createPeerClient(null);
        user1.addAllowedRemotePeer(USER2_NAME);
        user2.addAllowedRemotePeer(USER1_NAME);
        connect(user1, USER1_NAME, P2P_SERVER, true);
        connect(user2, USER2_NAME, P2P_SERVER, true);
        capturer1 = createDefaultCapturer();
        localStream1 = createLocalStream(true, capturer1);
        for (int i = 0; i < 200; i++) {
            TestCallback<Publication> callback = new TestCallback<>();
            user1.publish(USER2_NAME, localStream1, callback);
            assertTrue(callback.getResult(true, TIMEOUT));
            TestObserver publicationObserver = new TestObserver();
            callback.successCallbackResult.addObserver(publicationObserver);
            callback.successCallbackResult.stop();
            assertTrue(publicationObserver.getResult(TIMEOUT));
        }
    }

    public void testSend_200Times() {
        observer1 = new P2PClientObserver(USER1_NAME);
        user1 = createPeerClient(observer1);
        observer2 = new P2PClientObserver(USER2_NAME);
        user2 = createPeerClient(observer2);
        user1.addAllowedRemotePeer(USER2_NAME);
        user2.addAllowedRemotePeer(USER1_NAME);
        connect(user1, USER1_NAME, P2P_SERVER, true);
        connect(user2, USER2_NAME, P2P_SERVER, true);
        for (int i = 0; i < 200; i++) {
            send(user1, MESSAGE, USER2_NAME, observer2, true);
            send(user2, MESSAGE, USER1_NAME, observer1, true);
        }
    }
}
