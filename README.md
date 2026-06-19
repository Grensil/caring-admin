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
├── storage/AdminStorage.kt         # 로컬 저장소 인터페이스
├── di/AdminCommonModule.kt         # Koin 공통 모듈
├── viewmodel/
│   ├── LoginViewModel.kt
│   ├── AdjusterListViewModel.kt
│   ├── ConsultingRequestViewModel.kt
│   └── EducationRequestViewModel.kt
└── screen/
    ├── LoginScreen.kt
    ├── MainScreen.kt               # 사이드바 + 콘텐츠 레이아웃
    ├── PlaceholderScreen.kt        # 미구현 메뉴 공통 화면
    ├── AdjusterListScreen.kt       # 손해사정사 목록 / 편집
    ├── AdjusterEditDialog.kt       # 손해사정사 편집 다이얼로그
    ├── ConsultingRequestScreen.kt  # 보험금 상담 요청 내역
    └── EducationRequestScreen.kt   # 교육 요청 내역
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

## 현재 구현 현황

### 일반 회원관리
| 서브메뉴 | 상태 |
|----------|------|
| 회원 목록 | 미구현 |
| 회원 상세 | 미구현 |
| 제재 관리 | 미구현 |

### FA(일반) 관리
| 서브메뉴 | 상태 |
|----------|------|
| FA 목록 | 미구현 |
| 가입 승인 | 미구현 |
| 활동 내역 | 미구현 |

### FA(S급) 관리
| 서브메뉴 | 상태 |
|----------|------|
| S급 FA 목록 | 미구현 |
| S급 승인 | 미구현 |
| 활동 내역 | 미구현 |

### 손해사정사 관리
| 서브메뉴 | 기능 | 상태 |
|----------|------|------|
| 손해사정사 리스트 | 목록 조회 / 노출 여부 편집 / 정보 수정 | **완료** |
| 보험금 상담 요청 내역 | 전체 목록 조회 | **완료** |
| 교육 요청 내역 | 전체 목록 조회 | **완료** |

### 카포스 점주 관리
| 서브메뉴 | 상태 |
|----------|------|
| 점주 목록 | 미구현 |
| 가맹점 관리 | 미구현 |
| 정산 내역 | 미구현 |

---

## 앞으로 할 것

### 기능
- [ ] 일반 회원 / FA / S급 FA / 카포스 점주 관리 화면 구현
- [ ] 손해사정사 신규 등록 (현재 편집만 가능)
- [ ] 상담/교육 요청 페이지네이션
- [ ] 상담/교육 요청 상태 변경 (대기 → 수락 → 완료 등)

### 배포 전 필수 작업
- [ ] `caring-web-flatform` `feat/LOCAL_TEST` → `main` 머지 및 Stage/Prod 배포
- [ ] Stage / Prod 서버 `.env`에 어드민 계정 추가

  ```
  ADMIN_LOGIN_ID = {원하는ID}
  ADMIN_LOGIN_PW = {원하는비밀번호}
  ```

  > 어드민 계정은 DB 테이블이 아닌 `.env` 환경변수로 관리된다. (단일 계정)

- [ ] 토큰 만료/갱신 정책 결정 (현재 무기한)

---

## 어드민 계정 관리

### 현재 방식 (로컬/Dev) — `.env` 환경변수

단일 계정을 `.env`에 하드코딩하는 방식. 로컬 개발용으로만 사용한다.

```
# caring-web-flatform/.env dev
ADMIN_LOGIN_ID = caring-admin
ADMIN_LOGIN_PW = 비밀번호
```

```bash
# 로컬 서버 실행
docker compose -f docker/local/docker-compose.yml up -d
```

### Stage / Prod 방식 — DB 테이블

`.env` 방식은 계정이 하나뿐이고, 비밀번호 변경 시 재배포가 필요하며, 로그인 이력을 남길 수 없다.  
Stage/Prod에서는 `admin_users` DB 테이블로 관리한다.

**변경 내용** (`caring-web-flatform`):
- `admin_users` 테이블 마이그레이션 추가
- `AdminController::login()` — `.env` 비교 → DB 조회 + bcrypt 검증으로 변경
- Seeder로 초기 계정 생성

**초기 계정 생성** (마이그레이션 실행 후):
```bash
php spark db:seed AdminUserSeeder
```

**추가 계정은 DB에 직접 INSERT**:
```sql
INSERT INTO admin_users (login_id, password_hash, name)
VALUES ('새계정', '$2y$...bcrypt해시...', '담당자명');
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
