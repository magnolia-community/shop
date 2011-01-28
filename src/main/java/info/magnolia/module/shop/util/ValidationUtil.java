/**
 * This file Copyright (c) 2003-2009 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.shop.util;

import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.StringUtils;

/**
 * Util.
 * @author will
 */
public class ValidationUtil {

    public static final String VALIDATION_FAILED_EMPTY = "validation.failed.empty";
    public static final String VALIDATION_FAILED_INVALID = "validation.failed.invalid";
    public static final String VALIDATION_FAILED_INVALID_UNDER_LIMIT = "validation.failed.invalid.under.limit";
    public static final String VALIDATION_FAILED_INVALID_OVER_LIMIT = "validation.failed.invalid.over.limit";

    public static boolean validateNotEmpty(String key, String value, Map errors) {
        if (errors != null && StringUtils.isNotBlank(key)) {
            if (StringUtils.isBlank(value)) {
                errors.put(key, VALIDATION_FAILED_EMPTY);
                return false;
            }
        }
        return true;
    }

    public static boolean validateNotEmpty(String key, Number value, Map errors) {
        if (errors != null && StringUtils.isNotBlank(key)) {
            if (value == null) {
                errors.put(key, VALIDATION_FAILED_EMPTY);
                return false;
            }
        }
        return true;
    }

    public static boolean validateBoolean(String key, Boolean value, Boolean requiredValue, Map errors) {
        if (errors != null && StringUtils.isNotBlank(key) && requiredValue != null) {
            if (value == null || value.booleanValue() != requiredValue.booleanValue()) {
                errors.put(key, VALIDATION_FAILED_INVALID);
                return false;
            }
        }
        return true;
    }

    public static boolean validateNumberBiggerThanLimit(String key, Number value, Number limit, Map errors) {
        if (errors != null && StringUtils.isNotBlank(key) && limit != null) {
            if (value == null) {
                errors.put(key, VALIDATION_FAILED_EMPTY);
                return false;
            } else if (value.doubleValue() <= limit.doubleValue()) {
                errors.put(key, VALIDATION_FAILED_INVALID_UNDER_LIMIT);
                return false;
            }
        }
        return true;
    }

    public static boolean validateNumberSmallerThanLimit(String key, Number value, Number limit, Map errors) {
        if (errors != null && StringUtils.isNotBlank(key) && limit != null) {
            if (value == null) {
                errors.put(key, VALIDATION_FAILED_EMPTY);
                return false;
            } else if (value.doubleValue() >= limit.doubleValue()) {
                errors.put(key, VALIDATION_FAILED_INVALID_OVER_LIMIT);
                return false;
            }
        }
        return true;
    }

    public static boolean validateEmailAddress(String key, String emailAddress, Map errors) {
        if (emailAddress == null) {
            errors.put(key, VALIDATION_FAILED_EMPTY);
            return false;
        }
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(emailAddress);
            if (!hasNameAndDomain(emailAddress)) {
                result = false;
            }
        } catch (AddressException ex) {
            result = false;
        }
        if (!result) {
            errors.put(key, VALIDATION_FAILED_INVALID);
        }
        return result;
    }

    private static boolean hasNameAndDomain(String aEmailAddress) {
        String[] tokens = aEmailAddress.split("@");
        if (tokens.length == 2 && StringUtils.isNotBlank(tokens[0]) && StringUtils.isNotBlank(tokens[1])) {
            String regex = "\\" + ".";
            String[] domainTokens = tokens[1].split(regex);
            return domainTokens.length > 1 && StringUtils.isNotBlank(domainTokens[0]) && StringUtils.isNotBlank(domainTokens[1]);
        } else {
            return false;
        }
    }
}
