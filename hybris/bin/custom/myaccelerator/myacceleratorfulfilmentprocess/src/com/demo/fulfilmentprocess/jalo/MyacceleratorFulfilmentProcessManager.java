/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.demo.fulfilmentprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.demo.fulfilmentprocess.constants.MyacceleratorFulfilmentProcessConstants;

public class MyacceleratorFulfilmentProcessManager extends GeneratedMyacceleratorFulfilmentProcessManager
{
	public static final MyacceleratorFulfilmentProcessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MyacceleratorFulfilmentProcessManager) em.getExtension(MyacceleratorFulfilmentProcessConstants.EXTENSIONNAME);
	}
	
}
