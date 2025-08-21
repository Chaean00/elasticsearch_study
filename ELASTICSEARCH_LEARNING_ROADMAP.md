# Elasticsearch 학습 로드맵: Spring Data부터 심층 분석까지

이 로드맵은 점진적인 학습 경험을 위해 설계되었습니다. Spring Data Elasticsearch의 편리함으로 시작하여 Elasticsearch의 핵심 개념을 더 깊이 파고드는 방식으로 진행됩니다.

---

## 1단계: 시작하기 - Spring Data Elasticsearch 마스터하기 (1-4주차)

**목표**: 빠르게 시작하고 실행합니다. Spring Boot 생태계를 사용하여 기본 데이터 작업 및 검색을 수행하는 방법을 이해합니다.

### **1주차: 소개 및 첫 연결**
- **주제**:
    - Elasticsearch란 무엇이며 왜 사용하는가? (핵심 이점: 속도, 확장성, 전문 검색)
    - Elasticsearch 핵심 개념: `Index`, `Document`, `Mapping`, `Node`, `Cluster`.
    - `docker-compose.yml`을 사용하여 로컬에 Elasticsearch 및 Kibana 설정.
    - Spring Boot 애플리케이션과 Spring Data Elasticsearch 통합.
    - `ElasticsearchRepository`와 `ElasticsearchRestTemplate`의 차이점 이해.
- **실습**:
    - `docker-compose up`을 실행하여 Elasticsearch 및 Kibana 시작.
    - `application.yml`을 구성하여 Elasticsearch 인스턴스에 연결.
    - 간단한 `@Document` 엔티티 생성 (예: `Product`).
    - `ElasticsearchRepository`를 주입하고 애플리케이션이 성공적으로 연결되는지 확인.

### **2주차: 기본 CRUD 및 간단한 쿼리**
- **주제**:
    - Java 객체를 Elasticsearch 문서에 매핑하기 위한 `@Document` 및 `@Field` 어노테이션.
    - `ElasticsearchRepository`를 사용한 표준 CRUD: `save()`, `findById()`, `findAll()`, `delete()`.
    - 쿼리 메소드: Spring Data가 메소드 이름으로 쿼리를 생성하는 방법 (예: `findByName()`, `findByPriceGreaterThan()`).
    - 메소드 이름의 기본 논리 연산 (`And`, `Or`).
- **실습**:
    - `ElasticsearchRepository`를 상속하는 `ProductRepository` 구현.
    - `Product` 문서를 저장, 검색, 업데이트, 삭제하는 간단한 서비스 생성.
    - `Product` 엔티티의 다른 필드에 대한 여러 쿼리 메소드 작성.
    - Kibana의 Dev Tools를 사용하여 생성한 문서 검사.

### **3주차: 사용자 정의 및 복잡한 쿼리**
- **주제**:
    - 사용자 정의 JSON 기반 쿼리 작성을 위한 `@Query` 어노테이션.
    - Query DSL (Domain Specific Language) 소개.
    - 기본 쿼리 절: `match` (전문 검색용), `term` (정확한 값용), `range`.
    - `bool` 쿼리로 절 결합 (`must`, `should`, `filter`).
- **실습**:
    - 리포지토리 메소드 중 일부를 `@Query`를 사용하도록 리팩토링.
    - 텍스트 필드에 대한 `match` 쿼리와 가격 필드에 대한 `range` 쿼리를 결합한 검색 메소드 작성.
    - `bool` 쿼리에서 `must` (AND)와 `should` (OR)의 차이점 실험.

### **4주차: 집계(Aggregations) 소개**
- **주제**:
    - 집계란 무엇인가? (SQL의 `GROUP BY`와 유사하지만 더 강력함).
    - **메트릭 집계**: `min`, `max`, `avg`, `sum`, `stats`.
    - **버킷 집계**: `terms` (특정 필드 값으로 그룹화).
    - Spring Data Elasticsearch를 사용하여 집계를 요청하고 처리하는 방법.
- **실습**:
    - 모든 제품의 평균 가격을 찾는 쿼리 작성.
    - 제품을 `category`별로 그룹화하고 각 그룹에 몇 개가 있는지 계산하는 쿼리 구현 (`terms` 집계).
    - 검색 쿼리와 집계 결합.

---

## 2단계: 깊이 파고들기 - Elasticsearch 심층 분석 (5-8주차)

**목표**: 추상화를 넘어섭니다. Elasticsearch가 내부적으로 어떻게 작동하는지 이해하여 더 효율적이고 강력한 쿼리를 작성합니다.

### **5주차: 매핑 및 분석**
- **주제**:
    - **분석**: 텍스트를 토큰으로 변환하는 과정.
    - **분석기**: `standard`, `keyword`, `whitespace`.
    - **토크나이저 및 토큰 필터**: 분석기의 구성 요소.
    - **사용자 정의 분석기**: 자신만의 텍스트 처리 파이프라인 생성 (예: N-gram, 동의어).
    - **다중 필드**: 동일한 문자열을 여러 방식으로 인덱싱 (예: 전문 검색을 위한 `text` 및 정렬/집계를 위한 `keyword`).
- **실습**:
    - Kibana를 사용하여 `_analyze` API 실험.
    - Spring이 매핑을 생성하도록 두는 대신 `Product` 인덱스에 대한 사용자 정의 매핑 정의.
    - 텍스트 필드에 `.keyword` 다중 필드를 추가하고 `terms` 집계에 사용.

### **6주차: 고급 쿼리 및 스코어링**
- **주제**:
    - **전문 검색 심층 분석**: `match_phrase`, `multi_match`, `query_string`.
    - **관련성 및 스코어링**: Elasticsearch가 `_score`를 계산하는 방법과 그 중요성.
    - **복합 쿼리**: `bool` 쿼리 상세 분석 (`must_not`, `filter`).
    - 쿼리 컨텍스트(점수에 영향)와 필터 컨텍스트(점수에 영향 없음, 종종 더 빠름)의 차이점 이해.
- **실습**:
    - 정확한 구문을 찾는 검색 구현 (`match_phrase`).
    - `multi_match`를 사용하여 여러 텍스트 필드에서 동시에 검색.
    - `bool` 쿼리의 일부를 `filter` 절로 이동하고 성능/스코어링 영향 관찰.

### **7주차: 고급 집계**
- **주제**:
    - **중첩 집계**: 버킷 내의 버킷 (예: 카테고리별 그룹화 후 브랜드별 그룹화).
    - **파이프라인 집계**: 다른 집계의 출력을 집계.
    - **날짜 히스토그램 집계**: 시간 간격으로 문서 그룹화.
- **실습**:
    - 각 카테고리 내에서 브랜드별 평균 가격을 보여주는 집계 생성.
    - 매월 추가된 제품 수를 확인하기 위한 날짜 히스토그램 구현.

### **8주차: 성능 및 모범 사례**
- **주제**:
    - **인덱싱**: 효율적인 일괄 인덱싱을 위한 `_bulk` API 사용.
    - **쿼리 튜닝**: 느린 쿼리 식별 및 최적화.
    - **샤드 및 복제본**: 읽기/쓰기 확장성 및 내결함성에 미치는 영향 이해.
    - 대량 작업 및 복잡한 쿼리에 대한 더 많은 제어를 위해 `ElasticsearchRestTemplate`을 사용해야 하는 경우.
- **실습**:
    - `ElasticsearchRestTemplate`을 사용하여 다수의 제품에 대한 대량 인덱싱 메소드 구현.
    - 기존 쿼리를 검토하고 최적화할 수 있는지 확인 (예: 쿼리를 `filter` 컨텍스트로 이동).

---

## 3단계: 정상 - 프로덕션 준비 (9주차 이상)

**목표**: 프로덕션 환경에서 Elasticsearch를 실행하는 아키텍처 및 운영 측면을 이해합니다.

### **9주차: 아키텍처 및 클러스터 관리**
- **주제**:
    - 노드, 클러스터, 샤딩 및 복제본 심층 분석.
    - Kibana를 사용한 클러스터 상태 및 모니터링.
    - 기본 확장 전략.
- **실습**:
    - Kibana의 `_cat` API를 사용하여 클러스터 상태, 노드 및 인덱스 모니터링.

### **10주차: 고급 기능**
- **주제**:
    - **동의어**: 유사한 용어를 포함하도록 검색 확장.
    - **하이라이팅**: 텍스트에서 일치하는 부분을 사용자에게 표시.
    - **제안 기능**: "자동 완성" 또는 "이것을 찾으셨나요?" 기능 구현.
- **실습**:
    - 결과에 하이라이트된 스니펫을 포함하는 검색 엔드포인트 구현.

### **11주차: Spring Data를 넘어서**
- **주제**:
    - 공식 **Elasticsearch Java Client**.
    - 최대 제어를 위해 Spring Data를 우회해야 할 수 있는 시나리오.
    - Elastic Stack의 다른 부분(Logstash, Beats)과 통합.
- **실습**:
    - 복잡한 쿼리 하나를 `RestTemplate` 대신 공식 Java 클라이언트를 사용하도록 리팩토링.

### **12주차: 최종 프로젝트 및 검토**
- **주제**:
    - 배운 많은 개념을 사용하는 작은 완전한 애플리케이션 구축.
    - 예: 제품 검색 API, 로그 분석 대시보드, 문서 검색 엔진.
- **실습**_**:**_ 
    - 프로젝트 설계 및 구현.
    - 깨끗하고 효율적이며 잘 문서화된 코드 작성에 집중.
