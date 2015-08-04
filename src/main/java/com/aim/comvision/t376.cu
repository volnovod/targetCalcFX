#include <stdlib.h>
#include <stdio.h>

#define DCOLS 1024
#define DROWS 256

typedef struct {
  size_t step;
  size_t rows;
  size_t cols;
  unsigned char *data;
} mat;


// define the threads and grids for CUDA
#define BLOCK_ROWS 32
#define BLOCK_COLS 16

// define kernel dimensions
#define MEDIAN_LENGTH 9

// this is the error checking part for CUDA
#define gpuErrchk(ans) { gpuAssert((ans), __FILE__, __LINE__); }
inline void gpuAssert(cudaError_t code, char *file, int line, bool abort=true)
{
   if (code != cudaSuccess)
   {
      fprintf(stderr,"GPUassert: %s %s %d\n", cudaGetErrorString(code), file, line);
      if (abort) exit(code);
   }
}


__global__ void FilterKernel (unsigned char *d_input_img, unsigned char *d_output_img, int d_iRows, int d_iCols)

{

    unsigned int row = blockIdx.y*blockDim.y + threadIdx.y;
    unsigned int col = blockIdx.x*blockDim.x + threadIdx.x;
    unsigned char window[MEDIAN_LENGTH];

    if(col>=d_iCols || row>=d_iRows)
        return;

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
        unsigned char temp = window[j];
        unsigned int  idx  = j;
        for (unsigned int l=j+1; l<9; ++l)
            if (window[l] < temp){ idx=l; temp = window[l];}
        // Put found minimum element in its place
        window[idx] = window[j];
        window[j] = temp;
    }

    d_output_img[row*d_iCols + col] = (window[4]);

}

void take_input(const mat& input, const mat& output)
{

    unsigned char *device_input;
    unsigned char *device_output;

    size_t d_ipimgSize = input.step * input.rows;
    size_t d_opimgSize = output.step * output.rows;

    gpuErrchk( cudaMalloc( (void**) &device_input, d_ipimgSize) );
    gpuErrchk( cudaMalloc( (void**) &device_output, d_opimgSize) );

    gpuErrchk( cudaMemcpy(device_input, input.data, d_ipimgSize, cudaMemcpyHostToDevice) );

    dim3 Threads(BLOCK_COLS, BLOCK_ROWS);  // 512 threads per block
    dim3 Blocks((input.cols + Threads.x - 1)/Threads.x, (input.rows + Threads.y - 1)/Threads.y);

    //int check = (input.cols + Threads.x - 1)/Threads.x;
    //printf( "blockx %d", check);

    FilterKernel <<< Blocks, Threads >>> (device_input, device_output, input.rows, input.cols);
    gpuErrchk(cudaDeviceSynchronize());
    gpuErrchk(cudaGetLastError());

    gpuErrchk( cudaMemcpy(output.data, device_output, d_opimgSize, cudaMemcpyDeviceToHost) );

    //printf( "num_rows_cuda %d", num_rows);
    //printf("\n");

    gpuErrchk(cudaFree(device_input));
    gpuErrchk(cudaFree(device_output));

}

int main(){
  mat input_im, output_im;
  input_im.rows  = DROWS;
  input_im.cols  = DCOLS;
  input_im.step  = input_im.cols;
  input_im.data  = (unsigned char *)malloc(input_im.step*input_im.rows);
  output_im.rows = DROWS;
  output_im.cols = DCOLS;
  output_im.step = input_im.cols;
  output_im.data = (unsigned char *)malloc(output_im.step*output_im.rows);

  for (int i = 0; i < DCOLS*DROWS; i++) {
    output_im.data[i] = 0;
    input_im.data[i] = 0;
    int temp = (i%DCOLS);
    if (temp == 5) input_im.data[i] = 20;
    if ((temp > 5) && (temp < 15)) input_im.data[i] = 40;
    if (temp == 15) input_im.data[i] = 20;
    }

  take_input(input_im, output_im);
  for (int i = 2*DCOLS; i < DCOLS*(DROWS-2); i++)
    if (input_im.data[i] != output_im.data[i]) {printf("mismatch at %d, input: %d, output: %d\n", i, (int)input_im.data[i], (int)output_im.data[i]); return 1;}
  printf("Success\n");
  return 0;
}