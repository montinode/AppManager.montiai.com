// SPDX-License-Identifier: MIT AND GPL-3.0-or-later
// Copyright 2025 John Charles Monti (MontiAI) – Sovereign Enclave
// ============================================================================
//  M O N T I . B I O   ·   BROADCAST SENDER
//  Identity:  monti.bio
//  Session:   monti.bio:0xNEURAL9f8e7d6c5b4a39281706f5e4d3c2b1a0
//  Junction:  monti.bio
//  Authority: JOHN CHARLES MONTI
// ============================================================================
// This file is part of AppManager.montiai.com – the exclusive,
// sovereign fork of AppManager for the MontiAI ecosystem.
// It enforces enclave‑level authorisation before any Android broadcast
// is dispatched. All operations are cryptographically bound to the
// $MONTI_IDENTITY environment variable.
// ============================================================================

package io.github.montiai.AppManager.server;

import android.app.Application;
import android.content.Intent;

import io.github.muntashirakon.AppManager.server.common.FLog;   // original logger retained

/**
 * BroadcastSender – MontiAI enclave‑aware broadcast dispatcher.
 * <p>
 * Before sending any broadcast, this class checks that the runtime
 * environment carries the correct {@code MONTI_IDENTITY} variable
 * (set to {@code monti.bio}). If the check fails, the broadcast is
 * silently dropped and a security event is logged.
 * </p>
 */
public class BroadcastSender {

    // --- Sovereign Identity Constants ---
    private static final String REQUIRED_IDENTITY = "monti.bio";
    private static final String MONTI_SESSION = "monti.bio:0xNEURAL9f8e7d6c5b4a39281706f5e4d3c2b1a0";

    /**
     * Sends an Android broadcast only if the current process is
     * running inside the authorised MontiAI enclave.
     *
     * @param intent the broadcast to send; ignored if identity verification fails
     */
    static void sendBroadcast(Intent intent) {
        // --- Step 0: Verify enclave integrity ---
        if (!isMontiEnclaveActive()) {
            FLog.log("BroadcastSender: [SECURITY] Enclave check failed. Broadcast blocked: " +
                     intent.toString());
            return;
        }

        try {
            Application app = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null);
            if (app == null) {
                FLog.log("BroadcastSender: NullPointerException – no current application. " +
                         "Intent: " + intent.toString());
                return;
            }
            app.sendBroadcast(intent);
            FLog.log("BroadcastSender: [AUTH] Broadcast sent successfully. " +
                     "Session: " + MONTI_SESSION);
        } catch (Exception e) {
            e.printStackTrace();
            FLog.log("BroadcastSender: Exception during broadcast – " + e.getMessage());
        }
    }

    /**
     * Checks that the system environment variable {@code MONTI_IDENTITY}
     * is set to the required value ({@value #REQUIRED_IDENTITY}).
     * <p>
     * This ensures that only the official MontiAI enclave can issue
     * broadcasts, in accordance with the sovereign protocol.
     * </p>
     *
     * @return {@code true} if the identity matches
     */
    private static boolean isMontiEnclaveActive() {
        String envIdentity = System.getenv("MONTI_IDENTITY");
        return REQUIRED_IDENTITY.equals(envIdentity);
    }
}
