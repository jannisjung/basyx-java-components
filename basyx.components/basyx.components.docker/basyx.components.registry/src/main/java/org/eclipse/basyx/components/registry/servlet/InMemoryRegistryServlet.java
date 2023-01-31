/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.registry.servlet;

import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.registry.mqtt.MqttRegistryFactory;

/**
 * A registry servlet based on an InMemory Registry. The servlet therefore
 * provides an implementation for the IAASRegistryService interface without a
 * permanent storage capability.
 * 
 * Do not use this registry in a productive environment - the entries are not
 * persistent!
 * 
 * @author espen
 */
public class InMemoryRegistryServlet extends RegistryServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with ModelProvider based on an InMemoryRegistry
	 */
	public InMemoryRegistryServlet() {
		super(new InMemoryRegistry());
	}

	/**
	 * Constructor with ModelProvider based on an InMemoryRegistry with an MQTT
	 * event backend
	 */
	public InMemoryRegistryServlet(BaSyxMqttConfiguration mqttConfig) {
		super(new MqttRegistryFactory().create(new InMemoryRegistry(), mqttConfig));
	}
}
