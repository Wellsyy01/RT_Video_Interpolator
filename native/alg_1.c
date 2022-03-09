#include <stdio.h>
#include <jni.h>
#include "interpolator_Interpolate.h"

JNIEXPORT void JNICALL Java_interpolator_Interpolate_sayHello
  (JNIEnv* env, jobject thisObject) {
    printf("Hello from c");
}