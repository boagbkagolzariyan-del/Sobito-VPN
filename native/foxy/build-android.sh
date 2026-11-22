#!/usr/bin/env bash
set -euo pipefail
# Go >= 1.27.2, Android NDK r29. This build produces only arm64.
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
: "${ANDROID_HOME:?Set ANDROID_HOME to the Android SDK}"
NDK="${ANDROID_NDK_HOME:-$ANDROID_HOME/ndk/29.0.14206865}"
export GOOS=android GOARCH=arm64 CGO_ENABLED=1
export CC="$NDK/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android26-clang"
mkdir -p "$ROOT/app/src/main/jniLibs/arm64-v8a"
cd "$ROOT/native/foxy/core"
go build -buildmode=pie -o "$ROOT/app/src/main/jniLibs/arm64-v8a/libse7enfoxy.so" ./cmd/se7en-foxy
