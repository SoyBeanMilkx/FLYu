#include "collectors.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>

// ── CPU 温度 ──

static int is_cpu_thermal(const char *zone_path) {
    char type_path[512];
    char type_buf[128];
    snprintf(type_path, sizeof(type_path), "%s/type", zone_path);
    FILE *f = fopen(type_path, "r");
    if (!f) return 0;
    if (fgets(type_buf, sizeof(type_buf), f)) {
        // 去掉换行
        char *nl = strchr(type_buf, '\n');
        if (nl) *nl = '\0';
        fclose(f);
        // 常见 CPU thermal zone 命名
        if (strstr(type_buf, "cpu") || strstr(type_buf, "CPU") ||
            strstr(type_buf, "soc") || strstr(type_buf, "mtktscpu") ||
            strstr(type_buf, "tsens_tz_sensor")) {
            return 1;
        }
        return 0;
    }
    fclose(f);
    return 0;
}

float read_cpu_temp(void) {
    DIR *dir = opendir("/sys/class/thermal");
    if (!dir) return -1;

    float max_temp = -1;
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL) {
        if (strncmp(entry->d_name, "thermal_zone", 12) != 0) continue;

        char zone_path[512];
        snprintf(zone_path, sizeof(zone_path), "/sys/class/thermal/%s", entry->d_name);

        if (!is_cpu_thermal(zone_path)) continue;

        char temp_path[512];
        snprintf(temp_path, sizeof(temp_path), "%s/temp", zone_path);
        FILE *f = fopen(temp_path, "r");
        if (!f) continue;
        int raw;
        if (fscanf(f, "%d", &raw) == 1) {
            float t = raw / 1000.0f;
            if (t > max_temp) max_temp = t;
        }
        fclose(f);
    }
    closedir(dir);
    return max_temp;
}

// ── 电池 ──

static int read_int_file(const char *path) {
    FILE *f = fopen(path, "r");
    if (!f) return 0;
    int val = 0;
    fscanf(f, "%d", &val);
    fclose(f);
    return val;
}

static void read_str_file(const char *path, char *buf, size_t len) {
    buf[0] = '\0';
    FILE *f = fopen(path, "r");
    if (!f) return;
    if (fgets(buf, (int)len, f)) {
        char *nl = strchr(buf, '\n');
        if (nl) *nl = '\0';
    }
    fclose(f);
}

float read_battery_temp(void) {
    int raw = read_int_file("/sys/class/power_supply/battery/temp");
    return raw / 10.0f;
}

float read_battery_power(int *charging) {
    char status[64];
    read_str_file("/sys/class/power_supply/battery/status", status, sizeof(status));
    *charging = (strcmp(status, "Charging") == 0 || strcmp(status, "Full") == 0) ? 1 : 0;

    int current_ua = read_int_file("/sys/class/power_supply/battery/current_now"); // μA
    int voltage_uv = read_int_file("/sys/class/power_supply/battery/voltage_now"); // μV

    // 某些设备 current_now 单位是 mA 而非 μA，做简单判断
    // 手机电流不可能超过 10A，所以 >10000 的值必定是 μA 单位
    float current_a;
    if (abs(current_ua) > 10000) {
        current_a = current_ua / 1000000.0f; // μA -> A
    } else {
        current_a = current_ua / 1000.0f;    // mA -> A
    }

    float voltage_v = voltage_uv / 1000000.0f; // μV -> V
    float power = current_a * voltage_v;        // W

    // 返回绝对值，充放电由 charging 标志区分
    if (power < 0) power = -power;
    return power;
}

// ── 内存 ──

void read_mem_info(float *used, float *total) {
    *used = 0;
    *total = 0;
    FILE *f = fopen("/proc/meminfo", "r");
    if (!f) return;

    long mem_total_kb = 0, mem_available_kb = 0;
    char line[256];
    while (fgets(line, sizeof(line), f)) {
        if (strncmp(line, "MemTotal:", 9) == 0) {
            sscanf(line + 9, "%ld", &mem_total_kb);
        } else if (strncmp(line, "MemAvailable:", 13) == 0) {
            sscanf(line + 13, "%ld", &mem_available_kb);
        }
        if (mem_total_kb && mem_available_kb) break;
    }
    fclose(f);

    *total = mem_total_kb / (1024.0f * 1024.0f); // KB -> GB
    *used = (mem_total_kb - mem_available_kb) / (1024.0f * 1024.0f);
}

// ── 汇总 ──

void collect_all(hw_data_t *data) {
    memset(data, 0, sizeof(hw_data_t));
    data->cpu_temp = read_cpu_temp();
    data->bat_temp = read_battery_temp();
    data->bat_power = read_battery_power(&data->bat_charging);
    read_mem_info(&data->mem_used, &data->mem_total);
}

int hw_data_to_json(const hw_data_t *data, char *buf, size_t buf_size) {
    return snprintf(buf, buf_size,
        "{\"cpu_temp\":%.1f,\"bat_temp\":%.1f,\"bat_power\":%.1f,"
        "\"bat_charging\":%s,\"mem_used\":%.1f,\"mem_total\":%.1f}\n",
        data->cpu_temp, data->bat_temp, data->bat_power,
        data->bat_charging ? "true" : "false",
        data->mem_used, data->mem_total);
}
