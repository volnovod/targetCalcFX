extern "C"

#define BLOCK_WIDTH 16
#define BLOCK_HEIGHT 16


__global__ void filter(int *Input_Image, int *Output_Image, int Image_Width, int Image_Height)
{
    const int tx_l = threadIdx.x;                           // --- Local thread x index
    const int ty_l = threadIdx.y;                           // --- Local thread y index

    const int tx_g = blockIdx.x * blockDim.x + tx_l;        // --- Global thread x index
    const int ty_g = blockIdx.y * blockDim.y + ty_l;        // --- Global thread y index

    __shared__ int smem[BLOCK_WIDTH+2][BLOCK_HEIGHT+2];

    // --- Fill the shared memory border with zeros
    if (tx_l == 0)                      smem[tx_l]  [ty_l+1]    = 0;    // --- left border
    else if (tx_l == BLOCK_WIDTH-1)     smem[tx_l+2][ty_l+1]    = 0;    // --- right border
    if (ty_l == 0) {                    smem[tx_l+1][ty_l]      = 0;    // --- upper border
        if (tx_l == 0)                  smem[tx_l]  [ty_l]      = 0;    // --- top-left corner
        else if (tx_l == BLOCK_WIDTH-1) smem[tx_l+2][ty_l]      = 0;    // --- top-right corner
        }   else if (ty_l == BLOCK_HEIGHT-1) {smem[tx_l+1][ty_l+2]  = 0;    // --- bottom border
        if (tx_l == 0)                  smem[tx_l]  [ty_l+2]    = 0;    // --- bottom-left corder
        else if (tx_l == BLOCK_WIDTH-1) smem[tx_l+2][ty_l+2]    = 0;    // --- bottom-right corner
    }

    // --- Fill shared memory
                                                                    smem[tx_l+1][ty_l+1] =                           Input_Image[ty_g*Image_Width + tx_g];      // --- center
    if ((tx_l == 0)&&((tx_g > 0)))                                      smem[tx_l]  [ty_l+1] = Input_Image[ty_g*Image_Width + tx_g-1];      // --- left border
    else if ((tx_l == BLOCK_WIDTH-1)&&(tx_g < Image_Width - 1))         smem[tx_l+2][ty_l+1] = Input_Image[ty_g*Image_Width + tx_g+1];      // --- right border
    if ((ty_l == 0)&&(ty_g > 0)) {                                      smem[tx_l+1][ty_l]   = Input_Image[(ty_g-1)*Image_Width + tx_g];    // --- upper border
            if ((tx_l == 0)&&((tx_g > 0)))                                  smem[tx_l]  [ty_l]   = Input_Image[(ty_g-1)*Image_Width + tx_g-1];  // --- top-left corner
            else if ((tx_l == BLOCK_WIDTH-1)&&(tx_g < Image_Width - 1))     smem[tx_l+2][ty_l]   = Input_Image[(ty_g-1)*Image_Width + tx_g+1];  // --- top-right corner
         } else if ((ty_l == BLOCK_HEIGHT-1)&&(ty_g < Image_Height - 1)) {  smem[tx_l+1][ty_l+2] = Input_Image[(ty_g+1)*Image_Width + tx_g];    // --- bottom border
         if ((tx_l == 0)&&((tx_g > 0)))                                 smem[tx_l]  [ty_l+2] = Input_Image[(ty_g-1)*Image_Width + tx_g-1];  // --- bottom-left corder
        else if ((tx_l == BLOCK_WIDTH-1)&&(tx_g < Image_Width - 1))     smem[tx_l+2][ty_l+2] = Input_Image[(ty_g+1)*Image_Width + tx_g+1];  // --- bottom-right corner
    }
    __syncthreads();

    // --- Pull the 3x3 window in a local array
    long v[9] = { smem[tx_l][ty_l],   smem[tx_l+1][ty_l],     smem[tx_l+2][ty_l],
                            smem[tx_l][ty_l+1], smem[tx_l+1][ty_l+1],   smem[tx_l+2][ty_l+1],
                            smem[tx_l][ty_l+2], smem[tx_l+1][ty_l+2],   smem[tx_l+2][ty_l+2] };

    // --- Bubble-sort
    for (int i = 0; i < 5; i++) {
        for (int j = i + 1; j < 9; j++) {
            if (v[i] > v[j]) { // swap?
                int tmp = v[i];
                v[i] = v[j];
                v[j] = tmp;
            }
         }
    }

    // --- Pick the middle one
    Output_Image[ty_g*Image_Width + tx_g] = v[4];
}