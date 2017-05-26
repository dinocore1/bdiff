BUILD_DIR := build

.PHONY: all build clean

all: build

clean:
	rm -rf build

build/Makefile:
	mkdir build
	cd build && cmake -DCMAKE_INSTALL_PREFIX=$(abspath $(BUILD_DIR)) ..

build: build/Makefile
	cd build && cmake --build .

