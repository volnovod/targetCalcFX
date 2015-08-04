extern "C"
#include "stdio.h"

__global__ void example(int** A, int** B,int* C, int threads, int size){

    int i = threadIdx.x;
    C[i]=0;
    for(int k=0; k<threads; k++){
            C[i] += A[i][k] + B[i][k];
            printf(" A=%d C=%d  i=%d k=%d \n", A[k][i], C[k], i,k);
    }
    __syncthreads();
}