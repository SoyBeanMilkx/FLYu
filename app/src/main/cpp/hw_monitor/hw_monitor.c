#include "collectors.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <sys/socket.h>
#include <sys/un.h>

#define SOCKET_NAME "flyu_hw_monitor"
#define DEFAULT_INTERVAL 2  // seconds
#define RECONNECT_DELAY  3  // seconds

static volatile int running = 1;

static void signal_handler(int sig) {
    (void)sig;
    running = 0;
}

static int connect_to_server(void) {
    int fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd < 0) return -1;

    struct sockaddr_un addr;
    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    addr.sun_path[0] = '\0';
    strncpy(addr.sun_path + 1, SOCKET_NAME, sizeof(addr.sun_path) - 2);
    socklen_t addr_len = offsetof(struct sockaddr_un, sun_path) + 1 + strlen(SOCKET_NAME);

    if (connect(fd, (struct sockaddr*)&addr, addr_len) < 0) {
        close(fd);
        return -1;
    }
    return fd;
}

int main(int argc, char *argv[]) {
    int interval = DEFAULT_INTERVAL;
    if (argc > 1) {
        int val = atoi(argv[1]);
        if (val > 0) interval = val;
    }

    signal(SIGTERM, signal_handler);
    signal(SIGINT, signal_handler);
    signal(SIGPIPE, SIG_IGN);

    fprintf(stderr, "hw_monitor: started, interval=%ds, socket=@%s (client mode)\n", interval, SOCKET_NAME);

    while (running) {
        int fd = connect_to_server();
        if (fd < 0) {
            fprintf(stderr, "hw_monitor: connect failed, retrying in %ds\n", RECONNECT_DELAY);
            sleep(RECONNECT_DELAY);
            continue;
        }

        fprintf(stderr, "hw_monitor: connected to server\n");

        while (running) {
            hw_data_t data;
            collect_all(&data);

            char json_buf[1024];
            int json_len = hw_data_to_json(&data, json_buf, sizeof(json_buf));

            ssize_t written = write(fd, json_buf, json_len);
            if (written <= 0) {
                fprintf(stderr, "hw_monitor: server disconnected\n");
                break;
            }

            sleep(interval);
        }

        close(fd);

        if (running) {
            sleep(RECONNECT_DELAY);
        }
    }

    fprintf(stderr, "hw_monitor: stopped\n");
    return 0;
}
