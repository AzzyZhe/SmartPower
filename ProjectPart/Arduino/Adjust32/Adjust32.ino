//由于验收前终版测试中单片机引脚损坏重新飞线
//此版代码测量引脚（PIN_VOLT_M）会与PCB走线不匹配
#include <SoftwareSerial.h>

#define PIN_CS 2
#define PIN_UD 3
#define PIN_INC 4

#define PIN_VOLT_M A7

//BT_RX <-> 模块TX
#define BT_TX 11
#define BT_RX 12
SoftwareSerial BT_Serial(BT_RX,BT_TX);
#define Serial_Control BT_Serial
//#define Serial_Control Serial

void setup() {
  Serial.begin(9600);
  Serial_Control.begin(9600);
  Serial.println("ArduinoSerial begin!");

  pinMode(PIN_CS,OUTPUT);
  pinMode(PIN_UD,OUTPUT);
  pinMode(PIN_INC,OUTPUT);
  
  pinMode(PIN_VOLT_M,INPUT);

  digitalWrite(PIN_INC, HIGH);

  digitalWrite(PIN_UD, HIGH);
  
//  BT.begin(9600);
}


int goal=3.0/5.0*1024,eps=0.10/5.0*1024;
int ptr=0;
int mea=0;

int tmp;
int cnt;
float tmpf;
char buf[101];
void loop() {
  while(Serial_Control.available()&&ptr<100)
    buf[ptr++]=Serial_Control.read();
  buf[ptr]=0;
  if(ptr)
  {
    Serial.println("ControlRecv");
//    tmpf=atof(buf);
//    tmp=tmpf/5.0*1024;
    int st=0,ed=0;
    while(st<ptr&&buf[st]!='(')
      st++;
    ed=st+1;
    while(ed<ptr&&buf[ed]!=')')
      ed++;
    if(ed<ptr)
    {
      buf[ed]=0;
      Serial.println(buf+st+1);
      
      tmp=atoi(buf+st+1);
      Serial.print("goalRecv:");
      Serial.println(tmp);
  //    Serial.println(tmpf);
      Serial.println(5.0/1024*tmp);
      
      if(tmp<0)
        tmp=0;
      if(tmp>1023)
        tmp=1023;
      goal=tmp;
      Serial.print("goalSet:");
      Serial.println(goal);
    }
    ptr=0;
//    delay(200);

  }

  mea=analogRead(PIN_VOLT_M);
  cnt=0;
  while(abs(mea-goal)>eps&&cnt<20)//if
  {
    digitalWrite(PIN_UD, (mea<goal)?HIGH:LOW);
    
    digitalWrite(PIN_CS, LOW);
    
    digitalWrite(PIN_INC, LOW);
    delay(20);
    digitalWrite(PIN_INC, HIGH);
    
    digitalWrite(PIN_CS, HIGH);
    Serial.print((mea<goal)?'+':'-');
    mea=analogRead(PIN_VOLT_M);
    Serial.println(mea);
    delay(20);
    cnt++;
  }
  
  Serial_Control.print('(');
  Serial_Control.print(mea);
  Serial_Control.print(')');
  
  Serial.print('\t');
  Serial.println(5.0/1024*mea);
//  BT.print(5.0/1024*mea);
  delay(100);
}
