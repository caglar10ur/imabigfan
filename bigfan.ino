#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <IRsend.h>

const char* ssid = "<omitted>";
const char* password = "<omitted>";

ESP8266WebServer server(80);
MDNSResponder mdns;
// D5
IRsend irsend(14);

void setup(void) {
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, HIGH);

    irsend.begin();

    Serial.begin(115200);

    WiFi.disconnect();
    WiFi.begin(ssid, password);

    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);

    // Wait for connection
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    // Dump the connection info
    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(ssid);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    // Start the MDNS
    if (mdns.begin("esp8266")) {
        Serial.println("MDNS responder started");
    }

    // Add service to the MDNS
    mdns.addService("http", "tcp", 80);

    // Simple handler to toggle fan on/off
    server.on("/toggle", []() {
            digitalWrite(LED_BUILTIN, LOW);

            uint16_t  rawData[95] = {1250, 500, 1250, 500, 400, 1350, 1250, 500, 1250, 500, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 1250, 7200, 1250, 500, 1250, 500, 400, 1350, 1250, 500, 1250, 500, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 1250, 7200, 1250, 500, 1250, 500, 400, 1350, 1250, 500, 1250, 500, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 1250, 7200, 1250, 500, 1250, 500, 400, 1350, 1250, 500, 1250, 500, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 400, 1350, 1250}; // UNKNOWN 99F428D0
            irsend.sendRaw(rawData, 95, 38);

            server.send(200, "text/plain", "I'm a big fan...\n");

            digitalWrite(LED_BUILTIN, HIGH);
    });

    server.onNotFound([]() {
            server.send(404, "text/plain", "Not found\n");
    });

    server.begin();
    Serial.println("HTTP server started");
}

void loop(void) {
    server.handleClient();
}
