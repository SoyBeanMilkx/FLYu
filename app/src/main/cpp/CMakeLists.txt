cmake_minimum_required(VERSION 3.22.1)

project("flyu")

add_executable(hw_monitor
        hw_monitor/hw_monitor.c
        hw_monitor/collectors.c)

if (CMAKE_BUILD_TYPE MATCHES "Release|RelWithDebInfo|MinSizeRel")
    target_compile_options(hw_monitor PRIVATE
            -Oz
            -ffunction-sections
            -fdata-sections)
    target_link_options(hw_monitor PRIVATE
            -Wl,--gc-sections
            -Wl,--strip-all)
endif()

set_target_properties(hw_monitor PROPERTIES
        OUTPUT_NAME "hw_monitor"
        PREFIX "lib"
        SUFFIX ".so")
