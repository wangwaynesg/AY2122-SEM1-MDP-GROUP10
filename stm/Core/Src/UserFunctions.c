//User Functions

#include "main.h"
#include "cmsis_os.h"

#include "UserFunctions.h"

//dynamic movement function
//receives command from UART to move
int robot_move(const uint8_t *direction, const uint8_t *direction2,uint16_t pwmVal, TIM_HandleTypeDef htim8, TIM_HandleTypeDef htim1)
{
	//copying raw value
	int i=0;
	uint8_t check[4];

	while(*direction!='\0'){
		check[i] = *direction;
		i++;
		direction++;
	}
	i=0;

	//spliting up dist and dir
	int j=0;
	int a,b,c;
	int dir;

	while(*direction2!='\0'){
		if(j==0){
			a = (*direction2)-'0';
		}
		else if (j==1){
			b =  (*direction2)-'0';
		}
		else if (j==2){
			c =  (*direction2)-'0';
		}
		else{
			dir = (*direction2)-'0';
		}
		j++;
		direction2++;
	}
	j=0;
	if(strcmp(check,"9999")!=0){
		//OLED_ShowString(10,40,"runnings\0"); //debugging code
	if(dir==0){				//forward
			int dist = a*100 + b*10 + c;
			int time = ((dist*1000)/29.5);

			robot_move_const(0, pwmVal, time, htim8, htim1);
			return 1;
	}

	else if(dir==1){				//backward
			int dist = a*100 + b*10 + c;
			int time = ((dist*1000)/29.5);

			robot_move_const(1, pwmVal, time, htim8, htim1);
			return 1;
	}

	else if(dir==2){				//forward left
			int angle = a*100 + b*10 + c;
			int time = ((angle*5900/180));

			robot_move_const(2, pwmVal, time, htim8, htim1);
  	  		return 1;
	}

	else if(dir==3){				//forward right
			int angle = a*100 + b*10 + c;
			int time = ((angle*6100/180));

			robot_move_const(3, pwmVal, time, htim8, htim1);
  	  		return 1;
	}

	else if(dir==4){				//backward left
			int angle = a*100 + b*10 + c;
			int time = ((angle*6940/180));

			robot_move_const(4, pwmVal, time, htim8, htim1);
  	  		return 1;
	}

	else if(dir==5){				//backward right
			int angle = a*100 + b*10 + c;
			int time = ((angle*6200/180));

			robot_move_const(5, pwmVal, time, htim8, htim1);
  	  		return 1;
	}
	else if(dir==6){
			robot_move_const(6, pwmVal, 0, htim8, htim1);	//no need time here
			return 1;
	}
	else if(dir==7){
			int time = a*1000 + b*100 + c*10;
			robot_move_const(7, pwmVal, time, htim8, htim1);	//no need time here
			return 1;
	}
	else if(dir==8){
			rstDirection(htim1);
			return 1;
	}
	else{
			OLED_ShowString(10,40,"running\0"); //debugging code
			return 0;
	}
	}

}

//static movement function
//for testing and also cleaner dynamic movement function code
void robot_move_const(uint8_t direction, uint16_t pwmVal, uint16_t time, TIM_HandleTypeDef htim8, TIM_HandleTypeDef htim1)
{
	switch (direction)
		{
		//////////////////////SPEED: 102cm per second/////////////////////////
			case 0:				//forward
				//left motor
				htim1.Instance->CCR4 = 75;
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_SET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_RESET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal+500);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_SET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_RESET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal+500);
				osDelay(time);
				break;

			case 1:				//backward
				//left motor
				htim1.Instance->CCR4 = 74.9;
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_RESET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_SET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal+500);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_RESET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_SET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal+500);
				osDelay(time);
				break;

			case 2:				//forward left by 90deg (Turn Diameter of 45cm, takes 6 seconds to do 180 degree turn)
				htim1.Instance->CCR4 = 53; //extreme left
				//left motor
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_SET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_RESET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal/5);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_SET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_RESET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal);
	  	  		osDelay(time);
	  	  		htim1.Instance->CCR4 = 77; //Move front wheel position to center, was: 77
	  	  		//robot_move_const(7, pwmVal, 500, htim8, htim1);
	  	  		//rstDirection(htim1);
				break;

			case 3:				//forward right by 90deg (Turn Diameter of 41, takes 5.6 seconds to do 180 degree turn)
				htim1.Instance->CCR4 = 100; //extreme right
				//left motor
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_SET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_RESET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_SET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_RESET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal/5);
	  	  		osDelay(time);
	  	  		htim1.Instance->CCR4 = 75; //Move front wheel position to center
	  	  		//robot_move_const(7, pwmVal, 500, htim8, htim1);
	  	  	  	//rstDirection(htim1);
				break;

			case 4:				//backward left by 90deg	Turn Diameter of 24cm, takes 5.7 seconds to do 180 degree turn(oldvalue)
				htim1.Instance->CCR4 = 53; //extreme left
				//left motor
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_RESET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_SET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal/10);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_RESET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_SET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal);
	  	  		osDelay(time);
	  	  		htim1.Instance->CCR4 = 75.5; //Move front wheel position to center
				break;

			case 5:				//backward right by 90deg	Turn Diameter of 19cm, takes 4.4 seconds to do 180 degree turn(oldvalue)
				htim1.Instance->CCR4 = 100; //extreme right
				//left motor
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_RESET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_SET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_RESET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_SET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal/5);
	  	  		osDelay(time);
	  	  		htim1.Instance->CCR4 = 75.5; //Move front wheel position to center
				break;
			//for A5 of the checklist
			case 6: //9cm from object
			  	 robot_move_const(1, pwmVal, 400, htim8, htim1);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
			  	 robot_move_const(4, pwmVal, 3250, htim8, htim1);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
			  	 //hard code backwards just for case 6
			  	 	//left motor
					htim1.Instance->CCR4 = 76.5;
					HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_RESET);
					HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_SET);
					//Modify the comparison value for the duty cycle
					__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,pwmVal);

					//right motor
		  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_RESET);
		  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_SET);
		  	  		//Modify the comparison value for the duty cycle
		  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,pwmVal);
					osDelay(2800);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
			  	 robot_move_const(2, pwmVal, 3000, htim8, htim1);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
			  	 robot_move_const(0, pwmVal, 960, htim8, htim1);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
			  	 robot_move_const(3, pwmVal, 2950, htim8, htim1);
			  	 robot_move_const(7, pwmVal, 150, htim8, htim1);
	//		  	 robot_move_const(0, pwmVal, 300, htim8, htim1);
	//		  	 robot_move_const(7, pwmVal, 150, htim8, htim1);

			default:			//Return servo motors to center and stop everything
	  	  		htim1.Instance->CCR4 = 75.5; //Move front wheel position to center
				//left motor
				HAL_GPIO_WritePin(GPIOA,AIN2_Pin,GPIO_PIN_SET);
				HAL_GPIO_WritePin(GPIOA,AIN1_Pin,GPIO_PIN_RESET);
				//Modify the comparison value for the duty cycle
				__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_1,0);

				//right motor
	  	  		HAL_GPIO_WritePin(CIN2_GPIO_Port,CIN2_Pin,GPIO_PIN_SET);
	  	  		HAL_GPIO_WritePin(GPIOE,CIN1_Pin,GPIO_PIN_RESET);
	  	  		//Modify the comparison value for the duty cycle
	  	  		__HAL_TIM_SetCompare(&htim8,TIM_CHANNEL_3,0);
	  	  		osDelay(time);
				break;
		}

}

//for week 9 task
void robot_move_hardcode_triangle(uint16_t pwmVal, TIM_HandleTypeDef htim8, TIM_HandleTypeDef htim1, ADC_HandleTypeDef hadc1){
	while(IR(hadc1)>15){
		robot_move_const(0, 3500, 50, htim8, htim1);
	}
	robot_move_const(7, pwmVal, 100, htim8, htim1); 		//to stop movement
	robot_move_const(1, 3000, 800, htim8, htim1); 			//move back to give enough space for turn
	robot_move_const(7, pwmVal, 100, htim8, htim1); 		//to stop movement

///////////////////////////////////////////////////////////////////////////////////
	//turn left 45 degree then move 63.639cm
	robot_move_const(2, 3000, 600, htim8, htim1);			//turn left
	robot_move_const(7, pwmVal, 100, htim8, htim1); 		//to stop movement
	robot_move_const(0, pwmVal, 690, htim8, htim1);			//move forward

	//turn right 135 degree then move 60cm
	robot_move_const(3, 5500, 1000, htim8, htim1);			//turn right
	robot_move_const(7, pwmVal, 50, htim8, htim1); 			//to stop movement
	robot_move_const(0, 5500, 1150, htim8, htim1);			//move forward

	//turn right 135 degree then move 63.639cm
	robot_move_const(3, 5500, 850, htim8, htim1);			//turn right
	robot_move_const(7, pwmVal, 50, htim8, htim1); 			//to stop movement
	robot_move_const(0, pwmVal, 580, htim8, htim1);			//move forward

	//turn left 45 degree then travel back to starting position
	robot_move_const(7, pwmVal, 50, htim8, htim1); 			//to stop movement
	robot_move_const(2, 4000, 500, htim8, htim1);			//turn left
	robot_move_const(7, pwmVal, 250, htim8, htim1); 		//to stop movement
	rstDirection(htim1);
	robot_move_const(7, pwmVal, 50, htim8, htim1);			//to stop movement
	while(IR(hadc1)>15){
		robot_move_const(0, 3500, 50, htim8, htim1);
	}

	robot_move_const(7, pwmVal, 50, htim8, htim1); 			//to stop movement
///////////////////////////////////////////////////////////////////////////////////

}

//IR sensor function
int IR(ADC_HandleTypeDef hadc1){
	uint16_t raw;
	char msg[10];
	int distance;

	///////IR Sensor Code////////////
	//Set GPIO pin to high
	HAL_GPIO_WritePin(GPIOA, GPIO_PIN_10, GPIO_PIN_SET);

	//Get ADC value
	HAL_ADC_Start(&hadc1);
	HAL_ADC_PollForConversion(&hadc1, HAL_MAX_DELAY);
	raw = HAL_ADC_GetValue(&hadc1);

	//Set GPIO pin to low
	HAL_GPIO_WritePin(GPIOA, GPIO_PIN_10, GPIO_PIN_RESET);

	//Convert to string and display on OLED
	distance = 35 - +(((int)raw)/100);
	sprintf(msg, "%hu\r\n", distance);
	OLED_ShowString(10,30,msg);

	return distance;
}

void rstDirection(TIM_HandleTypeDef htim1){
	  htim1.Instance->CCR4 = 53; //extreme left
	  osDelay(500);
	  htim1.Instance->CCR4 = 96; //extreme right
	  osDelay(500);
	  htim1.Instance->CCR4 = 75; //Move front wheel position to center
	  osDelay(500);				//delay must be here for the wheels to turn
}
