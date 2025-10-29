# 보안 가이드

## 🔒 민감한 정보 관리

### 환경변수로 관리해야 하는 정보
- 데이터베이스 연결 정보 (URI, 사용자명, 비밀번호)
- API 키 및 토큰 (Databricks, Elasticsearch 등)
- 암호화 키
- 외부 서비스 인증 정보

### 환경변수 설정 방법

1. `.env` 파일 생성 (`.env.example` 참고)
```bash
cp .env.example .env
```

2. 실제 값으로 환경변수 설정
```bash
# 데이터베이스 설정
DATABASE_URL=jdbc:postgresql://your-host:5432/your-database
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
DATABASE_CONNECTION_STRING=postgresql://username:password@host:5432/database?sslmode=require

# Databricks 설정
DATABRICKS_HOST=your-databricks-host
DATABRICKS_TOKEN=your-databricks-token
DATABRICKS_WAREHOUSE_ID=your-warehouse-id

# Elasticsearch 설정
ELASTICSEARCH_HOST=your-elasticsearch-host
ELASTICSEARCH_USERNAME=your-username
ELASTICSEARCH_PASSWORD=your-password
```

## ⚠️ Git 히스토리 정리

민감한 정보가 이미 Git 히스토리에 포함된 경우, 다음 방법으로 제거할 수 있습니다:

### BFG Repo-Cleaner 사용 (권장)
```bash
# BFG 다운로드
wget https://repo1.maven.org/maven2/com/madgag/bfg/1.14.0/bfg-1.14.0.jar

# 민감한 정보가 포함된 파일 정리
java -jar bfg-1.14.0.jar --delete-files "*.properties" --delete-files "application-*.yml"

# 히스토리 정리 적용
git reflog expire --expire=now --all && git gc --prune=now --aggressive
```

### Git Filter-Branch 사용
```bash
# 특정 파일을 히스토리에서 완전 제거
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch database/README.md' \
  --prune-empty --tag-name-filter cat -- --all

# 원격 저장소에 강제 푸시 (주의: 협업 시 팀원과 협의 필요)
git push origin --force --all
```

## 🛡️ 추가 보안 조치

1. **GitHub Secrets 사용**: CI/CD 파이프라인에서 환경변수 관리
2. **Vault 시스템**: 프로덕션 환경에서 HashiCorp Vault 등 사용
3. **정기적인 토큰 로테이션**: API 키와 토큰을 주기적으로 갱신
4. **접근 권한 최소화**: 필요한 최소한의 권한만 부여

## 📋 체크리스트

- [ ] `.env` 파일이 `.gitignore`에 포함되어 있는가?
- [ ] 모든 민감한 정보가 환경변수로 관리되는가?
- [ ] Git 히스토리에서 민감한 정보가 제거되었는가?
- [ ] 프로덕션 환경에서 안전한 비밀 관리 시스템을 사용하는가?
- [ ] 팀원들이 보안 가이드를 숙지했는가?