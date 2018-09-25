#include <ArduinoJson.h>
#include <ESP8266WiFi.h>     
#include <String.h>
WiFiClient client;

char ssid[30] = "init";//默认热点名称，可修改为自己的路由器用户名
char password[30] = "12345678";///默认热点密码，可修改为自己的路由器密码
char onenettcp[20] = "192.168.43.1";//热点默认ip
int  onenetport = 1811;//app默认端口号
String null = "";
int current_Inf=0;
int next_Inf=0;
char buf[30];
String line = "";
char s1;
char s2;

int lastTime;
int timer = 0;
int current_timer = 0;
int i = 0;
int flag_bz = 0;
int SR_2;    //右边红外避障传感器状态
int SL_2;    //左边红外避障传感器状态

void connectWifi() {
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED){
    delay(1000);
    Serial.print(".");   
  }
  Serial.println("\rWifi Connected\r");
}

void c_send(char *s) {
     client.write((const uint8_t*)s, sizeof(s));
}

void LoupInit() {
  while (!client.connected())
  {
    Serial.println("Reconnect TCP Server...");
    if (!client.connect(onenettcp, onenetport)){
      if (WiFi.status() != WL_CONNECTED){
        Serial.println("Reconnect WIFI ...");
        connectWifi();
      }
      delay(100);
    } else {
      Serial.println("Reconnect Falied");
    }
  }
}

void Init() {
  connectWifi();
  if (client.connect(onenettcp, onenetport)) {
          Serial.println("Client Connected\r"); 
  }
}

void jieshou() {
    if(client.available()){
      null.toCharArray(buf,30);
      while (client.available()) {
        buf[i] = client.read();
        Serial.print(buf[i],HEX);
        Serial.print(" ");
        if(buf[i] == 0x2a&&buf[1] == i-1){//&&buf[1] == i-2&&buf[0] == 0xaa
          break;
          }
        i++;
        
      }
    Serial.println("");
    }
}

void bizhang(){
    if(flag_bz == 1)//切换到壁障模式
  {
      //有信号为LOW  没有信号为HIGH
    SR_2 = digitalRead(13);
    SL_2 = digitalRead(12);
    if (SL_2 == HIGH&&SR_2==HIGH)
      {
          digitalWrite(16,LOW);  
          digitalWrite(5,HIGH);     
          digitalWrite(4,HIGH);  
          digitalWrite(14,LOW);
      }
    else if (SL_2 == HIGH & SR_2 == LOW){// 右边探测到有障碍物，有信号返回，向左转 
          digitalWrite(16,HIGH);  
          digitalWrite(5,LOW);     
          digitalWrite(4,HIGH);  
          digitalWrite(14,LOW);
      }
    else if (SR_2 == HIGH & SL_2 == LOW){ //左边探测到有障碍物，有信号返回，向右转  
          digitalWrite(16,LOW);  
          digitalWrite(5,HIGH);     
          digitalWrite(4,LOW);  
          digitalWrite(14,HIGH);
  }
    else // 都是有障碍物, 后退
    {
        digitalWrite(16,LOW);  
        digitalWrite(5,LOW);     
        digitalWrite(4,LOW);  
        digitalWrite(14,LOW);//停止
        delay(300);
        digitalWrite(16,HIGH);  
        digitalWrite(5,LOW);     
        digitalWrite(4,LOW);  
        digitalWrite(14,HIGH);//后退500MS
        delay(400);
        digitalWrite(16,HIGH);  
        digitalWrite(5,LOW);     
        digitalWrite(4,HIGH);  
        digitalWrite(14,LOW);//左转
        delay(500); 
    }
  
  }
  
}

void setup() {
  Serial.begin(9600);
  pinMode(16,  OUTPUT);
  pinMode(5,  OUTPUT);
  pinMode(4,  OUTPUT);
  pinMode(14,  OUTPUT);
  
  pinMode(13,  INPUT);
  pinMode(12,  INPUT);

  digitalWrite(16,LOW);  
  digitalWrite(5,LOW);     
  digitalWrite(4,LOW);  
  digitalWrite(14,LOW);
  
}

void loop() { 
  Init();
  while(1){
    LoupInit();
    jieshou();
    bizhang();
      
      if(buf[i] == 0x2a&&buf[1] == i-1&&buf[0] == 0xaa){
        switch(buf[2]){  
        case 0x02:
          switch(buf[3]){
            case 1:
              digitalWrite(16,LOW);  
              digitalWrite(5,HIGH);     
              digitalWrite(4,HIGH);  
              digitalWrite(14,LOW);
              flag_bz = 1;
              break;
              
            case 2:
              digitalWrite(16,HIGH);  
              digitalWrite(5,LOW);     
              digitalWrite(4,HIGH);  
              digitalWrite(14,LOW);
              flag_bz = 0;
              break;
            case 3:
              digitalWrite(16,LOW);  
              digitalWrite(5,HIGH);     
              digitalWrite(4,LOW);  
              digitalWrite(14,HIGH);
              flag_bz = 0;
              break;
              
            case 4:
              digitalWrite(16,HIGH);  
              digitalWrite(5,LOW);     
              digitalWrite(4,LOW);  
              digitalWrite(14,HIGH);
              flag_bz = 0;
              break;
              
            case 5:
              digitalWrite(16,LOW);  
              digitalWrite(5,LOW);     
              digitalWrite(4,LOW);  
              digitalWrite(14,LOW);
              flag_bz = 0;
            break;
            
          }
          break;
        }
      }
      i = 0;
 
  }
  
}
