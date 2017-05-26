cmake_minimum_required(VERSION 3.2.2)

find_package(JNI)

add_library(buzhash SHARED
    "src/main/cpp/buzhash.cpp"
)

include_directories(
    "${JNI_INCLUDE_DIRS}"
    "src/main/cpp"
)

target_link_libraries(buzhash
    "${JNI_LIBRARIES}"
)