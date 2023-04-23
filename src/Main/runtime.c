#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void print_int(int i) {
    printf("%d\n", i);
    fflush(stdout);
}

void *alloc(int size) {
    void *p = malloc(size);
    memset(p, 0, size);
    return p;
}