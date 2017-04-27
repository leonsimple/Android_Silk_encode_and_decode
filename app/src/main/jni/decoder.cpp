//
// Created by ketian on 16-9-23.
//

#include <jni.h>

#ifdef __cplusplus
extern "C"
{
#endif

#include "silk.h"
#include "lame.h"
#include "encode.h"
#include <android/log.h>

#define  LOG_TAG    "【C_LOG】"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNICALL jint decoder(JNIEnv *env, jclass clazz, jstring src, jstring dest) {
    LOGI("调用 C xx() 方法\n");
    const char *str_c = env->GetStringUTFChars(src, 0);
    const char *dest_c = env->GetStringUTFChars(dest, 0);

    const char *tmp = "/data/data/com.example.administrator.myapp/t.t";

    if (x(str_c, tmp) == 0) {
        LOGI("调用 C x(str_c, tmp)  方法\n");
        lame_t lame = lame_init();
        lame_set_in_samplerate(lame, 24000);


        lame_set_num_channels(lame, 1);
        lame_set_mode(lame, MONO);
        lame_set_quality(lame, 5);
        lame_init_params(lame);

        FILE *pcm = fopen(tmp, "rb");
        FILE *mp3 = fopen(dest_c, "wb");
        int read, write;

        const int PCM_SIZE = 8192;
        const int MP3_SIZE = 8192;
        short int pcm_buffer[PCM_SIZE];
        unsigned char mp3_buffer[MP3_SIZE];

        do {
            read = fread(pcm_buffer, sizeof(short int), PCM_SIZE, pcm);
            if (read == 0) {
                write = lame_encode_flush(lame, mp3_buffer, MP3_SIZE);
            } else {
                write = lame_encode_buffer(lame, pcm_buffer, NULL, read, mp3_buffer, MP3_SIZE);
            }

            fwrite(mp3_buffer, 1, write, mp3);
        } while (read != 0);

        lame_close(lame);
        fclose(mp3);
        fclose(pcm);
        return 0;
    }

    return -1;
}

JNICALL jint encoder(JNIEnv *env, jclass clazz, jstring src, jstring dest) {
    const char *str_c = env->GetStringUTFChars(src, 0);
    const char *dest_c = env->GetStringUTFChars(dest, 0);
    LOGI("调用 C dc() 方法\n");
    encode(str_c, dest_c);
    return 0;
}


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("调用 C JNI_OnLoad() 方法\n");
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    JNINativeMethod gMethod[] = {
            {"decode", "(Ljava/lang/String;Ljava/lang/String;)I", (void *) decoder},
            {"encode", "(Ljava/lang/String;Ljava/lang/String;)I", (void *) encoder},
    };

    jclass clazz = env->FindClass("com/example/administrator/myapp/Jni");
    if (clazz == NULL) {
        return JNI_ERR;
    }

    if (env->RegisterNatives(clazz, gMethod, 2) < 0) {
        return false;
    }



    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    return;
}

#ifdef __cplusplus
}
#endif