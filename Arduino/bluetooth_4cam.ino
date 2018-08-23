
#include <Wire.h>
#include <ArduCAM.h>
#include <SPI.h>
#include "memorysaver.h"

#define   FRAMES_NUM    0x00

// set pin 4,5,6,7 as the slave select for SPI:
const int CS1 = 4;
const int CS2 = 5;
const int CS3 = 6;
const int CS4 = 7;

//the falgs of camera modules
bool cam1 = true, cam2 = true, cam3 = true, cam4 = true;
//the flag of JEPG data header
bool is_header;
//the falg data of 4 cameras' data
byte flag[5] = {0xFF, 0xAA, 0x01, 0xFF, 0x55};

ArduCAM myCAM1(OV5642, CS1);
ArduCAM myCAM2(OV5642, CS2);
ArduCAM myCAM3(OV5642, CS3);
ArduCAM myCAM4(OV5642, CS4);

void setup() {
  uint8_t vid, pid;
  uint8_t temp;

  Wire.begin();
  Serial.begin(19200);
  Serial.println("check");
  Serial1.begin(19200);
  Serial.println("bluetooth check");

  // set the CS output:
  pinMode(CS1, OUTPUT);
  digitalWrite(CS1, HIGH);
  pinMode(CS2, OUTPUT);
  digitalWrite(CS2, HIGH);
  pinMode(CS3, OUTPUT);
  digitalWrite(CS3, HIGH);
  pinMode(CS4, OUTPUT);
  digitalWrite(CS4, HIGH);

  // initialize SPI:
  SPI.begin();

  //Reset the CPLD
  myCAM1.write_reg(0x07, 0x80);
  delay(100);
  myCAM1.write_reg(0x07, 0x00);
  delay(100);
  myCAM2.write_reg(0x07, 0x80);
  delay(100);
  myCAM2.write_reg(0x07, 0x00);
  delay(100);
  myCAM3.write_reg(0x07, 0x80);
  delay(100);
  myCAM3.write_reg(0x07, 0x00);
  delay(100);
  myCAM4.write_reg(0x07, 0x80);
  delay(100);
  myCAM4.write_reg(0x07, 0x00);
  delay(100);

  //Check if the 4 ArduCAM Mini 2MP Cameras' SPI bus is OK
  while (1) {
    myCAM1.write_reg(ARDUCHIP_TEST1, 0x55);
    temp = myCAM1.read_reg(ARDUCHIP_TEST1);
    if (temp != 0x55)
    {
      Serial.println("SPI1 interface Error!");
      cam1 = false;
      break;
      //while(1);
    }
    myCAM2.write_reg(ARDUCHIP_TEST1, 0x55);
    temp = myCAM2.read_reg(ARDUCHIP_TEST1);
    if (temp != 0x55)
    {
      Serial.println("SPI2 interface Error!");
      cam2 = false;
      break;
      //while(1);
    }
    myCAM3.write_reg(ARDUCHIP_TEST1, 0x55);
    temp = myCAM3.read_reg(ARDUCHIP_TEST1);
    if (temp != 0x55)
    {
      Serial.println("SPI3 interface Error!");
      cam3 = false;
      break;
      //while(1);
    }
    myCAM4.write_reg(ARDUCHIP_TEST1, 0x55);
    temp = myCAM4.read_reg(ARDUCHIP_TEST1);
    if (temp != 0x55)
    {
      Serial.println("SPI4 interface Error!");
      cam4 = false;
      break;
      //while(1);
    }
    Serial.println("SPI OK");
    break;
  }

  //Check if the camera module type is OV5642
  while (1) {
    myCAM1.rdSensorReg16_8(OV5642_CHIPID_HIGH, &vid);
    myCAM1.rdSensorReg16_8(OV5642_CHIPID_LOW, &pid);
    if ((vid != 0x56) || (pid != 0x42))
      Serial.println("Can't find OV5642 module!");
    else
      Serial.println("OV5642 detected.");
    break;
  }

  //Set to JPEG capture mode and initialize the OV5642 module
  myCAM1.set_format(JPEG);
  myCAM1.InitCAM();
  myCAM1.write_reg(ARDUCHIP_TIM, VSYNC_LEVEL_MASK);   //VSYNC is active HIGH
  myCAM2.write_reg(ARDUCHIP_TIM, VSYNC_LEVEL_MASK);   //VSYNC is active HIGH
  myCAM3.write_reg(ARDUCHIP_TIM, VSYNC_LEVEL_MASK);   //VSYNC is active HIGH
  myCAM4.write_reg(ARDUCHIP_TIM, VSYNC_LEVEL_MASK);   //VSYNC is active HIGH
  myCAM1.clear_fifo_flag();
  myCAM1.write_reg(ARDUCHIP_FRAMES, FRAMES_NUM);
  myCAM2.write_reg(ARDUCHIP_FRAMES, FRAMES_NUM);
  myCAM3.write_reg(ARDUCHIP_FRAMES, FRAMES_NUM);
  myCAM4.write_reg(ARDUCHIP_FRAMES, FRAMES_NUM);
  myCAM1.OV5642_set_JPEG_size(OV5642_320x240);  //사진 크기 지정
  delay(1000);
  myCAM1.clear_fifo_flag();
  myCAM2.clear_fifo_flag();
  myCAM3.clear_fifo_flag();
  myCAM4.clear_fifo_flag();
  myCAM1.set_bit(ARDUCHIP_GPIO, LOW_POWER_MODE); //low power mode
  myCAM2.set_bit(ARDUCHIP_GPIO, LOW_POWER_MODE);
  myCAM3.set_bit(ARDUCHIP_GPIO, LOW_POWER_MODE);
  myCAM4.set_bit(ARDUCHIP_GPIO, LOW_POWER_MODE);

  Serial.println("set OK");
}

void loop() {
  uint8_t temp = 0xff, temp_last = 0;

  if (Serial1.available()) {
    temp = Serial1.read();
    Serial.println(temp);

    switch (temp) {
      case 49:
        Serial.println(F("CAMs start single shoot"));
        if(cam1) send_bluetooth(myCAM1, 1);
        if(cam2) send_bluetooth(myCAM2, 2);
        if(cam3) send_bluetooth(myCAM3, 3);
        if(cam4) send_bluetooth(myCAM4, 4);
      default:
        break;
    }

    delay(5000);
  }
}

void send_bluetooth(ArduCAM myCAM, int t) {
  uint8_t temp, temp_last;
  uint32_t length = 0;
  Serial.print(F("Cam start capture "));
  Serial.println(t);

  myCAM.clear_bit(ARDUCHIP_GPIO, LOW_POWER_MODE); //low power mode 해제
  myCAM.flush_fifo();
  myCAM.clear_fifo_flag();
  myCAM.start_capture();

  while (!myCAM.get_bit(ARDUCHIP_TRIG, CAP_DONE_MASK)); //캡쳐하는 중
  myCAM.set_bit(ARDUCHIP_GPIO, LOW_POWER_MODE); //low power mode

  length = myCAM.read_fifo_length();

  if (length >= 393216) {
    //Serial.println(F("Over size"));
    myCAM.clear_fifo_flag();
    return;
  }
  if (length == 0) {
    //Serial.println(F("0 size"));
    myCAM.clear_fifo_flag();
    return;
  }

  myCAM.CS_LOW();
  myCAM.set_fifo_burst();
  length--;
  while (length--) {
    temp_last = temp;
    temp = SPI.transfer(0x00);  //read a byte from spi

    if (is_header == true) {
      Serial1.write(temp);
    }
    else if ((temp == 0xD8) & (temp_last == 0xFF))
    {
      is_header = true;
      Serial1.write(temp_last);
      Serial1.write(temp);
    }

    if ((temp == 0xD9) && (temp_last == 0xFF)) {
      Serial1.write(temp_last);
      Serial1.write(temp);
      break;
    }

    delayMicroseconds(30);
  }
  
//  Serial1.println(F("\nEND"));
//  Serial.print(F("Capture Done "));
//  Serial.println(t);

  myCAM.CS_HIGH();
  myCAM.clear_fifo_flag();
  is_header = false;
}

