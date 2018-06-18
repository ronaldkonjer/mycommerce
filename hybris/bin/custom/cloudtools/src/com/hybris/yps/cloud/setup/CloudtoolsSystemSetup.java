/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.yps.cloud.setup;

import de.hybris.platform.core.initialization.SystemSetup;

import com.hybris.yps.cloud.constants.CloudtoolsConstants;


@SystemSetup(extension = CloudtoolsConstants.EXTENSIONNAME)
public class CloudtoolsSystemSetup
{

	public CloudtoolsSystemSetup()
	{
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
	}

}
