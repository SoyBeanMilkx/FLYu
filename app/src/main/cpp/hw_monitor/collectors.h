#ifndef HW_MONITOR_COLLECTORS_H
#define HW_MONITOR_COLLECTORS_H

#include <stddef.h>

typedef struct {
    float cpu_temp;      // °C
    float bat_temp;      // °C
    float bat_power;     // W (positive = charging, negative = discharging)
    int   bat_charging;  // 1 = charging, 0 = discharging
    float mem_used;      // GB
    float mem_total;     // GB
} hw_data_t;

// 读取 CPU 温度 (°C)，失败返回 -1
float read_cpu_temp(void);

// 读取电池温度 (°C)
float read_battery_temp(void);

// 读取充放电功率 (W)，charging 输出充电状态
float read_battery_power(int *charging);

// 读取内存信息，单位 GB
void read_mem_info(float *used, float *total);

// 采集所有数据到 hw_data_t
void collect_all(hw_data_t *data);

// 将 hw_data_t 序列化为 JSON 字符串 (以 \n 结尾)
// 返回写入 buf 的字节数
int hw_data_to_json(const hw_data_t *data, char *buf, size_t buf_size);

#endif // HW_MONITOR_COLLECTORS_H
