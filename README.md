# Caring Admin

Caring 서비스의 관리자 도구. Kotlin Multiplatform + Compose Multiplatform 기반으로 **데스크톱 앱(JVM)**과 **웹 브라우저(WASM)** 모두를 단일 코드베이스에서 지원한다.

---

## 프로젝트 구조

```
caring-admin/
├── admin/                          # 공통 UI 모듈 (핵심)
│   └── src/
│       ├── commonMain/             # Desktop + Web 공통 코드
│       │   ├── composeResources/
│       │   │   └── font/
│       │   │       └── NotoSansKR_Regular.ttf   # 폰트 임베딩 (WASM 한글 렌더링용)
│       │   └── kotlin/.../admin/
│       │       ├── AdminApp.kt                  # 루트 Composable, 화면 라우팅
│       │       ├── BuildConfig.kt               # expect (환경별 BASE_URL 등)
│       │       ├── data/
│       │       │   ├── AdminApiService.kt        # Ktor HTTP 클라이언트
│       │       │   └── dto/AdminDto.kt           # 요청/응답 DTO
│       │       ├── di/AdminCommonModule.kt       # Koin 공통 모듈
│       │       ├── model/Adjuster.kt             # 도메인 모델
│       │       ├── screen/                       # 모든 화면 (Desktop + Web 공유)
│       │       │   ├── LoginScreen.kt
│       │       │   ├── MainScreen.kt             # 사이드바 + 콘텐츠 레이아웃
│       │       │   ├── AdjusterListScreen.kt     # 손해사정사 목록/편집
│       │       │   ├── AdjusterEditDialog.kt     # 편집 다이얼로그
│       │       │   ├── ConsultingRequestScreen.kt # 보험금 상담 요청 내역
│       │       │   ├── EducationRequestScreen.kt  # 교육 요청 내역
│       │       │   ├── UserTypeScreen.kt          # 유저 타입 전환 (미구현)
│       │       │   ├── PushScreen.kt             # 푸시 알림 발송 (미구현)
│       │       │   └── ForceUpdateScreen.kt      # 강제 업데이트 (미구현)
│       │       ├── storage/AdminStorage.kt        # 로컬 저장소 인터페이스
│       │       ├── theme/AdminTheme.kt            # MaterialTheme + NotoSansKR 적용
│       │       └── viewmodel/                    # ViewModel 모음
│       ├── jvmMain/                # JVM(데스크톱) 전용
│       │   ├── di/JvmPlatformModule.kt           # Ktor CIO 엔진 + JvmAdminStorage
│       │   └── storage/JvmAdminStorage.kt        # Java Preferences API 기반 저장소
│       └── wasmJsMain/             # WASM(웹) 전용
│           ├── di/WebPlatformModule.kt           # Ktor JS 엔진 + WebAdminStorage
│           └── storage/WebAdminStorage.kt        # localStorage 기반 저장소
│
├── desktopApp/                     # 데스크톱 진입점만 존재
│   └── src/jvmMain/.../main.kt    # Window 생성, Koin 초기화
│
├── webApp/                         # 웹 진입점만 존재
│   └── src/wasmJsMain/
│       ├── kotlin/.../main.kt      # ComposeViewport, Koin 초기화
│       └── resources/index.html   # 웹 HTML 껍데기
│
└── shared/                         # (기존 KMP 앱용 shared 모듈, admin과 무관)
```

### 설계 원칙

- **모든 화면과 비즈니스 로직은 `admin/commonMain`에**. `desktopApp`, `webApp`에는 진입점(`main.kt`)만 존재.
- **플랫폼 차이는 `expect/actual` + Koin DI로 격리**
  - `BuildConfig` — 환경별 BASE_URL, APP_VERSION
  - `AdminStorage` — JVM: Java Preferences / Web: localStorage
  - `HttpClient` — JVM: Ktor CIO 엔진 / Web: Ktor JS 엔진
- **폰트는 composeResources에 TTF 임베딩** — WASM Skia 렌더러는 CSS 폰트를 사용할 수 없으므로 반드시 파일로 번들해야 한글이 표시됨

---

## 백엔드 레포

Admin API는 이 레포가 아닌 별도 백엔드 레포에 있다.

**레포**: [Incar-platform-promotion-office/caring-web-flatform](https://github.com/Incar-platform-promotion-office/caring-web-flatform/commits/feat/LOCAL_TEST/)  
**브랜치**: `feat/LOCAL_TEST`

Admin 관련 파일:
- `app/Controllers/AdminController.php` — 로그인, 손해사정사 조회/수정, 상담/교육 요청 조회
- `app/Repositories/AdjusterRepository.php` — 손해사정사 DB 조회/수정
- `app/Repositories/ConsultingRequestRepository.php` — 상담 요청 전체 조회 (admin용)
- `app/Repositories/EducationRequestRepository.php` — 교육 요청 전체 조회 (admin용)
- `app/Config/Routes.php` — `/admin/*` 라우트 정의
- `docker/local/nginx.conf` — 로컬 Docker용 CORS 설정

> **현재 이 브랜치는 main에 머지되지 않았다.** Stage/Prod 배포 전에 머지 및 환경별 검증 필요.

### 로컬 DB 현황

어드민 계정 및 테스트 데이터는 **개발자 로컬 환경(Docker)에만 존재**한다.  
다른 개발자가 Dev 환경에서 실행하려면 로컬 DB를 직접 세팅해야 한다.

**로컬 서버 실행 방법** (caring-web-flatform 레포 참고):
```bash
# caring-web-flatform 디렉토리에서
docker compose -f docker/local/docker-compose.yml up -d
```

**어드민 계정 수동 INSERT** (로컬 DB 접속 후):
```sql
INSERT INTO admin_users (login_id, password, name, created_at)
VALUES ('원하는ID', SHA2('비밀번호', 256), '관리자', NOW());
```

---

## 환경 설정

### local.properties (git에 포함되지 않음, 직접 작성)

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
# Dev (로컬 서버 바라봄)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Stage
./gradlew :webApp:wasmJsBrowserDevelopmentRun -Penv=stage

# Production 빌드 (배포용 정적 파일 생성)
./gradlew :webApp:wasmJsBrowserProductionWebpack -Penv=release
```

브라우저에서 `http://localhost:3000/` 접속.  
같은 와이파이 기기에서도 `http://{맥 IP}:3000/` 으로 접근 가능 (별도 설정 불필요).

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

## Stage / Prod 배포 전 필수 작업

현재 어드민 계정 및 Admin API는 **로컬 환경에만 존재**한다. Stage/Prod에서 동작하려면 아래 작업이 모두 필요하다.

1. **`feat/LOCAL_TEST` 브랜치를 `main`에 머지**  
   → [caring-web-flatform PR 생성](https://github.com/Incar-platform-promotion-office/caring-web-flatform/compare/main...feat/LOCAL_TEST)

2. **Stage / Prod DB에 admin 계정 INSERT**

   ```sql
   INSERT INTO admin_users (login_id, password, name, created_at)
   VALUES ('원하는ID', SHA2('비밀번호', 256), '관리자', NOW());
   ```

3. **Admin 토큰 보안 강화**  
   현재는 로그인 시 발급된 토큰을 `X-Admin-Token` 헤더로 매 요청마다 전달하는 방식.  
   토큰 만료 시간 및 갱신 정책 미구현 상태 → 배포 전 결정 필요.

---

## 현재 구현된 기능

| 메뉴 | 기능 | 상태 |
|------|------|------|
| 손해사정사 관리 > 리스트 | 목록 조회 / 노출 여부 편집 / 정보 수정 | 완료 |
| 손해사정사 관리 > 보험금 상담 요청 내역 | 전체 상담 요청 목록 조회 (페이지네이션 미구현) | 완료 |
| 손해사정사 관리 > 교육 요청 내역 | 전체 교육 요청 목록 조회 (페이지네이션 미구현) | 완료 |
| 유저 타입 전환 | — | 미구현 |
| 푸시 알림 발송 | — | 미구현 |
| 강제 업데이트 | — | 미구현 |

---

## 앞으로 구현해야 할 것

### 기능
- [ ] **유저 타입 전환** — 일반 → FA, S급관리자 → 손해사정사 등 역할 변경 API 연동
- [ ] **푸시 알림 발송** — FCM 연동, 템플릿(이벤트/광고/사과) 기반 발송
- [ ] **강제 업데이트 관리** — iOS/Android 최소 버전 설정 및 강제 업데이트 ON/OFF
- [ ] **페이지네이션** — 상담/교육 요청 목록에 페이지 단위 로딩 추가
- [ ] **상태 변경** — 상담/교육 요청 상태(대기→수락→완료 등) 직접 변경 기능
- [ ] **손해사정사 등록** — 신규 등록 다이얼로그 (현재 편집만 가능)

### 인프라 / 배포
- [ ] **Admin 계정 Stage/Prod DB 세팅** (위 계정 관련 항목 참고)
- [ ] **AdminController Stage/Prod 배포** (`feat/LOCAL_TEST` → `main` 머지)
- [ ] **Web 정적 배포** — `wasmJsBrowserProductionWebpack` 결과물을 CDN 또는 S3에 업로드
- [ ] **HTTPS 적용** — 웹 배포 시 Mixed Content 문제 방지 (API 서버도 HTTPS여야 함)

### 기술 개선
- [ ] **토큰 만료/갱신 처리** — 현재 로컬 저장 후 무기한 사용 중
- [ ] **에러 핸들링 통일** — 현재 화면마다 개별 처리 중
- [ ] **로딩/빈 상태 UI 개선**

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
