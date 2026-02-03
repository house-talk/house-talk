# HOUSE-TALK Frontend

자취 건물 관리자를 위한 세대·세입자 관리 서비스 **HOUSE-TALK**의 프론트엔드 애플리케이션입니다.  
관리자 웹과 세입자 화면을 React 기반으로 제공합니다.

---

## Overview

HOUSE-TALK Frontend는 관리자와 세입자가 사용하는 웹 UI를 담당합니다.  
관리자는 건물과 세대를 관리하고, 세입자는 초대코드를 통해 입장 및 승인 과정을 진행합니다.!

---

## Main Pages

### 관리자
- 로그인 / 인증
- 건물 목록 및 상세 조회
- 세대(Unit) 관리
- 세입자 승인 / 거절

### 세입자
- 초대코드 입력
- 승인 상태 확인
- 세대 정보 조회

---

## Tech Stack

- React
- Vite
- JavaScript (ES6+)
- Fetch API
- CSS

---

## Environment Variables

```env
VITE_API_BASE_URL=http://localhost:8080

test