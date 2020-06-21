  /*
 * HTTP Client GET Request
 * Copyright (c) 2018, circuits4you.com
 * All rights reserved.
 * https://circuits4you.com 
 * Connects to WiFi HotSpot. */

//internet con
#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>
#define INPUT_SIZE 7
/* Set these to your desired credentials. */
const char *ssid = "blablabla";  //ENTER YOUR WIFI SETTINGS
const char *password = "cacajelek1!";

//Web/Server address to read/write from 
const String host = "192.168.1.100";   //https://circuits4you.com website or IP address of server
const int buzzer = D4;
const int in0 = D11;
const int in1 = D9;
const int in2 = D12;
const int in3 = D13;
const String deviceid = "1";
const String URL_ADDRESS = "http://"+host+"/Hotel/Service/getStsArd?devid="+deviceid;

// Get next command from Serial (add 1 for final 0)
char input[INPUT_SIZE + 1];
byte size = Serial.readBytes(input, INPUT_SIZE);
// Add the final 0 to end the C string
int pintu, lampu, kipas, lift;
int currpintu, currlampu, currkipas, currlift;
bool change;
char *token;
const char s[2] = ",";

//=======================================================================
//                    Power on setup
//=======================================================================
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  connectWiFi();
  pinMode(in0, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  digitalWrite(in0, HIGH);
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  digitalWrite(in3, HIGH);
  pintu = 0;
  lampu = 0;
  kipas = 0;
  lift = 0;
  currpintu = 0;
  currlampu = 0;
  currkipas = 0;
  currlift = 0;
}

// ----------------- Function Loop
void loop() {
        change = false;
        memset ( input, 0, INPUT_SIZE );
        getData();
        if(input[0] != '\0'){
          packData();
        }
//        Serial.println(input);
        cekPintu();
        cekLampu();
        cekKipas();
        cekLift();
        printRoom();
        delay(200);
}



// ----------------- Function Connect to WiFi and MySQL
void connectWiFi(){
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //This line hides the viewing of ESP as wifi hotspot
  
  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
}

// ----------------- Function Getting Data From Web Server
void getData(){
        WiFiClient client;
        HTTPClient https;
        
//        Serial.print("[HTTPS] begin...\n");
        if (https.begin(client, URL_ADDRESS )) { // HTTPS
//          Serial.print("[HTTPS] GET...\n");
          int httpCode = https.GET();
          if (httpCode > 0) {
//            Serial.printf("[HTTPS] GET... code: %d\n", httpCode);
            if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
              String payload = https.getString();
              payload.toCharArray(input, sizeof(input));
//              Serial.println(payload);
            }
          } else {
          Serial.printf("[HTTPS] GET... failed, error: %s\n", https.errorToString(httpCode).c_str());
          }
          https.end();
        } else {
          Serial.printf("[HTTPS] Unable to connect\n");
        }
}

// ----------------- Function Pack Data From Web Server
void packData(){
        token = strtok(input, ",");
        pintu = atoi(token);
        token= strtok(0, ",");
        lampu = atoi(token);
        token = strtok(0, ",");
        kipas = atoi(token);
        token = strtok(0, ",");
        lift = atoi(token);
}

// ----------------- Function Open or Close Door
void cekPintu(){
        if(currpintu != pintu){
          if(pintu==1){
            digitalWrite(in3, LOW);
          }else if(pintu==0){
            digitalWrite(in3, HIGH);
          }
          currpintu = pintu;
          change = true;
        }
}

// ----------------- Function Turn On and Off Lamp
void cekLampu(){
      if(currlampu != lampu){
        if(lampu==1){
          digitalWrite(in0, LOW);
        }else if(lampu==0){
          digitalWrite(in0, HIGH);
        }
        currlampu = lampu;
        change = true;
      }
}

// ----------------- Function Turn On and Off Fan
void cekKipas(){
        if(currkipas != kipas){
          if(kipas==1){
            digitalWrite(in2, LOW);
          }else if(kipas==0){
            digitalWrite(in2, HIGH);
          }
          currkipas = kipas;
          change = true;
        }
}

// ----------------- Function Turn On and Off Fan
void cekLift(){
        if(currlift != lift){
          if(lift==1){
              digitalWrite(in1, HIGH);
          }else if(lift==0){
              digitalWrite(in1, LOW);
          }
          currlift = lift;
          change = true;
        }
}

void printRoom(){
    if(change){
      Serial.println("");
      Serial.println("===========================================================");
      Serial.println("=                   Kondisi Kamar                         =");
      Serial.println("===========================================================");
      //pintu
      Serial.print("Pintu: ");
      if(currpintu==1){Serial.println("BUKA");}
      else if(currpintu==0){Serial.println("TUTUP");}
      
      //lampu
      Serial.print("Lampu: ");
      if(currlampu==1){Serial.println("HIDUP");}
      else if(currlampu==0){Serial.println("MATI");}
      
      //kipas
      Serial.print("Kipas: ");
      if(currkipas==1){Serial.println("HIDUP");}
      else if(currkipas==0){Serial.println("MATI");}
      
      //lift
      Serial.print("Lift: ");
      if(currlift==1){Serial.println("HIDUP");}
      else if(currlift==0){Serial.println("MATI");}
    }
}
