/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: RegisterAuthModule.java,v 1.2 2008/06/25 05:42:12 qcheng Exp $
 *
 */

package com.sun.identity.cli.authentication;

import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IOutput;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.cli.schema.SchemaCommand;
import com.iplanet.sso.SSOException;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Adds custom authentication modules.
 */
public class RegisterAuthModule extends SchemaCommand {
    static final String AUTH_MODULE = "authmodule";
    static final String AUTH_AUTHENTICATOR_ATTR =
        "iplanet-am-auth-authenticators";

    /**
     * Services a Commandline Request.
     *
     * @param rc Request Context.
     * @throws CLIException if the request cannot serviced.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();

        String authModule = getStringOptionValue(AUTH_MODULE);
        ServiceSchema ss = getServiceSchema(ISAuthConstants.AUTH_SERVICE_NAME,
            null, "Global");
        IOutput outputWriter = getOutputWriter();

        try {
            String[] params = {ISAuthConstants.AUTH_SERVICE_NAME};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "ATTEMPT_REGISTER_AUTH_MODULE", params);
            Map attrValues = ss.getAttributeDefaults();

            Set values = (Set)attrValues.get(AUTH_AUTHENTICATOR_ATTR);
            if ((values == null) || values.isEmpty()) {
                values = new HashSet(2);
            }
            values.add(authModule);
            ss.setAttributeDefaults(AUTH_AUTHENTICATOR_ATTR, values);
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "SUCCEED_REGISTER_AUTH_MODULE", params);
            outputWriter.printlnMessage(
                getResourceString("register-auth-module-succeeded"));
        } catch (SSOException e) {
            String[] args = {ISAuthConstants.AUTH_SERVICE_NAME, 
                e.getMessage()};
            debugError("RegisterAuthModule.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_REGISTER_AUTH_MODULE", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SMSException e) {
            String[] args = {ISAuthConstants.AUTH_SERVICE_NAME, 
                e.getMessage()};
            debugError("RegisterAuthModule.handleRequest", e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_REGISTER_AUTH_MODULE", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}
