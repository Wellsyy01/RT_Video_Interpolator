#include <stdio.h>
#include <jni.h>
#include "interpolator_Interpolate.h"

JNIEXPORT void JNICALL Java_interpolator_Interpolate_sayHello
  (JNIEnv* env, jobject thisObject, jintArray pixels, jint width, jint height, jint target_width, jint target_height) {
    
    jsize len = (*env)->GetArrayLength(env, pixels);
    int x_periodicity = target_width / width;
    int y_periodicity = target_height / height;
    int pixels_to_fill = new_size - len;
    jint *pixel_arr = GetIntArrayElements(env, pixels, 0);
    int *array = malloc(new_size * sizeof(int));

    int x_pos = 0;
    int y_pos = 0;

    int i, j;
    int arr_point = 0;
    int i_pointer = 0;

    int f_0_0, f_0_1, f_1_0, f_1_1;
    int t_0_0, t_0_1, t_1_0, t_1_1;

    for (i = 0 ; i < target_height ; i++) {

      for (j = 0 ; j < target_width ; j++) {

        if (j % x_periodicity == 0 && i % y_periodicity == 0){
          array[arr_pointer] = pixel_arr[i_point];
          i_point = i_point + 1;
        } else {

          // Get pixel value to direct left
          if ( j == 0 ) {
            f_0_0 = 0;
            t_0_0 = 0;
          } else {
            f_0_0 = array[arr_pointer - 1];
            t_0_0 = 1;
          }

          // Get pixel value from directly above
          if ( i == 0 ) {
            f_0_1 = 0;
            t_0_1 = 0;
          } else {
            f_0_1 = array[arr_pointer - (i * target_width)]
            t_0_1 = 1;
          }

          // Get pixel value from first value to the right
          if (j == (target_width - 1)){
            f_1_1 = 0;
            t_1_1 = 0;
          } else {
            f_1_1 = pixel_arr[i_point + 1];
            t_1_1 = 1;
          }

          // Get pixel value from first value below
          if (j == (target_width - 1)){
            f_1_0 = 0;
            t_1_0 = 0;
          } else {
            f_1_0 = pixel_arr[i_point + target_width];
            t_1_0 = 1;
          }

          

        }
        i_pointer = i_pointer + 1;
      }

    }

    (*env)-> ReleaseArrayElements(env, pixels, 0)

}