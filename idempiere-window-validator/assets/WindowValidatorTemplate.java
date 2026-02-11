/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - hengsin                         								   *
 **********************************************************************/
package ${package};

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidator;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.osgi.service.component.annotations.Component;

/**
 * Window Validator Template
 */
@Component(service = WindowValidator.class, immediate = true, property = {"AD_Window_UU=${window_uuid}", "events=${events}"})
public class ${classname} implements WindowValidator {

	/**
	 * Default Constructor
	 */
	public ${classname}() {
	}

	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback) {
		// Example check for specific event
		// if (WindowValidatorEventType.BEFORE_PROCESS.getName().equals(event.getName())) {
		//     ... logic ...
		// }
		
		callback.onCallback(Boolean.TRUE);
	}

}
