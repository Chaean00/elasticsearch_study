-- 기존 데이터를 모두 삭제합니다.
TRUNCATE TABLE products RESTART IDENTITY;

-- 데이터 생성을 위한 단어 목록 및 변수 정의
WITH vars AS (
    SELECT
        '{삼성,LG,애플,한성,DELL,HP,레노버,ASUS,MSI}'::text[] as brands,
        '{노트북,스마트폰,모니터,키보드,마우스,태블릿,PC,이어폰,헤드셋,프린터}'::text[] as products,
        '{고성능,가성비,최신,전문가용,보급형,슬림,기계식,무선,게이밍,사무용}'::text[] as adjectives,
        '{2025년형 신제품,강력한 퍼포먼스,디자이너를 위한 최고의 선택,사무용 추천,가벼운 휴대성,놀라운 가격,특별 할인,한정판 에디션}'::text[] as descriptions
)

-- 실제와 유사한 데이터 100만 건 삽입
INSERT INTO products (name, price, category)
SELECT
    -- 1. 각 행마다 다른 조합의 상품명 생성
    v.brands[1 + floor(random() * array_length(v.brands, 1))] || ' ' ||
    v.products[1 + floor(random() * array_length(v.products, 1))] || ' ' ||
    v.adjectives[1 + floor(random() * array_length(v.adjectives, 1))] || ' (' ||
    v.descriptions[1 + floor(random() * array_length(v.descriptions, 1))] || ')',

    -- 2. 각 행마다 다른 랜덤 가격 생성
    (10000 + random() * 2990000)::int,

    -- 3. 각 행마다 다른 랜덤 카테고리 생성
    'CAT-' || (1 + floor(random() * 5000))::int
FROM
    generate_series(1, 1000000) g, vars v;
