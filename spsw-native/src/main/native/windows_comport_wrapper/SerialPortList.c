#include <jni.h>
#include <stdlib.h>
#include <windows.h>
#include "de_ibapl_spsw_SerialPortList.h"

/*
 * Class:     de_ibapl_spsw_SerialPortList
 * Method:    getWindowsBasedPortNames
 * Signature: (Z)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_de_ibapl_spsw_SerialPortList_getWindowsBasedPortNames
  (JNIEnv *env, jclass clazz, jboolean value) {
    HKEY phkResult;
    LPCSTR lpSubKey = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";
    jobjectArray returnArray = NULL;
    if(RegOpenKeyExA(HKEY_LOCAL_MACHINE, lpSubKey, 0, KEY_READ, &phkResult) == ERROR_SUCCESS){
        boolean hasMoreElements = TRUE;
        DWORD keysCount = 0;
        char valueName[256];
        DWORD valueNameSize;
        DWORD enumResult;
        while(hasMoreElements){
            valueNameSize = 256;
            enumResult = RegEnumValueA(phkResult, keysCount, valueName, &valueNameSize, NULL, NULL, NULL, NULL);
            if(enumResult == ERROR_SUCCESS){
                keysCount++;
            }
            else if(enumResult == ERROR_NO_MORE_ITEMS){
                hasMoreElements = FALSE;
            }
            else {
                hasMoreElements = FALSE;
            }
        }
        if(keysCount > 0){
            jclass stringClass = (*env)->FindClass(env, "java/lang/String");
            returnArray = (*env)->NewObjectArray(env, (jsize)keysCount, stringClass, NULL);
            char lpValueName[256];
            DWORD lpcchValueName;
            byte lpData[256];
            DWORD lpcbData;
            DWORD result;
            for(DWORD i = 0; i < keysCount; i++){
                lpcchValueName = 256;
                lpcbData = 256;
                result = RegEnumValueA(phkResult, i, lpValueName, &lpcchValueName, NULL, NULL, lpData, &lpcbData);
                if(result == ERROR_SUCCESS){
                    (*env)->SetObjectArrayElement(env, returnArray, i, (*env)->NewStringUTF(env, (char*)lpData));
                }
            }
        }
        CloseHandle(phkResult);
    }
    return returnArray;
}

