extern "C"

#include "limits.h"
#include "stdlib.h"

void arrayToVector(int width, int height,int** array, int* vector){
    int counter = 0;
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            vector[counter] = array[i][j];
            counter++;
        }
    }
}

int comparator (const void * a, const void * b)
{
    return ( *(int*)a - *(int*)b );
}

__global__ void filter(int width, int height, int** inputData){

    int i = threadIdx.x;
    int j = threadIdx.y;

    int** window;
    int vectorSize = 9;
    int* vectorFromArray;

    for (int i = 0; i < height-2; ++i) {
        for (int j = 0; j < width-2; ++j) {

            for (int k = 0; k < 3; ++k) {
                for (int l = 0; l < 3; ++l) {
                    window[k][l] =  inputData[i+k][j+l];
                }

            }

            arrayToVector(3,3, window, vectorFromArray);
            qsort(vectorFromArray, vectorSize, sizeof(int), comparator);
            inputData[i+1][j+1] = vectorFromArray[4];
        }
    }

}

