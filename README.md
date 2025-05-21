# Bonda Android

## Information
1. Android Studio Meerkat | 2024.3.1 Patch 1 버전으로 작업

## 폴더 구조
```
.
├── .idea    # AndroidStudio 옵션
├── .kotlin    # Error log
├── app
│   └── src
│       ├── androidTest
│       ├── main
│       │   ├── java    # 소스 코드
│       │   │   └── com
│       │   │       └── bonda
│       │   │           └── bonda
│       │   │               └── ui
│       │   └── res    # 리소스
│       │       ├── drawable
│       │       ├── layout
│       │       ├── menu
│       │       ├── mipmap
│       │       ├── navigation
│       │       ├── values
│       │       ├── values-night
│       │       └── xml
│       └── test
└── gradle    # 종속성 관리
```

## 깃 전략
### 브랜치 분류
```
main: 운영용
develop: 개발용
feat/{제목}: 기능 개발 브랜치
hotfix/{제목}: 핫픽스 브랜치
```
- `제목`은 케밥케이스 사용

### 커밋 메시지 컨벤션
```
{분류}. {제목}

- {내용1}
- {내용2}
```
- 내용은 필요한 경우에 작성
- `분류`는 아래 [태스크 분류](#태스크-분류) 참고

#### 태스크 분류
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
refactor: 코드 리펙토링
test: 테스트 코드, 리펙토링 테스트 코드 추가
chore: 빌드 업무 수정, 패키지 매니저 수정 등
```
