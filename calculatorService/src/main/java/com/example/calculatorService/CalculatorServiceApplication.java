package com.example.calculatorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculatorServiceApplication {

	//TEST VALUE: 11*2*sqrt(12*2.7 + 0.4)*fact(12+4*sqrt(20))
	//x*2+3 | x*5.0
	//fact(x) sqrt(x)
	public static void main(String[] args) {
		SpringApplication.run(CalculatorServiceApplication.class, args);
	}

}
