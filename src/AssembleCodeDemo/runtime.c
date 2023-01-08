#include <stdio.h>

int arr[10];
int cls[3];

void init_structs() {
    for (int i = 0; i < 10; i++)
        arr[i] = 0;
    for (int i = 0; i < 3; i++)
        cls[i] = 0;
}

void print_int(int v) {
    printf("%d\n", v);
    fflush(stdout);
}

void print_arr(int i) {
    print_int(arr[i]);
}

void set_arr(int i, int v) {
    arr[i] = v;
}

void print_cls(char c) {
    print_int(cls[c - 'a']);
}

void set_cls(char c, int v) {
    cls[c - 'a'] = v;
}