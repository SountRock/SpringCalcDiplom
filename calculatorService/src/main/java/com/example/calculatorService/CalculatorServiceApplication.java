package com.example.calculatorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculatorServiceApplication {

	//TEST VALUE: 11*2*sqrt(12*2.7 + 0.4)*fact(12+4*sqrt(20))-2.0E34:(1+1)
	//tcalc(1,x=2,y=3)+tcalc(two,x=2,y=3)+tcalc(11*sqrt(x)*y,x=121, y = 11)+tref(2,3)+tname(two,3)

	//Подключить акутаторы на сервера

	//http://localhost:8080/custom/create/fibonachi(one,two,count):RIGHT_SIDE/temp=stepone&stepone=steptwo<one+1&steptwo=temp+steptwo<two+1:count

	//http://localhost:8080/custom/create/fibonachi(one,count):TWO_SIDES/temp=stepone&stepone=steptwo<one+1&steptwo=temp+steptwo<1:count
	//3*100-(0+(0:2))fibonachi(3+3)-(30+10)

	public static void main(String[] args) {
		SpringApplication.run(CalculatorServiceApplication.class, args);
	}
}
