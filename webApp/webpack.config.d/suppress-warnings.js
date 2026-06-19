// Skiko(Compose Multiplatform 렌더러)의 동적 require()로 인한
// "Critical dependency: the request of a dependency is an expression" 경고 억제.
// 이 경고는 앱 동작에 영향을 주지 않는 Kotlin/WASM 런타임 내부 이슈다.
config.ignoreWarnings = [
    /Critical dependency: the request of a dependency is an expression/,
];
