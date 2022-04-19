#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "interpolator_Interpolate.h"

JNIEXPORT jintArray JNICALL Java_interpolator_Interpolate_interpolatein
  (JNIEnv* env, jobject thisObject, jintArray pixels, jint width, jint height, jint target_width, jint target_height) {
    
    jsize len = (*env)->GetArrayLength(env, pixels);

    int new_size = target_width * target_height;
    jint *pixel_arr = GetIntArrayElements(env, pixels, 0);
    int *array = malloc(new_size * sizeof(int));

    int i, j;
    int arr_pointer = 0;
    int i_pointer = 0;

    int f_0_0, f_0_1, f_1_0, f_1_1;
    int t_0_0, t_0_1, t_1_0, t_1_1;

    int x_pos = 0;


    for (i = 0 ; i < target_height ; i++) {

      x_pos = 0;
      for (j = 0 ; j < target_width ; j++) {

        if (i % 2 == 0 && j % 2 == 0) {
          array[arr_pointer] = pixel_arr[i_pointer];
          i_pointer = i_pointer + 1;
        } else {

          // Get pixel value to direct left

          if (j % 2 == 1 && i % 2 == 0) {
            if ( j == 0 ) {
              f_0_0 = 0;
              t_0_0 = 0;
            } else {
              f_0_0 = pixel_arr[i_pointer - 1];
              t_0_0 = 1;
            }

            // Get pixel value from first value to the right
            if (j == (target_width - 1)){
              f_1_1 = 0;
              t_1_1 = 0;
            } else {
              f_1_1 = pixel_arr[i_pointer];
              t_1_1 = 1;
            }

          } else if (i % 2 == 1) {

            if (i != 0) {
              int targ_pos = (int) i_pointer - target_width + (x_pos / 2);
              f_0_0 = pixel_arr[targ_pos];
              t_0_0 = 1;
            } else {
              f_0_0 = 0;
              t_0_0 = 0;
            }

            if (j != target_width - 1) {
              int targ_pos = (int) i_pointer - target_width + (x_pos / 2) + 1;
              f_1_1 = pixel_arr[targ_pos];
              t_1_1 = 1;
            } else {
              f_1_1 = 0;
              t_1_1 = 0;
            }

          } else {

            f_0_0 = 0;
            t_0_0 = 0;
            f_1_1 = 0;
            t_1_1 = 0;

          }

          if (j % 2 == 0 && i % 2 == 1) {
            
            if ( i == target_height - 1) {
              f_1_0 = 0;
              t_1_0 = 0;
            } else {
              int targ_pos = (int) i_pointer + (x_pos/2);
              f_1_0 = pixel_arr[targ_pos];
              t_1_0 = 1;
            }

            if ( i == 0) {
              f_0_1 = 0;
              t_0_1 = 0;
            } else {
              int targ_pos = (int) i_pointer - target_width + (x_pos/2);
              f_0_1 = pixel_arr[targ_pos];
              t_0_1 = 1;
            }


          } else if (j % 2 == 1) {

            if (i != 0) {
              int targ_pos = (int) i_pointer + (x_pos/2);
              f_1_0 = pixel_arr[targ_pos];
              t_1_0 = 1;
            } else {
              f_1_0 = 0;
              t_1_0 = 0;
            }

            if (j != 0) {
              int targ_pos = (int) i_pointer + (x_pos/2) + 1;
              f_0_1 = pixel_arr[i_pointer + target_width];
              t_0_1 = 1;
            } else {
              f_0_1 = 0;
              t_0_1 = 0;
            }

          } else {

            f_1_0 = 0;
            t_1_0 = 0;
            f_0_1 = 0;
            t_0_1 = 0;

          }  

          array[arr_pointer] = (f_0_0 + f_0_1 + f_1_0 + f_1_1) / (t_0_0 + t_0_1 + t_1_0 + t_1_1);

        }
        arr_pointer = arr_pointer + 1;
        x_pos = x_pos + 1;
      }

    }

    (*env)-> ReleaseIntArrayElements(env, pixels, pixel_arr, 0);

    return &array;

}