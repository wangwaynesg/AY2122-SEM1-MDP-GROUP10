//UserFunction Header

int robot_move(const uint8_t *, const uint8_t *, uint16_t, TIM_HandleTypeDef, TIM_HandleTypeDef);

void robot_move_const(uint8_t direction, uint16_t pwmVal, uint16_t time, TIM_HandleTypeDef htim8, TIM_HandleTypeDef htim1);

void robot_move_hardcode_triangle(uint16_t pwmVal, TIM_HandleTypeDef htim8, TIM_HandleTypeDef htim1, ADC_HandleTypeDef hadc1);

int IR(ADC_HandleTypeDef hadc1);

void rstDirection(TIM_HandleTypeDef);
