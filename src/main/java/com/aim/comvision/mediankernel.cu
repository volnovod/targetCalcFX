extern "C"
#include <stdlib.h>
#include <stdio.h>
#define MEDIAN_LENGTH 9

__global__ void filter(long *d_input_img, long*d_output_img, int d_iRows, int d_iCols){
    unsigned int row = blockIdx.y*blockDim.y + threadIdx.y;
    unsigned int col = blockIdx.x*blockDim.x + threadIdx.x;
    long window[MEDIAN_LENGTH];

    if(col>=d_iCols || row>=d_iRows){
        return;
        }
    window[0]= (row==0||col==0) ? 0 :                 d_input_img[(row-1)*d_iCols+(col-1)];
    window[1]= (row==0) ? 0 :                         d_input_img[(row-1)*d_iCols+col];
    window[2]= (row==0||col==d_iCols-1) ? 0 :         d_input_img[(row-1)*d_iCols+(col+1)];
    window[3]= (col==0) ? 0 :                         d_input_img[row*d_iCols+(col-1)];
    window[4]=                                        d_input_img[row*d_iCols+col];
    window[5]= (col==d_iCols-1) ? 0 :                 d_input_img[row*d_iCols+(col+1)];
    window[6]= (row==d_iRows-1||col==0) ? 0 :         d_input_img[(row+1)*d_iCols+(col-1)];
    window[7]= (row==d_iRows-1) ? 0 :                 d_input_img[(row+1)*d_iCols+col];
    window[8]= (row==d_iRows-1||col==d_iCols-1) ? 0 : d_input_img[(row+1)*d_iCols+(col+1)];

    // Order elements
    for (unsigned int j=0; j<5; ++j)
    {
        // Find position of minimum element
        long temp = window[j];
        unsigned int  idx  = j;
        for (unsigned int l=j+1; l<9; ++l)
            if (window[l] < temp){ idx=l; temp = window[l];}
        // Put found minimum element in its place
        window[idx] = window[j];
        window[j] = temp;
    }

    d_output_img[row*d_iCols + col] = (window[4]);

}