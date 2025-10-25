# راهنمای مهاجرت به Elasticsearch - سیستم جستجوی پرشیا بازار

## مقدمه

این مستند راهنمای کاملی برای مهاجرت از PostgreSQL Full-Text Search به Elasticsearch ارائه می‌دهد. این مهاجرت زمانی توصیه می‌شود که حجم داده‌ها یا تعداد کاربران به حدی برسد که PostgreSQL قادر به پاسخگویی مطلوب نباشد.

## زمان مناسب مهاجرت

### شاخص‌های کلیدی

مهاجرت به Elasticsearch در شرایط زیر توصیه می‌شود:

1. **حجم داده‌ها**: بیش از 10 میلیون رکورد
2. **تعداد جستجو**: بیش از 1000 جستجو در ثانیه
3. **زمان پاسخ**: بیش از 2 ثانیه برای جستجوهای پیچیده
4. **نیاز به ویژگی‌های پیشرفته**: جستجوی faceted، aggregation پیچیده، جستجوی جغرافیایی

### ارزیابی فعلی

```sql
-- بررسی حجم داده‌ها
SELECT 
    'ads' as table_name,
    COUNT(*) as record_count,
    pg_size_pretty(pg_total_relation_size('ads')) as table_size
FROM ads
UNION ALL
SELECT 
    'products' as table_name,
    COUNT(*) as record_count,
    pg_size_pretty(pg_total_relation_size('products')) as table_size
FROM products;

-- بررسی عملکرد جستجو
SELECT 
    query,
    calls,
    mean_exec_time,
    max_exec_time
FROM pg_stat_statements 
WHERE query LIKE '%search%'
ORDER BY mean_exec_time DESC;
```

## آمادگی پیش از مهاجرت

### 1. نصب و راه‌اندازی Elasticsearch

```bash
# دانلود و نصب Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.11.0-linux-x86_64.tar.gz
cd elasticsearch-8.11.0/

# تنظیمات پایه
echo "cluster.name: iran-bazaar-search" >> config/elasticsearch.yml
echo "node.name: node-1" >> config/elasticsearch.yml
echo "network.host: localhost" >> config/elasticsearch.yml
echo "http.port: 9200" >> config/elasticsearch.yml
echo "discovery.type: single-node" >> config/elasticsearch.yml

# شروع سرویس
./bin/elasticsearch
```

### 2. تنظیمات کانفیگوریشن

```properties
# application-elasticsearch.properties

# Elasticsearch Configuration
app.search.engine=elasticsearch
app.search.elasticsearch.host=localhost
app.search.elasticsearch.port=9200
app.search.elasticsearch.ssl=false
app.search.elasticsearch.timeout=30s

# Index Settings
app.search.elasticsearch.ads-index=iran_bazaar_ads
app.search.elasticsearch.products-index=iran_bazaar_products
app.search.elasticsearch.refresh-interval=5s
app.search.elasticsearch.number-of-shards=3
app.search.elasticsearch.number-of-replicas=1

# Migration Settings
app.search.migration.batch-size=1000
app.search.migration.parallel-threads=4
app.search.migration.verify-data=true
```

## مرحله 1: ایجاد Index Template ها

### Ad Index Template

```json
{
  "index_patterns": ["iran_bazaar_ads*"],
  "template": {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "5s",
      "analysis": {
        "analyzer": {
          "persian_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": [
              "lowercase",
              "persian_normalization",
              "persian_stop",
              "persian_stemmer"
            ]
          },
          "persian_search_analyzer": {
            "type": "custom", 
            "tokenizer": "standard",
            "filter": [
              "lowercase",
              "persian_normalization"
            ]
          }
        },
        "filter": {
          "persian_stop": {
            "type": "stop",
            "stopwords": "_persian_"
          },
          "persian_stemmer": {
            "type": "stemmer",
            "language": "persian"
          },
          "persian_normalization": {
            "type": "persian_normalization"
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "id": {
          "type": "long"
        },
        "title": {
          "type": "text",
          "analyzer": "persian_analyzer",
          "search_analyzer": "persian_search_analyzer",
          "fields": {
            "keyword": {
              "type": "keyword"
            },
            "suggest": {
              "type": "completion",
              "analyzer": "persian_analyzer"
            }
          }
        },
        "description": {
          "type": "text",
          "analyzer": "persian_analyzer",
          "search_analyzer": "persian_search_analyzer"
        },
        "price": {
          "type": "scaled_float",
          "scaling_factor": 100
        },
        "currency": {
          "type": "keyword"
        },
        "status": {
          "type": "keyword"
        },
        "category": {
          "type": "object",
          "properties": {
            "id": {"type": "long"},
            "name": {
              "type": "text",
              "analyzer": "persian_analyzer",
              "fields": {
                "keyword": {"type": "keyword"}
              }
            },
            "path": {"type": "keyword"}
          }
        },
        "location": {
          "type": "object",
          "properties": {
            "id": {"type": "long"},
            "city": {
              "type": "text",
              "analyzer": "persian_analyzer",
              "fields": {
                "keyword": {"type": "keyword"}
              }
            },
            "region": {
              "type": "text", 
              "analyzer": "persian_analyzer",
              "fields": {
                "keyword": {"type": "keyword"}
              }
            },
            "coordinates": {
              "type": "geo_point"
            }
          }
        },
        "user": {
          "type": "object",
          "properties": {
            "id": {"type": "long"},
            "username": {"type": "keyword"},
            "isVerified": {"type": "boolean"}
          }
        },
        "tags": {
          "type": "keyword"
        },
        "attributes": {
          "type": "object",
          "dynamic": true
        },
        "images": {
          "type": "keyword"
        },
        "viewCount": {
          "type": "integer"
        },
        "favoriteCount": {
          "type": "integer"
        },
        "isFeatured": {
          "type": "boolean"
        },
        "isUrgent": {
          "type": "boolean"
        },
        "createdAt": {
          "type": "date"
        },
        "updatedAt": {
          "type": "date"
        },
        "boost": {
          "type": "float"
        }
      }
    }
  }
}
```

### Product Index Template

```json
{
  "index_patterns": ["iran_bazaar_products*"],
  "template": {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "5s",
      "analysis": {
        "analyzer": {
          "persian_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": [
              "lowercase",
              "persian_normalization",
              "persian_stop",
              "persian_stemmer"
            ]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "id": {
          "type": "long"
        },
        "name": {
          "type": "text",
          "analyzer": "persian_analyzer",
          "fields": {
            "keyword": {"type": "keyword"},
            "suggest": {
              "type": "completion",
              "analyzer": "persian_analyzer"
            }
          }
        },
        "description": {
          "type": "text",
          "analyzer": "persian_analyzer"
        },
        "brand": {
          "type": "keyword"
        },
        "price": {
          "type": "scaled_float",
          "scaling_factor": 100
        },
        "discountPrice": {
          "type": "scaled_float",
          "scaling_factor": 100
        },
        "currency": {
          "type": "keyword"
        },
        "status": {
          "type": "keyword"
        },
        "stockQuantity": {
          "type": "integer"
        },
        "category": {
          "type": "object",
          "properties": {
            "id": {"type": "long"},
            "name": {
              "type": "text",
              "analyzer": "persian_analyzer",
              "fields": {"keyword": {"type": "keyword"}}
            }
          }
        },
        "specifications": {
          "type": "object",
          "dynamic": true
        },
        "tags": {
          "type": "keyword"
        },
        "images": {
          "type": "keyword"
        },
        "salesCount": {
          "type": "integer"
        },
        "rating": {
          "type": "float"
        },
        "reviewCount": {
          "type": "integer"
        },
        "isFeatured": {
          "type": "boolean"
        },
        "isOnSale": {
          "type": "boolean"
        },
        "isInStock": {
          "type": "boolean"
        },
        "isBestseller": {
          "type": "boolean"
        },
        "createdAt": {
          "type": "date"
        },
        "updatedAt": {
          "type": "date"
        }
      }
    }
  }
}
```

## مرحله 2: ایجاد کلاس‌های Elasticsearch

### ElasticsearchAdRepository

```java
@Repository
public class ElasticsearchAdRepository {
    
    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.search.elasticsearch.ads-index}")
    private String adsIndex;
    
    public ElasticsearchAdRepository(ElasticsearchClient elasticsearchClient, 
                                   ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }
    
    public SearchResponse<AdDocument> search(SearchCriteria criteria) throws IOException {
        
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        
        // Full-text search
        if (criteria.hasKeyword()) {
            boolQuery.must(MultiMatchQuery.of(m -> m
                .query(criteria.getKeyword())
                .fields("title^3", "description^2", "tags^1")
                .type(TextQueryType.BestFields)
                .operator(Operator.And)
                .fuzziness("AUTO")
            )._toQuery());
        }
        
        // Filters
        if (criteria.hasCategoryFilter()) {
            boolQuery.filter(TermQuery.of(t -> t
                .field("category.id")
                .value(criteria.getCategoryId())
            )._toQuery());
        }
        
        if (criteria.hasLocationFilter()) {
            boolQuery.filter(TermQuery.of(t -> t
                .field("location.id")
                .value(criteria.getLocationId())
            )._toQuery());
        }
        
        if (criteria.hasPriceRange()) {
            RangeQuery.Builder rangeBuilder = new RangeQuery.Builder()
                .field("price");
                
            if (criteria.getMinPrice() != null) {
                rangeBuilder.gte(JsonData.of(criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                rangeBuilder.lte(JsonData.of(criteria.getMaxPrice()));
            }
            
            boolQuery.filter(rangeBuilder.build()._toQuery());
        }
        
        if (criteria.getActiveOnly()) {
            boolQuery.filter(TermQuery.of(t -> t
                .field("status")
                .value("ACTIVE")
            )._toQuery());
        }
        
        if (criteria.getFeaturedOnly()) {
            boolQuery.filter(TermQuery.of(t -> t
                .field("isFeatured")
                .value(true)
            )._toQuery());
        }
        
        // Date filter
        if (criteria.hasDateFilter()) {
            LocalDateTime fromDate = LocalDateTime.now().minusDays(criteria.getDaysBack());
            boolQuery.filter(RangeQuery.of(r -> r
                .field("createdAt")
                .gte(JsonData.of(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            )._toQuery());
        }
        
        // Sorting
        List<SortOptions> sorts = buildSortOptions(criteria.getSortBy());
        
        // Build search request
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(adsIndex)
            .query(boolQuery.build()._toQuery())
            .sort(sorts)
            .from(criteria.getPage() * criteria.getSize())
            .size(criteria.getSize())
            .trackTotalHits(TrackHits.of(th -> th.enabled(true)))
        );
        
        return elasticsearchClient.search(searchRequest, AdDocument.class);
    }
    
    public List<String> suggest(String keyword, int size) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
            .index(adsIndex)
            .suggest(suggest -> suggest
                .suggesters("title_suggest", fieldSuggest -> fieldSuggest
                    .completion(completion -> completion
                        .field("title.suggest")
                        .prefix(keyword)
                        .size(size)
                    )
                )
            )
            .size(0)
        );
        
        SearchResponse<AdDocument> response = elasticsearchClient.search(request, AdDocument.class);
        
        return response.suggest()
            .get("title_suggest")
            .stream()
            .flatMap(suggestion -> suggestion.completion().options().stream())
            .map(option -> option.text())
            .collect(Collectors.toList());
    }
    
    private List<SortOptions> buildSortOptions(SortType sortBy) {
        return switch (sortBy) {
            case RELEVANCE -> List.of(SortOptions.of(s -> s.score(ScoreSort.of(sc -> sc.order(SortOrder.Desc)))));
            case PRICE_ASC -> List.of(SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("price").order(SortOrder.Asc)))));
            case PRICE_DESC -> List.of(SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("price").order(SortOrder.Desc)))));
            case DATE_ASC -> List.of(SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("createdAt").order(SortOrder.Asc)))));
            case DATE_DESC -> List.of(SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("createdAt").order(SortOrder.Desc)))));
            case POPULARITY -> List.of(
                SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("viewCount").order(SortOrder.Desc)))),
                SortOptions.of(s -> s.field(FieldSort.of(f -> f.field("favoriteCount").order(SortOrder.Desc))))
            );
        };
    }
}
```

### ElasticsearchSearchService

```java
@Service
@Profile("elasticsearch")
@Slf4j
public class ElasticsearchSearchServiceImpl implements SearchService {
    
    private final ElasticsearchAdRepository adRepository;
    private final ElasticsearchProductRepository productRepository;
    private final SearchAnalyticsService analyticsService;
    
    @Override
    public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
        log.info("جستجوی آگهی‌ها در Elasticsearch: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        
        try {
            SearchResponse<AdDocument> response = adRepository.search(criteria);
            
            List<AdSearchResult> results = response.hits().hits().stream()
                .map(hit -> convertToAdSearchResult(hit.source(), hit.score()))
                .collect(Collectors.toList());
            
            long totalCount = response.hits().total().value();
            
            SearchResultPage<AdSearchResult> resultPage = new SearchResultPage<>(
                results,
                totalCount,
                criteria.getPage(),
                criteria.getSize()
            );
            
            long searchTime = System.currentTimeMillis() - startTime;
            resultPage.setSearchTimeMs(searchTime);
            
            // Add aggregations if available
            if (response.aggregations() != null) {
                Map<String, Object> aggregations = extractAggregations(response.aggregations());
                resultPage.setAggregations(aggregations);
            }
            
            analyticsService.recordSearch(criteria, results.size(), searchTime);
            
            log.info("جستجوی Elasticsearch کامل شد. {} نتیجه در {} میلی‌ثانیه", 
                results.size(), searchTime);
            
            return resultPage;
            
        } catch (IOException e) {
            log.error("خطا در جستجوی Elasticsearch: {}", e.getMessage(), e);
            throw new SearchException("خطا در اجرای جستجو", e);
        }
    }
    
    @Override
    public List<String> getSearchSuggestions(String keyword, SearchType type) {
        try {
            if (type == SearchType.ADS || type == SearchType.ALL) {
                return adRepository.suggest(keyword, 10);
            } else {
                return productRepository.suggest(keyword, 10);
            }
        } catch (IOException e) {
            log.error("خطا در دریافت پیشنهادات: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private AdSearchResult convertToAdSearchResult(AdDocument doc, Double score) {
        return AdSearchResult.builder()
            .id(doc.getId())
            .title(doc.getTitle())
            .description(doc.getDescription())
            .price(doc.getPrice())
            .currency(doc.getCurrency())
            .status(doc.getStatus())
            .createdAt(doc.getCreatedAt())
            .updatedAt(doc.getUpdatedAt())
            .categoryId(doc.getCategory().getId())
            .categoryName(doc.getCategory().getName())
            .locationId(doc.getLocation().getId())
            .locationName(doc.getLocation().getCity())
            .userId(doc.getUser().getId())
            .userName(doc.getUser().getUsername())
            .viewCount(doc.getViewCount())
            .favoriteCount(doc.getFavoriteCount())
            .images(doc.getImages())
            .tags(doc.getTags())
            .attributes(doc.getAttributes())
            .relevanceScore(score)
            .isFeatured(doc.getIsFeatured())
            .isUrgent(doc.getIsUrgent())
            .isVerified(doc.getUser().getIsVerified())
            .build();
    }
    
    private Map<String, Object> extractAggregations(Map<String, Aggregate> aggregations) {
        Map<String, Object> result = new HashMap<>();
        
        aggregations.forEach((key, aggregate) -> {
            if (aggregate.isSterms()) {
                List<Map<String, Object>> buckets = aggregate.sterms().buckets().array()
                    .stream()
                    .map(bucket -> Map.of(
                        "key", bucket.key(),
                        "docCount", bucket.docCount()
                    ))
                    .collect(Collectors.toList());
                result.put(key, buckets);
            }
        });
        
        return result;
    }
}
```

## مرحله 3: Data Migration

### MigrationService

```java
@Service
@Slf4j
public class ElasticsearchMigrationService {
    
    private final AdRepository adRepository;
    private final ProductRepository productRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.search.migration.batch-size:1000}")
    private Integer batchSize;
    
    @Value("${app.search.migration.parallel-threads:4}")
    private Integer parallelThreads;
    
    @Value("${app.search.elasticsearch.ads-index}")
    private String adsIndex;
    
    @Value("${app.search.elasticsearch.products-index}")
    private String productsIndex;
    
    @Async
    public CompletableFuture<MigrationResult> migrateAds() {
        log.info("شروع مهاجرت آگهی‌ها به Elasticsearch");
        
        long startTime = System.currentTimeMillis();
        long totalCount = adRepository.count();
        AtomicLong processedCount = new AtomicLong(0);
        AtomicLong errorCount = new AtomicLong(0);
        
        try {
            // ایجاد index
            createAdsIndex();
            
            // مهاجرت به صورت batch
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (int page = 0; page * batchSize < totalCount; page++) {
                final int currentPage = page;
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        Pageable pageable = PageRequest.of(currentPage, batchSize);
                        Page<Ad> ads = adRepository.findAll(pageable);
                        
                        List<AdDocument> documents = ads.getContent().stream()
                            .map(this::convertToAdDocument)
                            .collect(Collectors.toList());
                        
                        bulkIndexAds(documents);
                        
                        processedCount.addAndGet(documents.size());
                        log.info("مهاجرت {} آگهی کامل شد. کل: {}/{}", 
                            documents.size(), processedCount.get(), totalCount);
                        
                    } catch (Exception e) {
                        log.error("خطا در مهاجرت batch {}: {}", currentPage, e.getMessage(), e);
                        errorCount.incrementAndGet();
                    }
                });
                
                futures.add(future);
                
                // محدود کردن تعداد thread های همزمان
                if (futures.size() >= parallelThreads) {
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    futures.clear();
                }
            }
            
            // منتظر ماندن برای تکمیل همه task ها
            if (!futures.isEmpty()) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
            
            // Refresh index
            refreshIndex(adsIndex);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            MigrationResult result = MigrationResult.builder()
                .totalRecords(totalCount)
                .processedRecords(processedCount.get())
                .errorCount(errorCount.get())
                .durationMs(duration)
                .success(errorCount.get() == 0)
                .build();
            
            log.info("مهاجرت آگهی‌ها کامل شد: {}", result);
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("خطا در مهاجرت آگهی‌ها: {}", e.getMessage(), e);
            throw new MigrationException("خطا در مهاجرت", e);
        }
    }
    
    private void createAdsIndex() throws IOException {
        if (!indexExists(adsIndex)) {
            // خواندن template از فایل
            String templateJson = loadTemplateFromFile("ads-index-template.json");
            
            CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(adsIndex)
                .withJson(new StringReader(templateJson))
            );
            
            elasticsearchClient.indices().create(request);
            log.info("Index {} ایجاد شد", adsIndex);
        }
    }
    
    private void bulkIndexAds(List<AdDocument> documents) throws IOException {
        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
        
        for (AdDocument doc : documents) {
            bulkBuilder.operations(op -> op
                .index(idx -> idx
                    .index(adsIndex)
                    .id(doc.getId().toString())
                    .document(doc)
                )
            );
        }
        
        BulkResponse response = elasticsearchClient.bulk(bulkBuilder.build());
        
        if (response.errors()) {
            log.warn("برخی سند ها با خطا مواجه شدند در bulk indexing");
            response.items().forEach(item -> {
                if (item.error() != null) {
                    log.error("خطا در indexing سند {}: {}", 
                        item.id(), item.error().reason());
                }
            });
        }
    }
    
    private AdDocument convertToAdDocument(Ad ad) {
        AdDocument.AdDocumentBuilder builder = AdDocument.builder()
            .id(ad.getId())
            .title(ad.getTitle())
            .description(ad.getDescription())
            .price(ad.getPrice())
            .currency(ad.getCurrency())
            .status(ad.getStatus().name())
            .viewCount(ad.getViewCount())
            .favoriteCount(ad.getFavoriteCount())
            .isFeatured(ad.getIsFeatured())
            .isUrgent(ad.getIsUrgent())
            .createdAt(ad.getCreatedAt())
            .updatedAt(ad.getUpdatedAt());
        
        // Category
        if (ad.getCategory() != null) {
            builder.category(AdDocument.CategoryInfo.builder()
                .id(ad.getCategory().getId())
                .name(ad.getCategory().getName())
                .build());
        }
        
        // Location
        if (ad.getLocation() != null) {
            AdDocument.LocationInfo.Builder locationBuilder = AdDocument.LocationInfo.builder()
                .id(ad.getLocation().getId().longValue())
                .city(ad.getLocation().getCity())
                .region(ad.getLocation().getRegion());
            
            // Add coordinates if available
            if (ad.getLocation().getLatitude() != null && ad.getLocation().getLongitude() != null) {
                locationBuilder.coordinates(Map.of(
                    "lat", ad.getLocation().getLatitude(),
                    "lon", ad.getLocation().getLongitude()
                ));
            }
            
            builder.location(locationBuilder.build());
        }
        
        // User
        if (ad.getUser() != null) {
            builder.user(AdDocument.UserInfo.builder()
                .id(ad.getUser().getId())
                .username(ad.getUser().getUsername())
                .isVerified(ad.getUser().getIsVerified())
                .build());
        }
        
        // Tags
        if (ad.getAdTags() != null) {
            List<String> tags = ad.getAdTags().stream()
                .map(adTag -> adTag.getTag().getName())
                .collect(Collectors.toList());
            builder.tags(tags);
        }
        
        // Images
        if (ad.getAdImages() != null) {
            List<String> images = ad.getAdImages().stream()
                .map(adImage -> adImage.getImageUrl())
                .collect(Collectors.toList());
            builder.images(images);
        }
        
        // Attributes
        if (ad.getAdAttributeValues() != null) {
            Map<String, Object> attributes = ad.getAdAttributeValues().stream()
                .collect(Collectors.toMap(
                    attr -> attr.getAttribute().getName(),
                    attr -> attr.getValue()
                ));
            builder.attributes(attributes);
        }
        
        return builder.build();
    }
    
    private boolean indexExists(String indexName) throws IOException {
        ExistsRequest request = ExistsRequest.of(e -> e.index(indexName));
        return elasticsearchClient.indices().exists(request).value();
    }
    
    private void refreshIndex(String indexName) throws IOException {
        RefreshRequest request = RefreshRequest.of(r -> r.index(indexName));
        elasticsearchClient.indices().refresh(request);
    }
    
    private String loadTemplateFromFile(String filename) throws IOException {
        return Files.readString(Paths.get("src/main/resources/elasticsearch/" + filename));
    }
}
```

## مرحله 4: تست و اعتبارسنجی

### MigrationVerificationService

```java
@Service
@Slf4j
public class MigrationVerificationService {
    
    private final AdRepository adRepository;
    private final ProductRepository productRepository;
    private final ElasticsearchClient elasticsearchClient;
    
    @Value("${app.search.elasticsearch.ads-index}")
    private String adsIndex;
    
    @Value("${app.search.elasticsearch.products-index}")
    private String productsIndex;
    
    public VerificationResult verifyAdsMigration() {
        log.info("شروع اعتبارسنجی مهاجرت آگهی‌ها");
        
        try {
            // مقایسه تعداد کل
            long dbCount = adRepository.count();
            long esCount = getElasticsearchCount(adsIndex);
            
            VerificationResult result = VerificationResult.builder()
                .databaseCount(dbCount)
                .elasticsearchCount(esCount)
                .countMatch(dbCount == esCount)
                .build();
            
            if (!result.isCountMatch()) {
                log.warn("تعداد رکوردها مطابقت ندارد. DB: {}, ES: {}", dbCount, esCount);
                return result;
            }
            
            // تست نمونه رکوردها
            List<Long> sampleIds = adRepository.findRandomIds(100);
            int matchedRecords = 0;
            
            for (Long id : sampleIds) {
                if (verifyAdRecord(id)) {
                    matchedRecords++;
                }
            }
            
            result.setSampleVerificationRate((double) matchedRecords / sampleIds.size());
            result.setVerificationPassed(result.getSampleVerificationRate() > 0.95);
            
            log.info("اعتبارسنجی مهاجرت: {}", result);
            return result;
            
        } catch (Exception e) {
            log.error("خطا در اعتبارسنجی: {}", e.getMessage(), e);
            return VerificationResult.builder()
                .verificationPassed(false)
                .error(e.getMessage())
                .build();
        }
    }
    
    private boolean verifyAdRecord(Long id) {
        try {
            Optional<Ad> dbAd = adRepository.findById(id);
            if (dbAd.isEmpty()) {
                return false;
            }
            
            GetRequest request = GetRequest.of(g -> g
                .index(adsIndex)
                .id(id.toString())
            );
            
            GetResponse<AdDocument> response = elasticsearchClient.get(request, AdDocument.class);
            
            if (!response.found()) {
                log.warn("رکورد {} در Elasticsearch یافت نشد", id);
                return false;
            }
            
            AdDocument esDoc = response.source();
            Ad dbRecord = dbAd.get();
            
            // مقایسه فیلدهای کلیدی
            return Objects.equals(esDoc.getTitle(), dbRecord.getTitle()) &&
                   Objects.equals(esDoc.getPrice(), dbRecord.getPrice()) &&
                   Objects.equals(esDoc.getStatus(), dbRecord.getStatus().name());
            
        } catch (Exception e) {
            log.error("خطا در اعتبارسنجی رکورد {}: {}", id, e.getMessage());
            return false;
        }
    }
    
    private long getElasticsearchCount(String indexName) throws IOException {
        CountRequest request = CountRequest.of(c -> c.index(indexName));
        CountResponse response = elasticsearchClient.count(request);
        return response.count();
    }
}
```

## مرحله 5: Switch Over

### HybridSearchService

```java
@Service
@Primary
@Slf4j
public class HybridSearchService implements SearchService {
    
    private final SearchService postgresqlSearchService;
    private final SearchService elasticsearchSearchService;
    
    @Value("${app.search.engine:postgresql}")
    private String searchEngine;
    
    @Value("${app.search.hybrid.percentage:0}")
    private Integer elasticsearchPercentage; // درصد ترافیک برای Elasticsearch
    
    public HybridSearchService(@Qualifier("postgresqlSearchService") SearchService postgresqlSearchService,
                              @Qualifier("elasticsearchSearchService") SearchService elasticsearchSearchService) {
        this.postgresqlSearchService = postgresqlSearchService;
        this.elasticsearchSearchService = elasticsearchSearchService;
    }
    
    @Override
    public SearchResultPage<AdSearchResult> searchAds(SearchCriteria criteria) {
        
        // انتخاب search engine بر اساس تنظیمات
        SearchService targetService = selectSearchService();
        
        long startTime = System.currentTimeMillis();
        String engineUsed = (targetService == elasticsearchSearchService) ? "elasticsearch" : "postgresql";
        
        try {
            SearchResultPage<AdSearchResult> result = targetService.searchAds(criteria);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("جستجو با {} انجام شد. {} نتیجه در {} میلی‌ثانیه", 
                engineUsed, result.getTotalCount(), duration);
            
            // ثبت متریک
            recordSearchMetric(engineUsed, duration, result.getTotalCount());
            
            return result;
            
        } catch (Exception e) {
            log.error("خطا در جستجو با {}: {}", engineUsed, e.getMessage(), e);
            
            // Fallback به PostgreSQL در صورت خطا
            if (targetService == elasticsearchSearchService) {
                log.info("Fallback به PostgreSQL");
                return postgresqlSearchService.searchAds(criteria);
            }
            
            throw e;
        }
    }
    
    private SearchService selectSearchService() {
        if ("elasticsearch".equals(searchEngine)) {
            return elasticsearchSearchService;
        }
        
        if ("postgresql".equals(searchEngine)) {
            return postgresqlSearchService;
        }
        
        // Hybrid mode - تقسیم ترافیک
        if ("hybrid".equals(searchEngine)) {
            Random random = new Random();
            int randomValue = random.nextInt(100);
            
            if (randomValue < elasticsearchPercentage) {
                return elasticsearchSearchService;
            } else {
                return postgresqlSearchService;
            }
        }
        
        // پیش‌فرض
        return postgresqlSearchService;
    }
    
    private void recordSearchMetric(String engine, long duration, long resultCount) {
        // ثبت متریک در سیستم monitoring
        MeterRegistry.counter("search.requests", "engine", engine).increment();
        MeterRegistry.timer("search.duration", "engine", engine).record(duration, TimeUnit.MILLISECONDS);
        MeterRegistry.counter("search.results", "engine", engine).increment(resultCount);
    }
}
```

## مرحله 6: Performance Comparison

### PerformanceComparisonService

```java
@Service
@Slf4j
public class PerformanceComparisonService {
    
    private final SearchService postgresqlSearchService;
    private final SearchService elasticsearchSearchService;
    
    @Async
    public CompletableFuture<ComparisonResult> comparePerformance(List<SearchCriteria> testCases) {
        log.info("شروع مقایسه عملکرد بین PostgreSQL و Elasticsearch");
        
        List<TestResult> postgresqlResults = new ArrayList<>();
        List<TestResult> elasticsearchResults = new ArrayList<>();
        
        for (SearchCriteria criteria : testCases) {
            // تست PostgreSQL
            TestResult pgResult = runTest("PostgreSQL", () -> 
                postgresqlSearchService.searchAds(criteria));
            postgresqlResults.add(pgResult);
            
            // تست Elasticsearch
            TestResult esResult = runTest("Elasticsearch", () -> 
                elasticsearchSearchService.searchAds(criteria));
            elasticsearchResults.add(esResult);
            
            // استراحت بین تست‌ها
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        ComparisonResult comparison = ComparisonResult.builder()
            .postgresqlResults(postgresqlResults)
            .elasticsearchResults(elasticsearchResults)
            .postgresqlAvgTime(calculateAverageTime(postgresqlResults))
            .elasticsearchAvgTime(calculateAverageTime(elasticsearchResults))
            .build();
        
        double improvement = ((comparison.getPostgresqlAvgTime() - comparison.getElasticsearchAvgTime()) 
                             / comparison.getPostgresqlAvgTime()) * 100;
        comparison.setPerformanceImprovement(improvement);
        
        log.info("مقایسه عملکرد کامل شد: {}", comparison);
        return CompletableFuture.completedFuture(comparison);
    }
    
    private TestResult runTest(String engine, Supplier<SearchResultPage<AdSearchResult>> searchFunction) {
        long startTime = System.currentTimeMillis();
        
        try {
            SearchResultPage<AdSearchResult> result = searchFunction.get();
            long duration = System.currentTimeMillis() - startTime;
            
            return TestResult.builder()
                .engine(engine)
                .duration(duration)
                .resultCount(result.getTotalCount())
                .success(true)
                .build();
                
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            log.error("خطا در تست {}: {}", engine, e.getMessage());
            
            return TestResult.builder()
                .engine(engine)
                .duration(duration)
                .success(false)
                .error(e.getMessage())
                .build();
        }
    }
    
    private double calculateAverageTime(List<TestResult> results) {
        return results.stream()
            .filter(TestResult::isSuccess)
            .mapToLong(TestResult::getDuration)
            .average()
            .orElse(0.0);
    }
}
```

## مرحله 7: Production Deployment

### Deployment Checklist

```yaml
# deployment-checklist.yml
production_deployment:
  prerequisites:
    - verify_elasticsearch_cluster_health: true
    - backup_postgresql_data: true
    - test_migration_on_staging: true
    - prepare_rollback_plan: true
    
  migration_steps:
    1_setup_elasticsearch:
      - install_elasticsearch_cluster
      - configure_security
      - setup_monitoring
      
    2_data_migration:
      - run_migration_service
      - verify_data_integrity
      - setup_real_time_sync
      
    3_traffic_switch:
      - enable_hybrid_mode: 10% # شروع با 10% ترافیک
      - monitor_performance
      - gradually_increase_traffic: [25%, 50%, 75%, 100%]
      
    4_cleanup:
      - disable_postgresql_search_indexes # پس از تایید
      - archive_old_data
      - update_documentation
      
  monitoring:
    metrics:
      - search_response_time
      - error_rate
      - throughput
      - resource_usage
      
    alerts:
      - response_time_threshold: 2000ms
      - error_rate_threshold: 1%
      - elasticsearch_cluster_health: yellow
      
  rollback_triggers:
    - error_rate > 5%
    - response_time > 5000ms
    - elasticsearch_cluster_down
    - data_inconsistency_detected
```

### Monitoring Dashboard

```java
@Component
@Slf4j
public class SearchMonitoringService {
    
    private final MeterRegistry meterRegistry;
    
    @EventListener
    public void handleSearchEvent(SearchEvent event) {
        // Response time
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("search.response.time")
            .tag("engine", event.getEngine())
            .tag("type", event.getSearchType())
            .register(meterRegistry));
        
        // Result count
        Counter.builder("search.results.count")
            .tag("engine", event.getEngine())
            .register(meterRegistry)
            .increment(event.getResultCount());
        
        // Success/Error rate
        Counter.builder("search.requests")
            .tag("engine", event.getEngine())
            .tag("status", event.isSuccess() ? "success" : "error")
            .register(meterRegistry)
            .increment();
    }
    
    @Scheduled(fixedRate = 30000)
    public void reportHealthMetrics() {
        // Elasticsearch cluster health
        try {
            // Check ES health and report
            reportElasticsearchHealth();
        } catch (Exception e) {
            log.error("خطا در بررسی سلامت Elasticsearch: {}", e.getMessage());
        }
        
        // PostgreSQL health
        try {
            reportPostgresqlHealth();
        } catch (Exception e) {
            log.error("خطا در بررسی سلامت PostgreSQL: {}", e.getMessage());
        }
    }
}
```

## نتیجه‌گیری

مهاجرت از PostgreSQL به Elasticsearch یک فرآیند پیچیده است که نیاز به برنامه‌ریزی دقیق دارد. نکات کلیدی:

### ✅ مزایای Elasticsearch
- عملکرد بهتر در حجم‌های بالا
- قابلیت‌های جستجوی پیشرفته‌تر
- مقیاس‌پذیری بهتر
- ویژگی‌های analytics قوی‌تر

### ⚠️ چالش‌های احتمالی
- پیچیدگی بیشتر infrastructure
- نیاز به expertise اضافی
- هزینه‌های operational بالاتر
- نیاز به monitoring بیشتر

### 📊 توصیه
مهاجرت را تنها زمانی انجام دهید که:
1. حجم داده‌ها یا ترافیک PostgreSQL را به چالش بکشد
2. نیاز به ویژگی‌های پیشرفته جستجو داشته باشید
3. تیم technical آمادگی نگهداری Elasticsearch را داشته باشد
4. Budget کافی برای infrastructure و operational costs در نظر گرفته شده باشد

با پیروی از این راهنما، مهاجرت با حداقل ریسک و حداکثر کارایی انجام خواهد شد.
