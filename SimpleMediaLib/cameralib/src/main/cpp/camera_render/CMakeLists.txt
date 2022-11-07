set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

find_library(log-lib log)

file(GLOB_RECURSE SRCS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")

add_library(
        CameraRender
        SHARED
        ${SRCS}
)

target_include_directories(CameraRender
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${CMAKE_CURRENT_SOURCE_DIR}/../common/libcommon/include
        ${CMAKE_CURRENT_SOURCE_DIR}/../common/glm)

target_link_libraries(CameraRender  android log GLESv2 EGL common)

