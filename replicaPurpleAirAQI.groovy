/**
*  Original Driver Copyright 2022 bthrock
*  Modified to include both Number & Test Field capbilites in Mariano Colmenarejo's VirtualAppliancesMc Driver
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
@SuppressWarnings('unused')
public static String version() {return "1.3.0"}

metadata 
{
    definition(name: "replica PurpleAirAQI", namespace: "replica", author: "bbthrock", importUrl:"https://raw.githubusercontent.com/TheMegamind/Replica-Drivers/main/replicaPurpleAirAQI.groovy")
    {
        capability "Sensor"
        capability "Configuration"
        capability "Refresh"
        
        attribute "aqi", "number"
        attribute "category", "string"
        attribute "sites", "string"
        attribute "pollinginterval", "string"
        
        command "setPollingInterval", [[name: "value*", type: "String", description: "Set Polling Interval"]]
        
        attribute "healthStatus", "enum", ["offline", "online"]
    }
    preferences {   
	input(name:"deviceInfoDisable", type: "bool", title: "Disable Info logging:", defaultValue: false)
    }
}

def installed() {
	initialize()
}

def updated() {
	initialize()    
}

def initialize() {
    updateDataValue("triggers", groovy.json.JsonOutput.toJson(getReplicaTriggers()))
    updateDataValue("commands", groovy.json.JsonOutput.toJson(getReplicaCommands()))
}

def configure() {
    log.info "${device.displayName} configured default rules"
    initialize()
    updateDataValue("rules", getReplicaRules())
    sendCommand("configure")
}

// Methods documented here will show up in the Replica Command Configuration. These should be mostly setter in nature. 
static Map getReplicaCommands() {
    return ([ 
    	"setAqiValue":[[name:"aqi*",type:"NUMBER"]], 
	"setAqiCategoryValue":[[name:"category*",type:"string"]], 
	"setAqiSitesValue":[[name:"sites*",type:"string"]],
	"setPollingIntervalValue":[[name:"pollingInterval*",type:"string"]],
	    
	"setHealthStatusValue":[[name:"healthStatus*",type:"ENUM"]
   ]])
}

def setAqiValue(value) {
    String descriptionText = "${device.displayName} AQI is $value"
    sendEvent(name: "aqi", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

def setAqiCategoryValue(value) {
    String descriptionText = "${device.displayName} AQI Category is $value"
    sendEvent(name: "category", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

def setAqiSitesValue(value) {
    String descriptionText = "${device.displayName} AQI Sites are $value"
    sendEvent(name: "sites", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

def setPollingIntervalValue(value) {
    String descriptionText = "${device.displayName} AQI Polling Interval is $value"
    sendEvent(name: "pollingInterval", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

def setHealthStatusValue(value) {    
    sendEvent(name: "healthStatus", value: value, descriptionText: "${device.displayName} healthStatus set to $value")
}

// Methods documented here will show up in the Replica Trigger Configuration. These should be all of the native capability commands
Map getReplicaTriggers() {
    return ([ 
        "setPollingInterval":[[name:"value*",type:"STRING"]],  
	"refresh":[]])
}

private def sendCommand(String name, def value=null, String unit=null, data=[:]) {
    data.version=version()
    parent?.deviceTriggerHandler(device, [name:name, value:value, unit:unit, data:data, now:now()])
}

def setPollingInterval(value) {
    sendCommand("setInterval", value)    
}

void refresh() {
    sendCommand("refresh")
}

String getReplicaRules() {
    return """{"version":1,"components":[{
    
    "trigger":{"type":"attribute","properties":{"value":{"title":"HealthState","type":"string"}},"additionalProperties":false,"required":["value"],"capability":"healthCheck","attribute":"healthStatus","label":"attribute: healthStatus.*"},"command":{"name":"setHealthStatusValue","label":"command: setHealthStatusValue(healthStatus*)","type":"command","parameters":[{"name":"healthStatus*","type":"ENUM"}]},"type":"smartTrigger","mute":true},
    
    {"trigger":{"name":"setNumberFieldFive","label":"command: setNumberFieldFive(value*)","type":"command","parameters":[{"name":"value*","type":"NUMBER"}]},"command":{"name":"setNumberFieldFive","arguments":[{"name":"value","optional":false,"schema":{"type":"number"}}],"type":"command","capability":"legendabsolute60149.numberFieldFive","label":"command: setNumberFieldFive(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setNumberFieldFour","label":"command: setNumberFieldFour(value*)","type":"command","parameters":[{"name":"value*","type":"NUMBER"}]},"command":{"name":"setNumberFieldFour","arguments":[{"name":"value","optional":false,"schema":{"type":"number"}}],"type":"command","capability":"legendabsolute60149.numberFieldFour","label":"command: setNumberFieldFour(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setNumberFieldOne","label":"command: setNumberFieldOne(value*)","type":"command","parameters":[{"name":"value*","type":"NUMBER"}]},"command":{"name":"setNumberFieldOne","arguments":[{"name":"value","optional":false,"schema":{"type":"number"}}],"type":"command","capability":"legendabsolute60149.numberFieldOne","label":"command: setNumberFieldOne(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setNumberFieldThree","label":"command: setNumberFieldThree(value*)","type":"command","parameters":[{"name":"value*","type":"NUMBER"}]},"command":{"name":"setNumberFieldThree","arguments":[{"name":"value","optional":false,"schema":{"type":"number"}}],"type":"command","capability":"legendabsolute60149.numberFieldThree","label":"command: setNumberFieldThree(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setNumberFieldTwo","label":"command: setNumberFieldTwo(value*)","type":"command","parameters":[{"name":"value*","type":"NUMBER"}]},"command":{"name":"setNumberFieldTwo","arguments":[{"name":"value","optional":false,"schema":{"type":"number"}}],"type":"command","capability":"legendabsolute60149.numberFieldTwo","label":"command: setNumberFieldTwo(value*)"},"type":"hubitatTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"number"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.numberFieldOne","attribute":"numberFieldOne","label":"attribute: numberFieldOne.*"},"command":{"name":"setNumberFieldOneValue","label":"command: setNumberFieldOneValue(numberFieldOne*)","type":"command","parameters":[{"name":"numberFieldOne*","type":"NUMBER"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"number"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.numberFieldTwo","attribute":"numberFieldTwo","label":"attribute: numberFieldTwo.*"},"command":{"name":"setNumberFieldTwoValue","label":"command: setNumberFieldTwoValue(numberFieldTwo*)","type":"command","parameters":[{"name":"numberFieldTwo*","type":"NUMBER"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"number"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.numberFieldThree","attribute":"numberFieldThree","label":"attribute: numberFieldThree.*"},"command":{"name":"setNumberFieldThreeValue","label":"command: setNumberFieldThreeValue(numberFieldThree*)","type":"command","parameters":[{"name":"numberFieldThree*","type":"NUMBER"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"number"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.numberFieldFour","attribute":"numberFieldFour","label":"attribute: numberFieldFour.*"},"command":{"name":"setNumberFieldFourValue","label":"command: setNumberFieldFourValue(numberFieldFour*)","type":"command","parameters":[{"name":"numberFieldFour*","type":"NUMBER"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"number"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.numberFieldFive","attribute":"numberFieldFive","label":"attribute: numberFieldFive.*"},"command":{"name":"setNumberFieldFiveValue","label":"command: setNumberFieldFiveValue(numberFieldFive*)","type":"command","parameters":[{"name":"numberFieldFive*","type":"NUMBER"}]},"type":"smartTrigger"},
    
    {"trigger":{"name":"setTextFieldFive","label":"command: setTextFieldFive(value*)","type":"command","parameters":[{"name":"value*","type":"STRING"}]},"command":{"name":"setTextFieldFive","arguments":[{"name":"value","optional":false,"schema":{"type":"string"}}],"type":"command","capability":"legendabsolute60149.textFieldFive","label":"command: setTextFieldFive(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setTextFieldFour","label":"command: setTextFieldFour(value*)","type":"command","parameters":[{"name":"value*","type":"STRING"}]},"command":{"name":"setTextFieldFour","arguments":[{"name":"value","optional":false,"schema":{"type":"string"}}],"type":"command","capability":"legendabsolute60149.textFieldFour","label":"command: setTextFieldFour(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setTextFieldOne","label":"command: setTextFieldOne(value*)","type":"command","parameters":[{"name":"value*","type":"STRING"}]},"command":{"name":"setTextFieldOne","arguments":[{"name":"value","optional":false,"schema":{"type":"string"}}],"type":"command","capability":"legendabsolute60149.textFieldOne","label":"command: setTextFieldOne(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setTextFieldThree","label":"command: setTextFieldThree(value*)","type":"command","parameters":[{"name":"value*","type":"STRING"}]},"command":{"name":"setTextFieldThree","arguments":[{"name":"value","optional":false,"schema":{"type":"string"}}],"type":"command","capability":"legendabsolute60149.textFieldThree","label":"command: setTextFieldThree(value*)"},"type":"hubitatTrigger"},{"trigger":{"name":"setTextFieldTwo","label":"command: setTextFieldTwo(value*)","type":"command","parameters":[{"name":"value*","type":"STRING"}]},"command":{"name":"setTextFieldTwo","arguments":[{"name":"value","optional":false,"schema":{"type":"string"}}],"type":"command","capability":"legendabsolute60149.textFieldTwo","label":"command: setTextFieldTwo(value*)"},"type":"hubitatTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.textFieldOne","attribute":"textFieldOne","label":"attribute: textFieldOne.*"},"command":{"name":"setTextFieldOneValue","label":"command: setTextFieldOneValue(textFieldOne*)","type":"command","parameters":[{"name":"textFieldOne*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.textFieldTwo","attribute":"textFieldTwo","label":"attribute: textFieldTwo.*"},"command":{"name":"setTextFieldTwoValue","label":"command: setTextFieldTwoValue(textFieldTwo*)","type":"command","parameters":[{"name":"textFieldTwo*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.textFieldThree","attribute":"textFieldThree","label":"attribute: textFieldThree.*"},"command":{"name":"setTextFieldThreeValue","label":"command: setTextFieldThreeValue(textFieldThree*)","type":"command","parameters":[{"name":"textFieldThree*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.textFieldFour","attribute":"textFieldFour","label":"attribute: textFieldFour.*"},"command":{"name":"setTextFieldFourValue","label":"command: setTextFieldFourValue(textFieldFour*)","type":"command","parameters":[{"name":"textFieldFour*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"legendabsolute60149.textFieldFive","attribute":"textFieldFive","label":"attribute: textFieldFive.*"},"command":{"name":"setTextFieldFiveValue","label":"command: setTextFieldFiveValue(textFieldFive*)","type":"command","parameters":[{"name":"textFieldFive*","type":"STRING"}]},"type":"smartTrigger"}
    
    ]}"""
}

private logInfo(msg)  { if(settings?.deviceInfoDisable != true) { log.info  "${msg}" } }
private logDebug(msg) { if(settings?.deviceDebugEnable == true) { log.debug "${msg}" } }
private logTrace(msg) { if(settings?.deviceTraceEnable == true) { log.trace "${msg}" } }
private logWarn(msg)  { log.warn   "${msg}" }
private logError(msg) { log.error  "${msg}" }
