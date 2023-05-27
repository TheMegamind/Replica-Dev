/**
*  Copyright 2022 bthrock
*
*  The driver is for use with Todd Austin's PurpleAir AQI driver, the primary advantage of which is the ability
*  to change the update interval on the fly in response to serious and rapidly changing air quality conditions. 
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
        
        attribute "aqi", "number"   		// Current AQI
        attribute "category", "string"		// Description of Current Air Quality
        attribute "sites", "string"		// List of Sensor Sites used 
        attribute "updateInterval", "string"    // Interval Between Updates
        
        command "setInterval", [[name: "interval*", type: "ENUM", description: "Set Interval Between Updates", constraints: ["1min","5min","10min","15min","30min","60min","180min"]
]] // Override Interval Between Updates
        
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
    refresh()
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
	"setCategoryValue":[[name:"category*",type:"STRING"]], 
	"setSitesValue":[[name:"sites*",type:"STRING"]],
	"setIntervalValue":[[name:"interval*",type:"STRING"]],
	    
	"setHealthStatusValue":[[name:"healthStatus*",type:"ENUM"]
   ]])
}

def setAqiValue(value) {
    String descriptionText = "${device.displayName} is $value"
    sendEvent(name: "aqi", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

def setCategoryValue(value) {
    String descriptionText = "${device.displayName} AQI Category is $value"
    sendEvent(name: "category", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

// ST driver provides an HTML Table; Convert to JSON
def setSitesValue(value) {    
    def rows = value.findAll(/<tr>(.*?)<\/tr>/)  	// Extract table rows
    def tableData = [] 					// Define a list to store table data
    // Iterate over rows and extract table data
	rows.each { row ->
	    // Extract cell value from the row
	    def cell = row.replaceFirst(/<tr><td>(.*?)<\/td><\/tr>/, '$1')
	    tableData.add(cell)
	}
    value = new groovy.json.JsonBuilder(tableData).toPrettyString()  // Convert table data to JSON
    String descriptionText = "${device.displayName} Sensor Sites are $value"
    sendEvent(name: "sites", value: value, descriptionText: descriptionText)
    log.info descriptionText
}

// The setInterval command in the ST driver overrides the preference setting and will
// report a value ONLY when the command is sent. The last command setting may not be 
// accurate if the preference setting has subsequently been changed, thereby overriding 
// the command value. If no value is provide, the status is set to "Not Reported"
def setIntervalValue(value) {    
    if (value != " ") {
        String descriptionText = "${device.displayName} Update Interval is $value"
        sendEvent(name: "interval", value: value, descriptionText: descriptionText)
        log.info descriptionText
    } else {
        String descriptionText = "${device.displayName} Update Interval Not Reported"
        sendEvent(name: "interval", value: "Not Reported", descriptionText: descriptionText)
        log.info descriptionText
    }  
}

def setHealthStatusValue(value) {    
    sendEvent(name: "healthStatus", value: value, descriptionText: "${device.displayName} healthStatus set to $value")
}

// Methods documented here will show up in the Replica Trigger Configuration. These should be all of the native capability commands
static Map getReplicaTriggers() {
    return ([ 
        "setInterval":[[name:"interval*",type:"STRING"]],  
	"refresh":[]])
}

private def sendCommand(String name, def value=null, String unit=null, data=[:]) {
    data.version=version()
    parent?.deviceTriggerHandler(device, [name:name, value:value, unit:unit, data:data, now:now()])
}

def setInterval(interval) {
    sendCommand("setInterval", interval)    
}

void refresh() {
    sendCommand("refresh")
}

static String getReplicaRules() {
    return """{"version":1,"components":[{"trigger":{"name":"setInterval","label":"command: setInterval(interval*)","type":"command","parameters":[{"name":"interval*","type":"STRING"}]},"command":{"name":"setInterval","arguments":[{"name":"interval","optional":false,"schema":{"type":"string","maxLength":20}}],"type":"command","capability":"partyvoice23922.aqisetinterval","label":"command: setInterval(interval*)"},"type":"hubitatTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"partyvoice23922.aqisites","attribute":"sites","label":"attribute: sites.*"},"command":{"name":"setSitesValue","label":"command: setSitesValue(sites*)","type":"command","parameters":[{"name":"sites*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"integer","minimum":0,"maximum":1000},"unit":{"type":"string","enum":["AQI"],"default":"AQI"}},"additionalProperties":false,"required":["value"],"capability":"partyvoice23922.purpleaqi","attribute":"aqi","label":"attribute: aqi.*"},"command":{"name":"setAqiValue","label":"command: setAqiValue(aqi*)","type":"command","parameters":[{"name":"aqi*","type":"NUMBER"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string"}},"additionalProperties":false,"required":["value"],"capability":"partyvoice23922.aqicategory","attribute":"category","label":"attribute: category.*"},"command":{"name":"setCategoryValue","label":"command: setCategoryValue(category*)","type":"command","parameters":[{"name":"category*","type":"STRING"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"title":"HealthState","type":"string"}},"additionalProperties":false,"required":["value"],"capability":"healthCheck","attribute":"healthStatus","label":"attribute: healthStatus.*"},"command":{"name":"setHealthStatusValue","label":"command: setHealthStatusValue(healthStatus*)","type":"command","parameters":[{"name":"healthStatus*","type":"ENUM"}]},"type":"smartTrigger"},{"trigger":{"type":"attribute","properties":{"value":{"type":"string","maxLength":20}},"additionalProperties":false,"required":["value"],"capability":"partyvoice23922.aqisetinterval","attribute":"interval","label":"attribute: interval.*"},"command":{"name":"setIntervalValue","label":"command: setIntervalValue(interval*)","type":"command","parameters":[{"name":"interval*","type":"NUMBER"}]},"type":"smartTrigger"}]}"""
}

private logInfo(msg)  { if(settings?.deviceInfoDisable != true) { log.info  "${msg}" } }
private logDebug(msg) { if(settings?.deviceDebugEnable == true) { log.debug "${msg}" } }
private logTrace(msg) { if(settings?.deviceTraceEnable == true) { log.trace "${msg}" } }
private logWarn(msg)  { log.warn   "${msg}" }
private logError(msg) { log.error  "${msg}" }
