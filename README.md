# Caring Admin

Caring 서비스 관리자 도구.  
Kotlin Multiplatform + Compose Multiplatform 기반으로 **데스크톱(JVM)**과 **웹 브라우저(WASM)** 모두를 단일 코드베이스에서 지원한다.

---

## 관련 레포

| 레포 | 설명 |
|------|------|
| [caring-admin](https://github.com/Grensil/caring-admin) | 이 레포. 프론트엔드 (Desktop + Web) |
| [caring-web-flatform / feat/LOCAL_TEST](https://github.com/Incar-platform-promotion-office/caring-web-flatform/tree/feat/LOCAL_TEST) | 백엔드. Admin API가 이 브랜치에 있음 |

---

## 설계 구조

### 핵심 원칙

모든 화면과 비즈니스 로직은 `admin/commonMain` 하나에 존재한다.  
`desktopApp`, `webApp`에는 플랫폼별 진입점(`main.kt`)만 있다.

```
caring-admin/
├── admin/                      # 공통 모듈 (핵심)
│   └── src/
│       ├── commonMain/         # Desktop + Web 공통 — 화면/뷰모델/API 전부 여기
│       ├── jvmMain/            # 데스크톱 전용 (Ktor CIO 엔진, Java Preferences 저장소)
│       └── wasmJsMain/         # 웹 전용 (Ktor JS 엔진, localStorage 저장소)
│
├── desktopApp/                 # 데스크톱 진입점만 존재 (Window 생성, Koin 초기화)
└── webApp/                     # 웹 진입점만 존재 (ComposeViewport, Koin 초기화)
```

### 플랫폼별 차이

동일한 화면을 두 플랫폼에서 공유하고, 차이는 DI로 격리한다.

| | 데스크톱 (JVM) | 웹 (WASM) |
|---|---|---|
| 진입점 | `desktopApp/main.kt` → `Window()` | `webApp/main.kt` → `ComposeViewport()` |
| HTTP 엔진 | Ktor CIO | Ktor JS |
| 로컬 저장소 | Java Preferences API | localStorage |
| 환경값(BuildConfig) | `jvmMain/BuildConfig.kt` (actual) | `wasmJsMain/BuildConfig.kt` (actual) |

### 공통 모듈 구조 (`admin/commonMain`)

```
kotlin/.../admin/
├── AdminApp.kt                     # 루트 Composable, 로그인/메인 화면 라우팅
├── BuildConfig.kt                  # expect 선언 (BASE_URL, ENV, APP_VERSION)
├── theme/AdminTheme.kt             # MaterialTheme + NotoSansKR 폰트 적용
├── data/
│   ├── AdminApiService.kt          # Ktor HTTP 클라이언트 (모든 API 호출)
│   └── dto/AdminDto.kt             # 요청/응답 DTO
├── model/Adjuster.kt               # 도메인 모델
├── storage/AdminStorage.kt         # 로컬 저장소 인터페이스 (expect)
├── di/AdminCommonModule.kt         # Koin 공통 모듈
├── viewmodel/                      # ViewModel 모음
│   ├── LoginViewModel.kt
│   ├── AdjusterListViewModel.kt
│   ├── ConsultingRequestViewModel.kt
│   └── EducationRequestViewModel.kt
└── screen/                         # 모든 화면 (Desktop + Web 동일)
    ├── LoginScreen.kt
    ├── MainScreen.kt               # 사이드바 + 콘텐츠 레이아웃
    ├── AdjusterListScreen.kt       # 손해사정사 목록 / 편집
    ├── AdjusterEditDialog.kt       # 손해사정사 편집 다이얼로그
    ├── ConsultingRequestScreen.kt  # 보험금 상담 요청 내역
    ├── EducationRequestScreen.kt   # 교육 요청 내역
    ├── UserTypeScreen.kt           # 유저 타입 전환 (미구현)
    ├── PushScreen.kt               # 푸시 알림 발송 (미구현)
    └── ForceUpdateScreen.kt        # 강제 업데이트 (미구현)
```

> **폰트 임베딩**: WASM Skia 렌더러는 CSS 폰트를 사용할 수 없어서 NotoSansKR TTF를  
> `admin/src/commonMain/composeResources/font/` 에 직접 번들한다.

---

## 환경 설정

`local.properties` 파일을 프로젝트 루트에 직접 생성한다 (git에 포함되지 않음).

```properties
sdk.dir=/Users/{USERNAME}/Library/Android/sdk

BASE_URL_DEV=http://{로컬IP}:8080       # Docker로 띄운 로컬 서버
BASE_URL_STAGE=https://caring-web-flatform-stage.onrender.com
BASE_URL_PROD=https://apis.car-ing.kr
```

---

## 실행 방법

### 웹 (브라우저)

```bash
# Dev — 로컬 서버 바라봄 (http://localhost:3000)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Stage
./gradlew :webApp:wasmJsBrowserDevelopmentRun -Penv=stage

# Production 정적 빌드 (배포용)
./gradlew :webApp:wasmJsBrowserProductionWebpack -Penv=release
```

- 같은 와이파이의 다른 기기에서 `http://{맥 IP}:3000/` 으로 접근 가능 (별도 설정 불필요)
- Production 빌드 결과물: `webApp/build/dist/wasmJs/productionExecutable/`

### 데스크톱 (JVM)

```bash
# Dev
./gradlew :desktopApp:run

# Stage
./gradlew :desktopApp:run -Penv=stage

# Production
./gradlew :desktopApp:run -Penv=release
```

---

## 웹 배포

WASM 앱은 정적 파일로 배포한다. 단, 브라우저가 WASM 실행 시 아래 HTTP 헤더를 요구한다.

```
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
```

### nginx에 배포 (기존 서버 사용 시)

```bash
# 빌드
./gradlew :webApp:wasmJsBrowserProductionWebpack -Penv=release

# 서버에 업로드
rsync -avz webApp/build/dist/wasmJs/productionExecutable/ user@서버:/var/www/caring-admin/
```

```nginx
server {
    listen 443 ssl;
    server_name admin.car-ing.kr;

    root /var/www/caring-admin;
    index index.html;

    add_header Cross-Origin-Opener-Policy "same-origin" always;
    add_header Cross-Origin-Embedder-Policy "require-corp" always;

    types { application/wasm wasm; }

    location / {
        try_files $uri /index.html;
    }
}
```

### Netlify에 배포 (간단 옵션)

빌드 결과물 디렉토리에 `_headers` 파일 추가:

```
/*
  Cross-Origin-Opener-Policy: same-origin
  Cross-Origin-Embedder-Policy: require-corp
```

```bash
netlify deploy --dir=webApp/build/dist/wasmJs/productionExecutable --prod
```

> **주의**: 웹 배포 시 API 서버도 반드시 HTTPS여야 한다. HTTP API를 호출하면 브라우저가 Mixed Content로 차단한다.

---

## 현재 구현 현황

| 메뉴 | 기능 | 상태 |
|------|------|------|
| 손해사정사 관리 > 리스트 | 목록 조회 / 노출 여부 편집 / 정보 수정 | 완료 |
| 손해사정사 관리 > 보험금 상담 요청 내역 | 전체 목록 조회 | 완료 |
| 손해사정사 관리 > 교육 요청 내역 | 전체 목록 조회 | 완료 |
| 유저 타입 전환 | — | 미구현 |
| 푸시 알림 발송 | — | 미구현 |
| 강제 업데이트 | — | 미구현 |

---

## 앞으로 할 것

### 기능
- [ ] 유저 타입 전환 — 일반 → FA, S급관리자 → 손해사정사 등 역할 변경
- [ ] 푸시 알림 발송 — FCM 연동, 템플릿(이벤트/광고/사과) 기반 발송
- [ ] 강제 업데이트 관리 — iOS/Android 최소 버전 설정 및 ON/OFF
- [ ] 페이지네이션 — 상담/교육 요청 목록 페이지 단위 로딩
- [ ] 상태 변경 — 상담/교육 요청 상태 직접 변경 (대기 → 수락 → 완료 등)
- [ ] 손해사정사 신규 등록 (현재 편집만 가능)

### 배포 전 필수 작업
- [ ] `caring-web-flatform` `feat/LOCAL_TEST` → `main` 머지 및 Stage/Prod 배포
- [ ] Stage / Prod DB에 admin 계정 INSERT

  ```sql
  INSERT INTO admin_users (login_id, password, name, created_at)
  VALUES ('원하는ID', SHA2('비밀번호', 256), '관리자', NOW());
  ```

- [ ] 토큰 만료/갱신 정책 결정 (현재 무기한)
- [ ] 웹 정적 파일 호스팅 설정 (nginx 서브도메인 또는 Netlify)

---

## 어드민 계정 현황

현재 어드민 계정과 테스트 데이터는 **개발자 로컬 환경(Docker)에만 존재**한다.  
새 환경에서 실행하려면 백엔드 Docker를 띄우고 계정을 수동으로 INSERT해야 한다.

```bash
# caring-web-flatform 디렉토리에서 로컬 서버 실행
docker compose -f docker/local/docker-compose.yml up -d
```

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| 언어 | Kotlin 2.3 |
| UI | Compose Multiplatform 1.10 |
| 네트워크 | Ktor 3.4 (CIO / JS 엔진) |
| DI | Koin 4.1 |
| 직렬화 | kotlinx.serialization |
| 상태관리 | ViewModel + StateFlow |
| 폰트 | NotoSansKR (composeResources 임베딩) |
| 백엔드 | PHP (CodeIgniter 4) + PostgreSQL |
