LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := ImgToGray
LOCAL_SRC_FILES := ImgToGray.cpp
include $(BUILD_SHARED_LIBRARY)