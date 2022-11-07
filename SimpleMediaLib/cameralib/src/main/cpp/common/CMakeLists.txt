file(GLOB_RECURSE COMMON_SRCS "libcommon/src/*.?pp")

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

find_library(log-lib log)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/glm)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/libpng)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/libjpeg-turbo)

add_library(common
        SHARED
        ${COMMON_SRCS}
        )

target_include_directories(common PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/libcommon/include)

target_link_libraries(common
        android
        log
        GLESv2
        EGL
        glm
        png
        turbojpeg)