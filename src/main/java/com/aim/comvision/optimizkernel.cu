extern "C"

#define BLOCK_WIDTH 16
#define BLOCK_HEIGHT 16

__global__ void filter(long *Input_Image, long *Output_Image, int Image_Width, int Image_Height) {

    long surround[9];

    int iterator;

    const int x     = blockDim.x * blockIdx.x + threadIdx.x;
    const int y     = blockDim.y * blockIdx.y + threadIdx.y;

    if( (x >= (Image_Width - 1)) || (y >= Image_Height - 1) || (x == 0) || (y == 0)) return;

    // --- Fill array private to the threads
    iterator = 0;
    for (int r = x - 1; r <= x + 1; r++) {
        for (int c = y - 1; c <= y + 1; c++) {
            surround[iterator] = Input_Image[c*Image_Width+r];
            iterator++;
        }
    }

    // --- Sort private array to find the median using Bubble Short
    for (int i=0; i<5; ++i) {

        // --- Find the position of the minimum element
        int minval=i;
        for (int l=i+1; l<9; ++l) if (surround[l] < surround[minval]) minval=l;

        // --- Put found minimum element in its place
        long temp = surround[i];
        surround[i]=surround[minval];
        surround[minval]=temp;
    }

    // --- Pick the middle one
    Output_Image[(y*Image_Width)+x]=surround[4];

}