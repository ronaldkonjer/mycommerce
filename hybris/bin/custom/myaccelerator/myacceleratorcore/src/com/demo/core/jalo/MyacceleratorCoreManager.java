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
package com.demo.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.demo.core.constants.MyacceleratorCoreConstants;
import com.demo.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class MyacceleratorCoreManager extends GeneratedMyacceleratorCoreManager
{
	public static final MyacceleratorCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MyacceleratorCoreManager) em.getExtension(MyacceleratorCoreConstants.EXTENSIONNAME);
	}
}
