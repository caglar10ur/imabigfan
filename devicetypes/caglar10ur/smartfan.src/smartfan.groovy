/**
 *  SmartFan
 *
 *  Copyright 2017 S.Çağlar Onur
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "SmartFan", namespace: "caglar10ur", author: "S.Çağlar Onur") {
		capability "Switch"
        attribute "triggerswitch", "string"
        command "Toggle"
	}

	preferences {
		input("ip", "string", title:"Device IP Address", description: "Please enter your device's IP Address", required: true, displayDuringSetup: true)
		input("port", "string", title:"Device Port", description: "Please enter port 80 or your device's Port", required: true, displayDuringSetup: true)
		input("path", "string", title:"URL Path", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		standardTile("Toggle", "device.triggerswitch", width: 2, height: 2, canChangeIcon: true, canChangeBackground: true) {
			state "triggeroff", label:'OFF' , action: "on", icon: "st.Appliances.appliances11", backgroundColor:"#ffffff", nextState: "trying"
			state "triggeron", label: 'ON', action: "off", icon: "st.Appliances.appliances11", backgroundColor: "#79b821", nextState: "trying"
			state "trying", label: 'TRYING', action: "", icon: "st.Appliances.appliances11", backgroundColor: "#FFAA33"
		}
        main "Toggle"
		details(["Toggle"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def toggle() {
	def headers = [:]
	headers.put("HOST", "${ip}:${port}")

	def result = new physicalgraph.device.HubAction(
		method: "GET",
		path: path,
		headers: headers,
	)

	try {
		sendHubCommand(result)
	} catch (Exception e) {
		log.debug "Hit Exception $e on $result"
        return false
	}
    return true
}

// handle commands
def on() {
	log.debug "Executing 'on'"

	if (toggle()) {
		sendEvent(name: "triggerswitch", value: "triggeron", isStateChange: true)
	}
}

def off() {
	log.debug "Executing 'off'"

	if (toggle()) {
		sendEvent(name: "triggerswitch", value: "triggeroff", isStateChange: true)
	}
}
